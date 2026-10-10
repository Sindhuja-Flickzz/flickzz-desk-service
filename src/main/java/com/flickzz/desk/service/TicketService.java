package com.flickzz.desk.service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.*;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.service.notification.NotificationService;
import com.flickzz.desk.vo.*;
import com.flickzz.desk.vo.request.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

@Service
public class TicketService {

    private static final Logger log = LoggerFactory.getLogger(TicketService.class);
    private static final String RITM_REQUEST_TYPE = "RITM";
    @Autowired
    CommonMapper mapper;
    @Autowired
    private TicketTypeMasterRepository ticketTypeMasterRepository;
    @Value("${ritm.attachment.base-path}")
    private String ritmAttachmentBasePath;
    @Value("${ritm.storage.type}")
    private String ritmStorageType;
    @Autowired
    private TicketMasterRepository ticketMasterRepository;
    @Autowired
    private TicketApproverRepository ticketApproverRepository;
    @Autowired
    private TicketApproverRemarkRepository ticketApproverRemarkRepository;
    @Autowired
    private ApprovalRepository approvalRepository;
    @Autowired
    private RequestApproverConfigRepository requestApproverConfigRepository;
    @Autowired
    private RequestApproverRepository requestApproverRepository;
    @Autowired
    private CompanyMasterRepository companyMasterRepository;
    @Autowired
    private AgentMasterRepository agentMasterRepository;
    @Autowired
    private BPCategoryRepository categoryRepository;
    @Autowired
    private BPSubCategoryRepository subCategoryRepository;
    @Autowired
    private BPSupportGroupRepository supportGroupRepository;
    @Autowired
    private BPPriorityRepository priorityRepository;
    @Autowired
    private BPSlaRepository bpSlaRepository;
    @Autowired
    private TicketAttachmentRepository ticketAttachmentRepository;
    @Autowired
    private TicketWatchlistRepository ticketWatchlistRepository;
    @Autowired
    private TicketCommentRepository ticketCommentRepository;
    @Autowired
    private TicketAuditRepository ticketAuditRepository;
    @Autowired
    private TicketAuditDetailRepository ticketAuditDetailRepository;
    @Autowired
    private BPSupportGroupManagerRepository supportGroupManagerRepository;
    @Autowired
    private BPSupportGroupMemberRepository supportGroupMemberRepository;
    @Autowired
    private RequestConfigRepository requestConfigRepository;
    @Autowired
    private RequestTypeMasterRepository requestTypeMasterRepository;
    @Autowired
    private TicketFieldValueRepository ticketFieldValueRepository;
    @Autowired
    private TemplateFieldRepository templateFieldRepository;
    @Autowired
    private StatusMasterRepository statusMasterRepository;
    @Autowired
    private StatusVisibilityRepository statusVisibilityRepository;
    @Autowired
    private WorkItemRepository workItemRepository;
    @Autowired
    private AuditService auditService;
    @Autowired
    private NotificationService notificationService;

    public List<TicketTypeMasterVO> getTicketTypeMasterList() {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            return ticketTypeMasterRepository.findByIsActiveTrue().stream()
                    .map(ticketType -> mapper.toTicketTypeMasterVo(ticketType)).toList();
        } catch (Exception e) {
            log.error("Exception in getTicketTypeMasterList method in TicketService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    @Transactional
    public List<TicketApproverVO> createRitmApprovers(RitmApproverRequestVO request) {
        validateRitmApproverRequest(request);
        TicketMaster ritm = findRitmForApproverRequest(request.getRitmId(), request.getCompanyId());
        if (!ticketApproverRepository.findByTicket_TicketIdAndIsActiveTrueOrderByApproverSequenceAsc(request.getRitmId()).isEmpty()) {
            throw new FlickzzDeskException(ALREADY_EXISTS,
                    getDescription(ALREADY_EXISTS.getDescription(), "RITM approvers"));
        }
        List<AgentMaster> targets = resolveRitmApproverTargets(request);
        boolean followSequence = Boolean.TRUE.equals(request.getIsGroupApprover())
                && isRitmApproverSequenceEnabled(request.getApproverConfigId());
        List<TicketApprover> saved = saveRitmApprovers(ritm, request, targets, followSequence);
        List<AgentMaster> notificationTargets = followSequence
                ? saved.stream().filter(approver -> Objects.equals(approver.getApproverSequence(), 1))
                .map(TicketApprover::getApproverAgent).toList()
                : targets;
        notificationService.notifyRitmApproverAssignment(ritm, request.getAssignedBy(),
                request.getIsCreatorAdmin(), notificationTargets, "CREATE");
        recordRitmApproverAudit(ritm, request, CREATE, null, saved);
        return mapper.toRitmApproverVOList(saved);
    }

    private void validateRitmApproverRequest(RitmApproverRequestVO request) {
        if (request == null || request.getRitmId() == null || request.getCompanyId() == null
                || request.getAssignedBy() == null || request.getIsGroupApprover() == null
                || request.getIsCreatorAdmin() == null
                || (Boolean.TRUE.equals(request.getIsGroupApprover())
                ? request.getApproverConfigId() == null || request.getApproverIds() != null
                : request.getApproverConfigId() != null || request.getApproverIds() == null || request.getApproverIds().isEmpty())) {
            throw new FlickzzDeskException(INVALID_REQUEST,
                    getDescription(INVALID_REQUEST.getDescription(), "Invalid RITM approver data"));
        }
        if (request.getReason() != null && request.getReason().length() > 2000) {
            throw new FlickzzDeskException(INVALID_FIELD,
                    getDescription(INVALID_FIELD.getDescription(), "Reason cannot exceed 2000 characters"));
        }
        if (request.getApproverIds() != null && (request.getApproverIds().contains(null)
                || new HashSet<>(request.getApproverIds()).size() != request.getApproverIds().size())) {
            throw new FlickzzDeskException(INVALID_FIELD,
                    getDescription(INVALID_FIELD.getDescription(), "Approver IDs must be unique"));
        }
    }

    private TicketMaster findRitmForApproverRequest(Long ritmId, Long companyId) {
        TicketMaster ritm = ticketMasterRepository.findById(ritmId)
                .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "RITM with ID " + ritmId)));
        if (ritm.getCompany() == null || !companyId.equals(ritm.getCompany().getCompanyId())) {
            throw new FlickzzDeskException(INVALID_FIELD,
                    getDescription(INVALID_FIELD.getDescription(), "RITM belongs to a different company"));
        }
        return ritm;
    }

    private List<AgentMaster> resolveRitmApproverTargets(RitmApproverRequestVO request) {
        List<AgentMaster> targets;
        if (Boolean.TRUE.equals(request.getIsGroupApprover())) {
            RequestApproverConfig config = requestApproverConfigRepository.findById(request.getApproverConfigId())
                    .filter(value -> Boolean.TRUE.equals(value.getIsActive()))
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Active approver config " + request.getApproverConfigId())));
            if (config.getCompany() == null || !request.getCompanyId().equals(config.getCompany().getCompanyId())) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Approver config belongs to a different company"));
            }
            targets = new ArrayList<>();
            requestApproverRepository
                    .findByApproverConfig_ApproverConfigIdAndIsActiveTrueOrderByApproverSequenceAsc(config.getApproverConfigId())
                    .forEach(entry -> targets.add(entry.getAgent()));
        } else {
            targets = request.getApproverIds().stream().map(agentId -> agentMasterRepository.findById(agentId)
                            .filter(agent -> Boolean.TRUE.equals(agent.getIsActive()))
                            .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                    getDescription(DOES_NOT_EXIST.getDescription(), "Active agent with ID " + agentId))))
                    .toList();
        }
        if (targets.isEmpty()) {
            throw new FlickzzDeskException(INVALID_REQUEST,
                    getDescription(INVALID_REQUEST.getDescription(), "At least one active approver is required"));
        }
        for (AgentMaster agent : targets) {
            if (!Boolean.TRUE.equals(agent.getIsActive())) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Approver agent is inactive"));
            }
            if (agent.getOrganization() == null || !request.getCompanyId().equals(agent.getOrganization().getCompanyId())) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Approver belongs to a different company"));
            }
        }
        return targets;
    }

    private boolean isRitmApproverSequenceEnabled(Long approverConfigId) {
        return requestApproverConfigRepository.findById(approverConfigId)
                .map(config -> Boolean.TRUE.equals(config.getFollowSequence()))
                .orElse(false);
    }

    private List<TicketApprover> saveRitmApprovers(TicketMaster ritm, RitmApproverRequestVO request,
                                                   List<AgentMaster> targets, boolean firstLevelOnly) {
        RequestApproverConfig config = Boolean.TRUE.equals(request.getIsGroupApprover())
                ? requestApproverConfigRepository.findById(request.getApproverConfigId()).orElseThrow() : null;
        Map<Long, Integer> groupSequences = new HashMap<>();
        if (config != null) {
            requestApproverRepository.findByApproverConfig_ApproverConfigIdAndIsActiveTrueOrderByApproverSequenceAsc(config.getApproverConfigId())
                    .forEach(entry -> groupSequences.put(entry.getAgent().getAgentId(), entry.getApproverSequence()));
        }
        List<TicketApprover> approvers = new ArrayList<>();
        List<TicketApproverRemark> remarks = new ArrayList<>();
        for (int index = 0; index < targets.size(); index++) {
            AgentMaster agent = targets.get(index);
            int sequence = config == null ? index + 1 : groupSequences.getOrDefault(agent.getAgentId(), index + 1);
            TicketApprover approver = new TicketApprover();
            approver.setTicket(ritm);
            approver.setIsGroupApprover(Boolean.TRUE.equals(request.getIsGroupApprover()));
            approver.setApproverConfig(config);
            approver.setApproverAgent(agent);
            approver.setApproverSequence(sequence);
            approver.setIsMainApprover(sequence == 1);
            approver.setApprovalStatus(PENDING);
            approver.setCreatedBy(request.getAssignedBy());
            approver.setIsActive(true);
            TicketApproverRemark remark = new TicketApproverRemark();
            remark.setTicketApprover(approver);
            remark.setTicket(ritm);
            remark.setRemarkType(CREATE);
            remark.setRemark(request.getReason());
            remark.setCreatedBy(request.getAssignedBy());
            remarks.add(remark);
            approvers.add(approver);
        }
        List<TicketApprover> savedApprovers = ticketApproverRepository.saveAllAndFlush(approvers);
        ticketApproverRemarkRepository.saveAllAndFlush(remarks);
        remarks.forEach(remark -> remark.getTicketApprover().setRemark(List.of(remark)));
        List<ApprovalMaster> approvals = savedApprovers.stream()
                .filter(approver -> !firstLevelOnly || Objects.equals(approver.getApproverSequence(), 1))
                .map(approver -> ApprovalMaster.builder()
                        .requestId(approver.getTicketApproverId())
                        .requestType(RITM_REQUEST_TYPE)
                        .approvalType(DRAFTED)
                        .description(limitApprovalDescription(request.getReason(), ritm))
                        .approverType(Boolean.TRUE.equals(approver.getIsGroupApprover()) ? "GROUP" : "INDIVIDUAL")
                        .approverLevel(approver.getApproverSequence())
                        .approverUserId(approver.getApproverAgent().getUser().getUserId())
                        .approverOrgId(approver.getApproverAgent().getOrganization().getCompanyId())
                        .status(approver.getApprovalStatus())
                        .mandatory(approver.getIsMainApprover())
                        .approvedOn(approver.getApprovedOn())
                        .createdBy(approver.getCreatedBy())
                        .createdOn(approver.getCreatedOn())
                        .updatedBy(approver.getUpdatedBy())
                        .updatedOn(approver.getUpdatedOn())
                        .build())
                .toList();
        List<ApprovalMaster> savedApprovals = approvalRepository.saveAllAndFlush(approvals);
        for (ApprovalMaster approval : savedApprovals) {
            auditService.recordAudit(SystemAuditRequest.builder().module(RITM_REQUEST_TYPE).area("RITM Approval")
                    .entityName("ApprovalMaster").entityId(approval.getApprovalId()).action(CREATE)
                    .newValue("{\"ritmApproverId\":" + approval.getRequestId()
                            + ",\"approverUserId\":" + approval.getApproverUserId()
                            + ",\"approverLevel\":" + approval.getApproverLevel()
                            + ",\"status\":\"" + approval.getStatus() + "\",\"active\":true}")
                    .userId(request.getAssignedBy()).companyId(ritm.getCompany().getCompanyId())
                    .status(SUCCESS).build());
        }
        return savedApprovers;
    }

    private void recordRitmApproverAudit(TicketMaster ritm, RitmApproverRequestVO request, String action,
                                         String oldValue, List<TicketApprover> approvers) {
        auditService.recordAudit(SystemAuditRequest.builder().module(RITM_REQUEST_TYPE).area("RITM Approver")
                .entityName("RitmApprover").entityId(ritm.getTicketId()).action(action)
                .oldValue(oldValue).newValue(ritmApproverSnapshot(approvers))
                .userId(request.getAssignedBy()).companyId(request.getCompanyId()).status(SUCCESS).build());
    }

    private String limitApprovalDescription(String description, TicketMaster ritm) {
        String value = description == null || description.isBlank()
                ? "Approval for RITM " + ritm.getTicketNumber()
                : description;
        return value.length() > 200 ? value.substring(0, 200) : value;
    }

    private String ritmApproverSnapshot(List<TicketApprover> approvers) {
        StringBuilder snapshot = new StringBuilder("{\"approvers\":[");
        for (int index = 0; index < approvers.size(); index++) {
            TicketApprover approver = approvers.get(index);
            if (index > 0) snapshot.append(',');
            snapshot.append("{\"agentId\":").append(approver.getApproverAgent().getAgentId())
                    .append(",\"ritmApproverId\":").append(approver.getTicketApproverId())
                    .append(",\"approverConfigId\":")
                    .append(approver.getApproverConfig() == null ? "null" : approver.getApproverConfig().getApproverConfigId())
                    .append(",\"sequence\":").append(approver.getApproverSequence())
                    .append(",\"status\":\"").append(approver.getApprovalStatus()).append('"')
                    .append(",\"active\":").append(approver.getIsActive())
                    .append('}');
        }
        return snapshot.append("]}").toString();
    }

    @Transactional
    public List<TicketApproverVO> updateRitmApprovers(RitmApproverRequestVO request) {
        validateRitmApproverRequest(request);
        TicketMaster ritm = findRitmForApproverRequest(request.getRitmId(), request.getCompanyId());
        List<TicketApprover> current = ticketApproverRepository
                .findByTicket_TicketIdAndIsActiveTrueOrderByApproverSequenceAsc(request.getRitmId());
        if (current.isEmpty()) {
            throw new FlickzzDeskException(DOES_NOT_EXIST,
                    getDescription(DOES_NOT_EXIST.getDescription(), "RITM approvers for RITM " + request.getRitmId()));
        }
        List<AgentMaster> targets = resolveRitmApproverTargets(request);
        Set<Long> affectedAgentIds = new LinkedHashSet<>();
        current.forEach(approver -> affectedAgentIds.add(approver.getApproverAgent().getAgentId()));
        targets.forEach(agent -> affectedAgentIds.add(agent.getAgentId()));
        String oldValue = ritmApproverSnapshot(current);
        current.forEach(approver -> approver.setIsActive(false));
        ticketApproverRepository.saveAll(current);
        List<Long> currentApproverIds = current.stream()
                .map(TicketApprover::getTicketApproverId)
                .toList();
        List<ApprovalMaster> currentApprovals = approvalRepository
                .findByRequestTypeAndRequestIdIn(RITM_REQUEST_TYPE, currentApproverIds);
        LocalDateTime updatedOn = LocalDateTime.now();
        currentApprovals.forEach(approval -> {
            approval.setActive(false);
            approval.setUpdatedBy(request.getAssignedBy());
            approval.setUpdatedOn(updatedOn);
        });
        approvalRepository.saveAll(currentApprovals);
        List<TicketApprover> saved = saveRitmApprovers(ritm, request, targets, false);
        notificationService.notifyRitmApproverAssignment(ritm, request.getAssignedBy(),
                request.getIsCreatorAdmin(), findAgents(affectedAgentIds), "UPDATE");
        recordRitmApproverAudit(ritm, request, UPDATE, oldValue, saved);
        return mapper.toRitmApproverVOList(saved);
    }

    private List<AgentMaster> findAgents(Set<Long> agentIds) {
        List<AgentMaster> agents = new ArrayList<>();
        for (Long agentId : agentIds) {
            Optional<AgentMaster> agent = agentMasterRepository.findById(agentId);
            agent.ifPresent(agents::add);
        }
        return agents;
    }

    @Transactional(readOnly = true)
    public List<TicketApproverVO> getRitmApprovers(Long ritmId, Long companyId) {
        TicketMaster ritm = findRitmForApproverRequest(ritmId, companyId);
        return mapper.toRitmApproverVOList(ticketApproverRepository
                .findByTicket_TicketIdAndIsActiveTrueOrderByApproverSequenceAsc(ritm.getTicketId()));
    }

    @Transactional(readOnly = true)
    public TicketApproverVO getRitmApproverById(Long ritmApproverId) {
        if (ritmApproverId == null) {
            throw new FlickzzDeskException(INVALID_FIELD,
                    getDescription(INVALID_FIELD.getDescription(), "RITM approver ID"));
        }

        TicketApprover approver = ticketApproverRepository.findById(ritmApproverId)
                .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "RITM approver")));
        TicketMaster ritm = ticketMasterRepository.findById(approver.getTicket().getTicketId())
                .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), RITM_REQUEST_TYPE)));

        List<TicketTemplateDetailVO> templateDetails = ritm.getFieldValues() == null ? null : ritm.getFieldValues().stream()
                .filter(value -> Boolean.TRUE.equals(value.getIsActive())
                        && Boolean.TRUE.equals(value.getTemplateField().getMandatory()))
                .map(value -> TicketTemplateDetailVO.builder()
                        .fieldId(value.getTemplateField().getFieldId())
                        .fieldName(value.getTemplateField().getFieldName())
                        .value(value.getFieldValue())
                        .build())
                .toList();

        TicketApproverVO response = mapper.toRitmApproverVO(approver);
        response.setTemplateDetails(templateDetails);
        return response;
    }

    @Transactional
    public void deleteRitmApprovers(Long ritmId, Long companyId, Long deletedBy, Boolean isDeleterAdmin) {
        if (ritmId == null || companyId == null || deletedBy == null || isDeleterAdmin == null) {
            throw new FlickzzDeskException(INVALID_REQUEST,
                    getDescription(INVALID_REQUEST.getDescription(), "RITM ID, company, assigned by and admin flag are required"));
        }
        TicketMaster ritm = findRitmForApproverRequest(ritmId, companyId);
        List<TicketApprover> current = ticketApproverRepository
                .findByTicket_TicketIdAndIsActiveTrueOrderByApproverSequenceAsc(ritmId);
        if (current.isEmpty()) {
            throw new FlickzzDeskException(DOES_NOT_EXIST,
                    getDescription(DOES_NOT_EXIST.getDescription(), "RITM approvers for RITM " + ritmId));
        }
        RitmApproverRequestVO request = new RitmApproverRequestVO(ritmId, companyId, null, deletedBy,
                false, null, List.of(), isDeleterAdmin);
        String oldValue = ritmApproverSnapshot(current);
        current.forEach(approver -> approver.setIsActive(false));
        ticketApproverRepository.saveAll(current);
        List<Long> currentApproverIds = current.stream()
                .map(TicketApprover::getTicketApproverId)
                .toList();
        List<ApprovalMaster> currentApprovals = approvalRepository
                .findByRequestTypeAndRequestIdIn(RITM_REQUEST_TYPE, currentApproverIds);
        LocalDateTime updatedOn = LocalDateTime.now();
        currentApprovals.forEach(approval -> {
            approval.setActive(false);
            approval.setUpdatedBy(deletedBy);
            approval.setUpdatedOn(updatedOn);
        });
        approvalRepository.saveAll(currentApprovals);
        List<AgentMaster> affected = new ArrayList<>();
        for (TicketApprover approver : current) {
            affected.add(approver.getApproverAgent());
        }
        notificationService.notifyRitmApproverAssignment(ritm, request.getAssignedBy(),
                request.getIsCreatorAdmin(), affected, "DELETE");
        recordRitmApproverAudit(ritm, request, DELETE, oldValue, List.of());
    }

    @Transactional
    public List<RequestTypeMasterVO> createRitmRequestTypes(List<RitmRequestTypeRequestVO> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new FlickzzDeskException(INVALID_REQUEST,
                    getDescription(INVALID_REQUEST.getDescription(), "At least one RITM request type is required"));
        }

        List<RequestTypeMasterVO> response = new ArrayList<>();
        for (RitmRequestTypeRequestVO request : requests) {
            try {
                validateRequestTypeRequest(request);
                CompanyMaster company = companyMasterRepository.findByCompanyIdAndIsActiveTrue(request.getCompanyId())
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), "Company with ID " + request.getCompanyId())));

                RequestTypeMaster requestType = requestTypeMasterRepository
                        .findByRequestTypeNameAndCompany_CompanyId(request.getRequestTypeName().trim(), request.getCompanyId())
                        .orElse(null);
                if (requestType != null && Boolean.TRUE.equals(requestType.getIsActive())) {
                    throw new FlickzzDeskException(ALREADY_EXISTS,
                            getDescription(ALREADY_EXISTS.getDescription(), "RITM request type"));
                }
                if (requestType == null) {
                    requestType = RequestTypeMaster.builder().requestTypeName(request.getRequestTypeName().trim())
                            .company(company).createdBy(request.getCreatedBy())
                            .isCreatorAdmin(Boolean.TRUE.equals(request.getIsCreatedByAdmin())).isActive(true)
                            .isUpdaterAdmin(false).createdAt(LocalDateTime.now()).build();
                } else {
                    requestType.setIsActive(true);
                    requestType.setUpdatedBy(request.getCreatedBy());
                    requestType.setIsUpdaterAdmin(Boolean.TRUE.equals(request.getIsCreatedByAdmin()));
                    requestType.setUpdatedAt(LocalDateTime.now());
                }
                requestType.setCreatedAt(requestType.getCreatedAt() == null ? LocalDateTime.now() : requestType.getCreatedAt());
                RequestTypeMaster saved = requestTypeMasterRepository.saveAndFlush(requestType);
                recordRequestTypeAudit(saved, CREATE, null, request.getCreatedBy(), request.getIsCreatedByAdmin(), SUCCESS, null);
                response.add(mapper.toRequestTypeMasterVO(saved));
            } catch (FlickzzDeskException e) {
                recordRequestTypeAudit(null, CREATE, null, request != null ? request.getCreatedBy() : null,
                        request != null ? request.getIsCreatedByAdmin() : null, FAILED, e.getDescription());
                throw e;
            } catch (Exception e) {
                recordRequestTypeAudit(null, CREATE, null, request != null ? request.getCreatedBy() : null,
                        request != null ? request.getIsCreatedByAdmin() : null, FAILED, e.getMessage());
                log.error("Exception in createRitmRequestTypes method", e);
                throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
            }
        }
        return response;
    }

    private void validateRequestTypeRequest(RitmRequestTypeRequestVO request) {
        if (request == null || request.getCompanyId() == null || request.getCreatedBy() == null
                || request.getIsCreatedByAdmin() == null || request.getRequestTypeName() == null
                || request.getRequestTypeName().isBlank()) {
            throw new FlickzzDeskException(INVALID_REQUEST,
                    getDescription(INVALID_REQUEST.getDescription(), "RITM request type data is invalid"));
        }
    }

    private void recordRequestTypeAudit(RequestTypeMaster requestType, String action, String oldValue,
                                        Long userId, Boolean isAdmin, String status, String errorMessage) {
        auditService.recordAudit(mapper.toSystemAuditRequest(RITM_REQUEST_TYPE, "Request Type", "RequestTypeMaster",
                requestType != null ? requestType.getRequestTypeId() : null, action,
                requestType != null ? "{\"requestType\":\"" + requestType.getRequestTypeName()
                        + "\",\"isActive\":" + requestType.getIsActive() + "}" : null,
                oldValue, null, userId, null,
                requestType != null && requestType.getCompany() != null ? requestType.getCompany().getCompanyId() : null,
                status, errorMessage));
    }

    @Transactional
    public void deleteRitmRequestType(Long requestTypeId, Long deletedBy, Boolean isDeletedByAdmin) {
        RequestTypeMaster requestType = requestTypeMasterRepository.findById(requestTypeId)
                .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "RITM request type with ID " + requestTypeId)));
        String oldValue = "{\"requestType\":\"" + requestType.getRequestTypeName() + "\",\"isActive\":"
                + requestType.getIsActive() + "}";
        requestTypeMasterRepository.delete(requestType);
        recordRequestTypeAudit(requestType, DELETE, oldValue, deletedBy, isDeletedByAdmin, SUCCESS, null);
    }

    @Transactional(readOnly = true)
    public List<RequestTypeMasterVO> getRitmRequestTypes(Long companyId) {
        return mapper.toRequestTypeMasterVOList(
                requestTypeMasterRepository.findByCompany_CompanyIdAndIsActiveTrueOrderByRequestTypeNameAsc(companyId));
    }

    @Transactional
    public TicketMasterVO createRitm(RitmRequestVO ritmVO, List<MultipartFile> files) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (ritmVO == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM request data is required"));
            }

            if (ritmVO.getOrgId() == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Organization ID is required"));
            }

            CompanyMaster company = companyMasterRepository.findByCompanyIdAndIsActiveTrue(ritmVO.getOrgId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Company with ID " + ritmVO.getOrgId())));

            boolean isRitmSubtask = "RITM_SUBTASK".equalsIgnoreCase(ritmVO.getRequestType());
            TicketMaster parentRitm = null;
            if (isRitmSubtask) {
                if (ritmVO.getParentRitmId() == null) {
                    throw new FlickzzDeskException(INVALID_REQUEST,
                            getDescription(INVALID_REQUEST.getDescription(), "Parent RITM ID is required for an RITM subtask"));
                }
                parentRitm = ticketMasterRepository.findById(ritmVO.getParentRitmId())
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), "Parent RITM with ID " + ritmVO.getParentRitmId())));
                if (!Objects.equals(parentRitm.getCompany().getCompanyId(), ritmVO.getOrgId())) {
                    throw new FlickzzDeskException(INVALID_REQUEST,
                            getDescription(INVALID_REQUEST.getDescription(), "Parent RITM belongs to a different organization"));
                }
            }

            Long openedById = ritmVO.getOpenedBy() != null ? ritmVO.getOpenedBy() : ritmVO.getCreatedBy();
            if (openedById == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Opened by is required"));
            }
            AgentMaster openedBy = agentMasterRepository.findById(openedById)
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Agent with ID " + openedById)));

            Long requestedForId = ritmVO.getRequestedFor();
            if (requestedForId == null && parentRitm != null && parentRitm.getRequestedFor() != null) {
                requestedForId = parentRitm.getRequestedFor().getAgentId();
            }
            Long resolvedRequestedForId = requestedForId;
            if (resolvedRequestedForId == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Requested for is required"));
            }
            AgentMaster requestedFor = agentMasterRepository.findById(resolvedRequestedForId)
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Agent with ID " + resolvedRequestedForId)));

            Long supportGroupId = ritmVO.getAssignmentGroup() != null ? ritmVO.getAssignmentGroup() : ritmVO.getSupportGroup();
            if (supportGroupId == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Support group is required"));
            }
            BPSupportGroup supportGroup = supportGroupRepository.findById(supportGroupId)
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Support group with ID " + supportGroupId)));

            if (ritmVO.getCategory() == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Category is required"));
            }
            BPCategory category = categoryRepository.findByCategoryIdAndIsActive(ritmVO.getCategory(), true)
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Category with ID " + ritmVO.getCategory())));

            if (ritmVO.getSubCategory() == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Sub category is required"));
            }
            BPSubCategory subCategory = subCategoryRepository.findBySubCategoryIdAndIsActive(ritmVO.getSubCategory(), true)
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Sub category with ID " + ritmVO.getSubCategory())));

            if (ritmVO.getPriority() == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Priority is required"));
            }
            BPPriority priority = priorityRepository.findByPriorityIdAndIsActive(ritmVO.getPriority(), true)
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Priority with ID " + ritmVO.getPriority())));

            if (ritmVO.getRequestTypeId() == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Request type is required"));
            }

            RequestTypeMaster requestType = requestTypeMasterRepository.findById(ritmVO.getRequestTypeId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Request type " + ritmVO.getRequestType() + " for org " + ritmVO.getOrgId())));

            if (requestType == null) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Request type with ID " + ritmVO.getRequestTypeId() + " does not exist"));
            }

            WorkItem workItem = workItemRepository.findByCodeAndCompany_CompanyIdAndIsActiveTrue(ritmVO.getRequestType(), ritmVO.getOrgId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Work item with code " + ritmVO.getRequestType() + " does not exist")));

            String ticketNumber = generateFreshRitmNumber(ritmVO.getOrgId(), ritmVO.getRequestType(), ritmVO.getRitmNumber());
            AgentMaster assignedTo = ritmVO.getAssignedTo() == null ? null : agentMasterRepository.findById(ritmVO.getAssignedTo())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Agent with ID " + ritmVO.getAssignedTo())));
            Long createdBy = ritmVO.getCreatedBy() != null ? ritmVO.getCreatedBy() : openedById;
            Long updatedBy = ritmVO.getUpdatedBy() != null ? ritmVO.getUpdatedBy() : createdBy;
            StatusMaster status = statusMasterRepository.findFirstByCompanyCompanyIdAndIsActiveTrueOrderBySequenceNoAsc(ritmVO.getOrgId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "No active RITM status found for org " + ritmVO.getOrgId())));
            LocalDateTime customerResolutionDate = calculateCustomerResolutionDate(priority.getPriorityId(), LocalDateTime.now());

            TicketMaster ritm = TicketMaster.builder()
                    .ticketNumber(ticketNumber)
                    .company(company)
                    .requestedBy(openedBy)
                    .requestedFor(requestedFor)
                    .assignedTo(assignedTo)
                    .category(category)
                    .subCategory(subCategory)
                    .supportGroup(supportGroup)
                    .requestType(requestType)
                    .priority(priority)
                    .workItem(workItem)
                    .status(status)
                    .ticketReference(parentRitm)
                    .requestedAt(LocalDateTime.now())
                    .customerResolutionDate(customerResolutionDate != null ? customerResolutionDate : ritmVO.getCustomerResolution())
                    .dueDate(ritmVO.getDueDate())
                    .resolvedAt(ritmVO.getResolvedAt())
                    .closedAt(ritmVO.getClosedAt())
                    .cancelledAt(ritmVO.getCancelledAt())
                    .actionReason(ritmVO.getActionReason())
                    .isActive(true)
                    .createdBy(createdBy)
                    .updatedBy(updatedBy)
                    .isCreatorAdmin(Boolean.TRUE.equals(ritmVO.getIsCreatorAdmin()))
                    .isUpdaterAdmin(Boolean.TRUE.equals(ritmVO.getIsUpdaterAdmin()))
                    .build();

            TicketMaster savedRitm = ticketMasterRepository.saveAndFlush(ritm);

            List<TicketAttachment> attachmentEntities = saveRitmFiles(savedRitm, files, createdBy);
            if (!attachmentEntities.isEmpty()) {
                ticketAttachmentRepository.saveAllAndFlush(attachmentEntities);
            }

            List<TicketWatchlist> watchlistEntries = new ArrayList<>();
            Set<Long> uniqueWatchAgents = new LinkedHashSet<>();
            if (ritmVO.getWatchList() != null) {
                uniqueWatchAgents.addAll(ritmVO.getWatchList());
            }
            for (Long agentId : uniqueWatchAgents) {
                if (agentId == null) {
                    continue;
                }
                AgentMaster watchAgent = agentMasterRepository.findById(agentId)
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), "Watchlist agent with ID " + agentId)));
                watchlistEntries.add(TicketWatchlist.builder()
                        .ticket(savedRitm)
                        .watchedBy(watchAgent)
                        .isActive(true)
                        .build());
            }
            if (!watchlistEntries.isEmpty()) {
                ticketWatchlistRepository.saveAllAndFlush(watchlistEntries);
            }
            saveTemplateDetails(savedRitm, ritmVO, createdBy);

            TicketAudit ticketAudit = ticketAuditRepository.saveAndFlush(TicketAudit.builder()
                    .ticket(savedRitm)
                    .actionType("CREATE")
                    .description("RITM created successfully")
                    .changedBy(createdBy)
                    .build());

            recordSystemAudit("RitmMaster", savedRitm.getTicketId(), CREATE, null,
                    savedRitm.getTicketNumber(), createdBy, company.getCompanyId(), SUCCESS, null);

            createNotificationEntries(savedRitm, supportGroup, createdBy, openedBy, uniqueWatchAgents);

            log.info(generateLog(EXIT, this.getClass().getName()));

            savedRitm.setAttachment(attachmentEntities);
            savedRitm.setWatchlist(watchlistEntries);
            return mapper.toRitmMasterVo(savedRitm);
        } catch (FlickzzDeskException e) {
            recordSystemAudit("RitmMaster", ritmVO != null ? ritmVO.getRitmId() : null, CREATE, null,
                    null, ritmVO != null ? ritmVO.getCreatedBy() : null,
                    ritmVO != null ? ritmVO.getOrgId() : null, FAILED, e.getDescription());
            throw e;
        } catch (Exception e) {
            recordSystemAudit("RitmMaster", ritmVO != null ? ritmVO.getRitmId() : null, CREATE, null,
                    null, ritmVO != null ? ritmVO.getCreatedBy() : null,
                    ritmVO != null ? ritmVO.getOrgId() : null, FAILED, e.getMessage());
            log.error("Exception in createRitm method in RitmService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    private String generateFreshRitmNumber(Long orgId, String requestType, String providedRitmNumber) {
        if (providedRitmNumber != null && !providedRitmNumber.isBlank()) {
            log.info("Ignoring provided {} number {} and generating a fresh sequence number for org {}",
                    requestType, providedRitmNumber, orgId);
        }

        RequestConfig requestConfig = requestConfigRepository.findByRequestTypeAndCompany_CompanyIdAndIsActiveTrueAndIsEnabledTrue(requestType, orgId)
                .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Request config for " + requestType + " in org " + orgId)));

        if (!Boolean.TRUE.equals(requestConfig.getIsActive()) || !Boolean.TRUE.equals(requestConfig.getIsEnabled())) {
            throw new FlickzzDeskException(INACTIVE_ERROR,
                    getDescription(INACTIVE_ERROR.getDescription(), requestType + " request config is disabled"));
        }

        Integer nextRange = requestConfig.getCurrentRange() == null ? requestConfig.getRangeFrom() : requestConfig.getCurrentRange() + 1;
        if (nextRange > requestConfig.getRangeTo() || nextRange < requestConfig.getRangeFrom()) {
            throw new FlickzzDeskException(INVALID_REQUEST,
                    getDescription(INVALID_REQUEST.getDescription(), "No available " + requestType + " number range is left"));
        }

        requestConfig.setCurrentRange(nextRange);
        requestConfigRepository.saveAndFlush(requestConfig);
        return requestConfig.getRequestPrefix() + nextRange;
    }

    public LocalDateTime calculateCustomerResolutionDate(Long priorityId, LocalDateTime startTime) {
        Optional<BPSla> sla = bpSlaRepository
                .findFirstByPriorityPriorityIdAndIsActiveTrueOrderByVersionDesc(priorityId);
        if (sla.isEmpty() || sla.get().getResolutionTime() == null || sla.get().getResolutionTerm() == null) {
            return null;
        }

        long resolutionTime = sla.get().getResolutionTime();
        return switch (Character.toUpperCase(sla.get().getResolutionTerm())) {
            case 'D' -> startTime.plusDays(resolutionTime);
            case 'H' -> startTime.plusHours(resolutionTime);
            case 'M' -> startTime.plusMinutes(resolutionTime);
            default -> throw new FlickzzDeskException(INVALID_FIELD,
                    getDescription(INVALID_FIELD.getDescription(), "Unsupported SLA resolution term"));
        };
    }

    private List<TicketAttachment> saveRitmFiles(TicketMaster savedRitm, List<MultipartFile> files, Long createdBy) throws IOException {
        List<TicketAttachment> attachmentEntities = new ArrayList<>();
        if (files == null || files.isEmpty()) {
            return attachmentEntities;
        }

        Path baseDirectory = Paths.get(ritmAttachmentBasePath, String.valueOf(savedRitm.getTicketId()));
        Files.createDirectories(baseDirectory);

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }

            String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "attachment";
            String normalizedName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path destination = baseDirectory.resolve(normalizedName);
            file.transferTo(destination);

            String savedPath = destination.toAbsolutePath().toString().replace("\\", "/");
            attachmentEntities.add(TicketAttachment.builder()
                    .ticket(savedRitm)
                    .fileName(normalizedName)
                    .originalFileName(originalName)
                    .mimeType(file.getContentType())
                    .fileSize(file.getSize())
                    .storageType(ritmStorageType)
                    .storagePath(savedPath)
                    .fileHash(savedPath)
                    .isActive(true)
                    .uploadedBy(createdBy)
                    .build());
        }
        return attachmentEntities;
    }

    private void saveTemplateDetails(TicketMaster ritm, RitmRequestVO request, Long actorId) {
        List<TicketFieldValue> existingFieldValues = ticketFieldValueRepository.findAllByTicket_TicketId(ritm.getTicketId());
        if (ritm.getFieldValues() == null) {
            ritm.setFieldValues(new ArrayList<>());
        } else {
            ritm.getFieldValues().clear();
        }
        if (!existingFieldValues.isEmpty()) {
            ticketFieldValueRepository.deleteAll(existingFieldValues);
        }
        ticketFieldValueRepository.flush();
        if (request.getTemplateDetails() == null || request.getTemplateDetails().isEmpty()) {
            return;
        }

        List<TicketFieldValue> fieldValues = new ArrayList<>();
        Set<Long> fieldIds = new HashSet<>();
        for (com.flickzz.desk.vo.request.RitmTemplateDetailVO detail : request.getTemplateDetails()) {
            if (detail == null || detail.getFieldId() == null || !fieldIds.add(detail.getFieldId())) {
                continue;
            }
            TemplateField templateField = templateFieldRepository.findById(detail.getFieldId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Template field with ID " + detail.getFieldId())));
            if (!Boolean.TRUE.equals(templateField.getIsActive())
                    || templateField.getTemplate() == null
                    || templateField.getTemplate().getCompany() == null
                    || !Objects.equals(templateField.getTemplate().getCompany().getCompanyId(),
                    ritm.getCompany().getCompanyId())) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Template field does not belong to the RITM organization"));
            }
            fieldValues.add(TicketFieldValue.builder()
                    .ticket(ritm)
                    .templateField(templateField)
                    .fieldValue(detail.getValue())
                    .isActive(true)
                    .createdBy(actorId)
                    .updatedBy(actorId)
                    .isCreatorAdmin(Boolean.TRUE.equals(request.getIsCreatorAdmin()))
                    .isUpdaterAdmin(Boolean.TRUE.equals(request.getIsUpdaterAdmin()))
                    .build());
        }
        if (!fieldValues.isEmpty()) {
            ticketFieldValueRepository.saveAllAndFlush(fieldValues);
            ritm.getFieldValues().addAll(fieldValues);
        }
    }

    private void recordSystemAudit(String entityName, Long entityId, String action, String oldValue,
                                   String newValue, Long userId, Long companyId, String status,
                                   String errorMessage) {
        auditService.recordAudit(mapper.toSystemAuditRequest(
                RITM_REQUEST_TYPE, RITM_REQUEST_TYPE, entityName, entityId, action, newValue, oldValue,
                null, userId, null, companyId, status, errorMessage));
    }

    private void createNotificationEntries(TicketMaster savedRitm,
                                           BPSupportGroup supportGroup,
                                           Long createdBy,
                                           AgentMaster openedBy,
                                           Set<Long> watchAgents) {
        Set<Long> recipientIds = new LinkedHashSet<>();

        if (supportGroup != null) {
            List<BPSupportGroupManager> managers = supportGroupManagerRepository
                    .findBySupportGroupSupportGroupIdAndIsActive(supportGroup.getSupportGroupId(), true);
            for (BPSupportGroupManager manager : managers) {
                if (manager != null && manager.getAgent() != null && manager.getAgent().getAgentId() != null) {
                    recipientIds.add(manager.getAgent().getAgentId());
                }
            }

            List<BPSupportGroupMember> members = supportGroupMemberRepository
                    .findBySupportGroupSupportGroupIdAndIsActive(supportGroup.getSupportGroupId(), true);
            for (BPSupportGroupMember member : members) {
                if (member != null && member.getAgent() != null && member.getAgent().getAgentId() != null) {
                    recipientIds.add(member.getAgent().getAgentId());
                }
            }
        }

        if (watchAgents != null) {
            recipientIds.addAll(watchAgents);
        }

        if (recipientIds.isEmpty()) {
            return;
        }

        List<AgentMaster> recipients = new ArrayList<>();
        for (Long recipientId : recipientIds) {
            if (recipientId == null) {
                continue;
            }
            AgentMaster recipient = agentMasterRepository.findById(recipientId).orElse(null);
            if (recipient != null) recipients.add(recipient);
        }
        notificationService.notifyRitmCreated(savedRitm, createdBy, openedBy, recipients);
    }

    @Transactional
    public TicketMasterVO updateRitm(RitmRequestVO request, List<MultipartFile> files) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (request == null || request.getRitmId() == null || request.getRitmId() <= 0) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM ID is required and must be valid"));
            }

            TicketMaster ritm = ticketMasterRepository.findById(request.getRitmId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "RITM with ID " + request.getRitmId())));

            Long companyId = ritm.getCompany() != null ? ritm.getCompany().getCompanyId() : null;
            if (request.getOrgId() != null && !Objects.equals(companyId, request.getOrgId())) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM does not belong to the provided organization"));
            }

            Long actorId = request.getUpdatedBy() != null ? request.getUpdatedBy()
                    : request.getCreatedBy() != null ? request.getCreatedBy()
                    : request.getOpenedBy();
            if (actorId == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Updated by is required"));
            }

            TicketAudit audit = ticketAuditRepository.saveAndFlush(TicketAudit.builder()
                    .ticket(ritm)
                    .actionType("UPDATE")
                    .description("RITM details updated")
                    .changedBy(actorId)
                    .build());
            List<TicketAuditDetail> details = new ArrayList<>();

            if (request.getOpenedBy() != null) {
                AgentMaster requestedBy = findAgent(request.getOpenedBy(), "Opened by");
                if (!Objects.equals(idOf(ritm.getRequestedBy()), requestedBy.getAgentId())) {
                    details.add(buildAuditDetail(audit, "REQUESTED_BY", stringValue(idOf(ritm.getRequestedBy())),
                            stringValue(requestedBy.getAgentId())));
                    ritm.setRequestedBy(requestedBy);
                }
            }
            if (request.getRequestedFor() != null) {
                AgentMaster requestedFor = findAgent(request.getRequestedFor(), "Requested for");
                if (!Objects.equals(idOf(ritm.getRequestedFor()), requestedFor.getAgentId())) {
                    details.add(buildAuditDetail(audit, "REQUESTED_FOR", stringValue(idOf(ritm.getRequestedFor())),
                            stringValue(requestedFor.getAgentId())));
                    ritm.setRequestedFor(requestedFor);
                }
            }
            if (request.getCategory() != null) {
                BPCategory category = categoryRepository.findByCategoryIdAndIsActive(request.getCategory(), true)
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), "Category with ID " + request.getCategory())));
                if (!Objects.equals(idOf(ritm.getCategory()), category.getCategoryId())) {
                    details.add(buildAuditDetail(audit, "CATEGORY_ID", stringValue(idOf(ritm.getCategory())),
                            stringValue(category.getCategoryId())));
                    ritm.setCategory(category);
                }
            }
            if (request.getSubCategory() != null) {
                BPSubCategory subCategory = subCategoryRepository.findBySubCategoryIdAndIsActive(request.getSubCategory(), true)
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), "Sub category with ID " + request.getSubCategory())));
                if (!Objects.equals(idOf(ritm.getSubCategory()), subCategory.getSubCategoryId())) {
                    details.add(buildAuditDetail(audit, "SUB_CATEGORY_ID", stringValue(idOf(ritm.getSubCategory())),
                            stringValue(subCategory.getSubCategoryId())));
                    ritm.setSubCategory(subCategory);
                }
            }
            if (request.getPriority() != null) {
                BPPriority priority = priorityRepository.findByPriorityIdAndIsActive(request.getPriority(), true)
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), "Priority with ID " + request.getPriority())));
                if (!Objects.equals(idOf(ritm.getPriority()), priority.getPriorityId())) {
                    details.add(buildAuditDetail(audit, "PRIORITY_ID", stringValue(idOf(ritm.getPriority())),
                            stringValue(priority.getPriorityId())));
                    ritm.setPriority(priority);
                    ritm.setCustomerResolutionDate(calculateCustomerResolutionDate(
                            priority.getPriorityId(), LocalDateTime.now()));
                }
            }
            Long supportGroupId = request.getAssignmentGroup() != null ? request.getAssignmentGroup() : request.getSupportGroup();
            if (supportGroupId != null) {
                BPSupportGroup supportGroup = supportGroupRepository.findById(supportGroupId)
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), "Support group with ID " + supportGroupId)));
                if (!Objects.equals(idOf(ritm.getSupportGroup()), supportGroup.getSupportGroupId())) {
                    details.add(buildAuditDetail(audit, "SUPPORT_GROUP_ID", stringValue(idOf(ritm.getSupportGroup())),
                            stringValue(supportGroup.getSupportGroupId())));
                    ritm.setSupportGroup(supportGroup);
                }
            }
            if (request.getStatus() != null) {
                StatusMaster status = statusMasterRepository.findById(request.getStatus())
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), "RITM status with ID " + request.getStatus())));
                if (status.getCompany() == null || !Objects.equals(status.getCompany().getCompanyId(), companyId)) {
                    throw new FlickzzDeskException(INVALID_REQUEST,
                            getDescription(INVALID_REQUEST.getDescription(), "RITM status does not belong to the RITM organization"));
                }
                if (!Objects.equals(idOf(ritm.getStatus()), status.getStatusId())) {
                    details.add(buildAuditDetail(audit, "STATUS", ritm.getStatus() != null ? ritm.getStatus().getStatusCode() : null,
                            status.getStatusCode()));
                    ritm.setStatus(status);
                }
            }
            if (request.getAssignedTo() != null) {
                AgentMaster assignedTo = findAgent(request.getAssignedTo(), "Assigned to");
                if (!Objects.equals(idOf(ritm.getAssignedTo()), assignedTo.getAgentId())) {
                    details.add(buildAuditDetail(audit, "ASSIGNED_TO", stringValue(idOf(ritm.getAssignedTo())),
                            stringValue(assignedTo.getAgentId())));
                    ritm.setAssignedTo(assignedTo);
                }
            }
            if (request.getDueDate() != null && !Objects.equals(ritm.getDueDate(), request.getDueDate())) {
                details.add(buildAuditDetail(audit, "DUE_DATE", String.valueOf(ritm.getDueDate()), String.valueOf(request.getDueDate())));
                ritm.setDueDate(request.getDueDate());
            }
            if (request.getResolvedAt() != null && !Objects.equals(ritm.getResolvedAt(), request.getResolvedAt())) {
                details.add(buildAuditDetail(audit, "RESOLVED_AT", String.valueOf(ritm.getResolvedAt()), String.valueOf(request.getResolvedAt())));
                ritm.setResolvedAt(request.getResolvedAt());
            }
            if (request.getClosedAt() != null && !Objects.equals(ritm.getClosedAt(), request.getClosedAt())) {
                details.add(buildAuditDetail(audit, "CLOSED_AT", String.valueOf(ritm.getClosedAt()), String.valueOf(request.getClosedAt())));
                ritm.setClosedAt(request.getClosedAt());
            }
            if (request.getCancelledAt() != null && !Objects.equals(ritm.getCancelledAt(), request.getCancelledAt())) {
                details.add(buildAuditDetail(audit, "CANCELLED_AT", String.valueOf(ritm.getCancelledAt()), String.valueOf(request.getCancelledAt())));
                ritm.setCancelledAt(request.getCancelledAt());
            }
            if (request.getActionReason() != null && !Objects.equals(ritm.getActionReason(), request.getActionReason())) {
                details.add(buildAuditDetail(audit, "ACTION_REASON", ritm.getActionReason(), request.getActionReason()));
                ritm.setActionReason(request.getActionReason());
            }

            if (request.getWatchList() != null) {
                ticketWatchlistRepository.deleteByTicket_TicketId(ritm.getTicketId());
                ticketWatchlistRepository.flush();
                if (ritm.getWatchlist() != null) {
                    ritm.getWatchlist().clear();
                } else {
                    ritm.setWatchlist(new ArrayList<>());
                }
                for (Long watcherId : new LinkedHashSet<>(request.getWatchList())) {
                    if (watcherId != null) {
                        ritm.getWatchlist().add(TicketWatchlist.builder().ticket(ritm)
                                .watchedBy(findAgent(watcherId, "Watchlist agent"))
                                .isActive(true).build());
                    }
                }
            }

            ritm.setUpdatedBy(actorId);
            ritm.setIsUpdaterAdmin(Boolean.TRUE.equals(request.getIsUpdaterAdmin()));
            TicketMaster saved = ticketMasterRepository.saveAndFlush(ritm);
            if (!details.isEmpty()) {
                ticketAuditDetailRepository.saveAllAndFlush(details);
            }
            if (request.getTemplateDetails() != null) {
                saveTemplateDetails(saved, request, actorId);
            }
            List<TicketAttachment> attachments = saveRitmFiles(saved, files, actorId);
            if (!attachments.isEmpty()) {
                ticketAttachmentRepository.saveAllAndFlush(attachments);
            }
            notificationService.notifyRitmUpdated(saved, actorId);
            recordSystemAudit("RitmMaster", saved.getTicketId(), UPDATE, null, saved.getTicketNumber(), actorId,
                    companyId, SUCCESS, null);
            log.info(generateLog(EXIT, this.getClass().getName()));
            return mapper.toRitmMasterVo(saved);
        } catch (FlickzzDeskException e) {
            recordSystemAudit("RitmMaster", request != null ? request.getRitmId() : null, UPDATE, null, null,
                    request != null ? request.getUpdatedBy() : null, request != null ? request.getOrgId() : null,
                    FAILED, e.getDescription());
            throw e;
        } catch (Exception e) {
            recordSystemAudit("RitmMaster", request != null ? request.getRitmId() : null, UPDATE, null, null,
                    request != null ? request.getUpdatedBy() : null, request != null ? request.getOrgId() : null,
                    FAILED, e.getMessage());
            log.error("Exception in updateRitm method in RitmService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    private AgentMaster findAgent(Long agentId, String label) {
        return agentMasterRepository.findById(agentId)
                .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), label + " agent with ID " + agentId)));
    }

    private Long idOf(Object entity) {
        if (entity instanceof AgentMaster agent) return agent.getAgentId();
        if (entity instanceof BPCategory category) return category.getCategoryId();
        if (entity instanceof BPSubCategory subCategory) return subCategory.getSubCategoryId();
        if (entity instanceof BPPriority priority) return priority.getPriorityId();
        if (entity instanceof BPSupportGroup supportGroup) return supportGroup.getSupportGroupId();
        if (entity instanceof StatusMaster status) return status.getStatusId();
        return null;
    }

    private TicketAuditDetail buildAuditDetail(TicketAudit audit, String fieldName, String oldValue, String newValue) {
        return TicketAuditDetail.builder()
                .audit(audit)
                .fieldName(fieldName)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();
    }

    private String stringValue(Long value) {
        return value == null ? null : String.valueOf(value);
    }

    @Transactional
    public TicketCommentVO saveRitmComment(TicketCommentVO commentVO) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (commentVO == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM comment data is required"));
            }
            if (commentVO.getTicketId() == null || commentVO.getTicketId() == 0) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM ID is required"));
            }
            if (commentVO.getCommentText() == null || commentVO.getCommentText().isBlank()) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Comment text is required"));
            }
            if (commentVO.getCreatedBy() == null && commentVO.getUpdatedBy() == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Comment actor is required"));
            }

            TicketMaster ritm = ticketMasterRepository.findById(commentVO.getTicketId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "RITM with ID " + commentVO.getTicketId())));

            Long actorId = commentVO.getUpdatedBy() != null ? commentVO.getUpdatedBy() : commentVO.getCreatedBy();
            AgentMaster actor = agentMasterRepository.findById(actorId)
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Agent with ID " + actorId)));

            Long requestedForId = ritm.getRequestedFor() != null ? ritm.getRequestedFor().getAgentId() : null;
            BPSupportGroup supportGroup = ritm.getSupportGroup();
            boolean actorAllowed = Objects.equals(actorId, requestedForId);
            if (!actorAllowed && supportGroup != null && supportGroup.getSupportGroupId() != null) {
                actorAllowed = supportGroupManagerRepository.findBySupportGroupSupportGroupIdAndAgentAgentId(supportGroup.getSupportGroupId(), actorId).isPresent()
                        || supportGroupMemberRepository.findBySupportGroupSupportGroupIdAndAgentAgentIdAndIsActive(supportGroup.getSupportGroupId(), actorId, true).isPresent();
            }
            if (!actorAllowed) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Only the requested for user or a member/manager of the support group can comment"));
            }

            TicketComment comment;
            if (commentVO.getCommentId() != null) {
                comment = ticketCommentRepository.findById(commentVO.getCommentId())
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), "RITM comment with ID " + commentVO.getCommentId())));
                if (!Objects.equals(comment.getTicket().getTicketId(), ritm.getTicketId())) {
                    throw new FlickzzDeskException(INVALID_REQUEST,
                            getDescription(INVALID_REQUEST.getDescription(), "Comment does not belong to the provided RITM"));
                }
                if (!Objects.equals(comment.getCreatedBy(), actorId) && !actorAllowed) {
                    throw new FlickzzDeskException(INVALID_REQUEST,
                            getDescription(INVALID_REQUEST.getDescription(), "You are not allowed to update this comment"));
                }
                String previousText = comment.getCommentText();
                String previousType = comment.getCommentType();
                comment.setCommentText(commentVO.getCommentText());
                comment.setCommentType(commentVO.getCommentType() != null ? GENERAL : previousType);
                comment.setIsInternal(commentVO.getIsInternal() != null ? Boolean.FALSE : comment.getIsInternal());
                comment.setUpdatedBy(actorId);
                comment.setIsActive(true);

                TicketAudit ticketAudit = ticketAuditRepository.saveAndFlush(TicketAudit.builder()
                        .ticket(ritm)
                        .actionType("COMMENT_UPDATE")
                        .description("RITM comment updated")
                        .changedBy(actorId)
                        .build());
                List<TicketAuditDetail> auditDetails = new ArrayList<>();
                if (!Objects.equals(previousText, commentVO.getCommentText())) {
                    auditDetails.add(buildAuditDetail(ticketAudit, "COMMENT_TEXT", previousText, commentVO.getCommentText()));
                }
                if (commentVO.getCommentType() != null && !Objects.equals(previousType, commentVO.getCommentType())) {
                    auditDetails.add(buildAuditDetail(ticketAudit, "COMMENT_TYPE", previousType, commentVO.getCommentType()));
                }
                if (!auditDetails.isEmpty()) {
                    ticketAuditDetailRepository.saveAllAndFlush(auditDetails);
                }
                comment = ticketCommentRepository.saveAndFlush(comment);
                recordSystemAudit("RitmComment", comment.getCommentId(), UPDATE, previousText,
                        comment.getCommentText(), actorId,
                        ritm.getCompany() != null ? ritm.getCompany().getCompanyId() : null, SUCCESS, null);
                return TicketCommentVO.builder()
                        .commentId(comment.getCommentId())
                        .ticketId(comment.getTicket().getTicketId())
                        .commentType(comment.getCommentType())
                        .commentText(comment.getCommentText())
                        .isInternal(comment.getIsInternal())
                        .createdBy(comment.getCreatedBy())
                        .createdAt(comment.getCreatedAt())
                        .updatedBy(comment.getUpdatedBy())
                        .updatedAt(comment.getUpdatedAt())
                        .isActive(comment.getIsActive())
                        .build();
            }

            comment = TicketComment.builder()
                    .ticket(ritm)
                    .commentType(commentVO.getCommentType() != null ? commentVO.getCommentType() : "GENERAL")
                    .commentText(commentVO.getCommentText())
                    .isInternal(commentVO.getIsInternal() != null ? commentVO.getIsInternal() : false)
                    .createdBy(actorId)
                    .updatedBy(actorId)
                    .isActive(true)
                    .build();
            comment = ticketCommentRepository.saveAndFlush(comment);

            TicketAudit ticketAudit = ticketAuditRepository.saveAndFlush(TicketAudit.builder()
                    .ticket(ritm)
                    .actionType("COMMENT_CREATE")
                    .description("RITM comment added")
                    .changedBy(actorId)
                    .build());
            ticketAuditDetailRepository.saveAllAndFlush(List.of(buildAuditDetail(ticketAudit, "COMMENT_TEXT", null, comment.getCommentText())));
            recordSystemAudit("RitmComment", comment.getCommentId(), CREATE, null,
                    comment.getCommentText(), actorId,
                    ritm.getCompany() != null ? ritm.getCompany().getCompanyId() : null, SUCCESS, null);

            log.info(generateLog(EXIT, this.getClass().getName()));
            return TicketCommentVO.builder()
                    .commentId(comment.getCommentId())
                    .ticketId(comment.getTicket().getTicketId())
                    .commentType(comment.getCommentType())
                    .commentText(comment.getCommentText())
                    .isInternal(comment.getIsInternal())
                    .createdBy(comment.getCreatedBy())
                    .createdAt(comment.getCreatedAt())
                    .updatedBy(comment.getUpdatedBy())
                    .updatedAt(comment.getUpdatedAt())
                    .isActive(comment.getIsActive())
                    .build();
        } catch (FlickzzDeskException e) {
            recordSystemAudit("RitmComment", commentVO != null ? commentVO.getCommentId() : null,
                    commentVO != null && commentVO.getCommentId() != null ? UPDATE : CREATE, null,
                    commentVO != null ? commentVO.getCommentText() : null,
                    commentVO != null ? (commentVO.getUpdatedBy() != null ? commentVO.getUpdatedBy() : commentVO.getCreatedBy()) : null,
                    null, FAILED, e.getDescription());
            throw e;
        } catch (Exception e) {
            recordSystemAudit("RitmComment", commentVO != null ? commentVO.getCommentId() : null,
                    commentVO != null && commentVO.getCommentId() != null ? UPDATE : CREATE, null,
                    commentVO != null ? commentVO.getCommentText() : null,
                    commentVO != null ? (commentVO.getUpdatedBy() != null ? commentVO.getUpdatedBy() : commentVO.getCreatedBy()) : null,
                    null, FAILED, e.getMessage());
            log.error("Exception in saveRitmComment method in RitmService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    @Transactional
    public TicketMasterVO assignRitm(RitmRequestVO ritmVO) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (ritmVO == null || ritmVO.getRitmId() == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM ID is required"));
            }
            if (ritmVO.getAssignedTo() == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Assigned to is required"));
            }
            if (ritmVO.getAssignedBy() == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Assigned by is required"));
            }

            TicketMaster ritm = ticketMasterRepository.findById(ritmVO.getRitmId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "RITM with ID " + ritmVO.getRitmId())));

            BPSupportGroup supportGroup = ritm.getSupportGroup();
            if (supportGroup == null || supportGroup.getSupportGroupId() == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM support group is required for assignment"));
            }

            AgentMaster actor = agentMasterRepository.findById(ritmVO.getAssignedBy())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Agent with ID " + ritmVO.getAssignedBy())));
            AgentMaster assignedTo = agentMasterRepository.findById(ritmVO.getAssignedTo())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Agent with ID " + ritmVO.getAssignedTo())));

            boolean actorIsEligible = supportGroupManagerRepository.findBySupportGroupSupportGroupIdAndAgentAgentId(supportGroup.getSupportGroupId(), actor.getAgentId()).isPresent()
                    || supportGroupMemberRepository.findBySupportGroupSupportGroupIdAndAgentAgentIdAndIsActive(supportGroup.getSupportGroupId(), actor.getAgentId(), true).isPresent();
            if (!actorIsEligible) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Only a member or manager of the support group can assign this RITM"));
            }

            boolean targetIsEligible = supportGroupManagerRepository.findBySupportGroupSupportGroupIdAndAgentAgentId(supportGroup.getSupportGroupId(), assignedTo.getAgentId()).isPresent()
                    || supportGroupMemberRepository.findBySupportGroupSupportGroupIdAndAgentAgentIdAndIsActive(supportGroup.getSupportGroupId(), assignedTo.getAgentId(), true).isPresent();
            if (!targetIsEligible) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Only a member or manager of the support group can be assigned this RITM"));
            }

            List<TicketAuditDetail> auditDetails = new ArrayList<>();
            TicketAudit ticketAudit = ticketAuditRepository.saveAndFlush(TicketAudit.builder()
                    .ticket(ritm)
                    .actionType("ASSIGN")
                    .description("RITM assigned to agent")
                    .changedBy(actor.getAgentId())
                    .build());
            if (!Objects.equals(ritm.getAssignedTo() != null ? ritm.getAssignedTo().getAgentId() : null, assignedTo.getAgentId())) {
                auditDetails.add(buildAuditDetail(ticketAudit, "ASSIGNED_TO",
                        ritm.getAssignedTo() != null ? String.valueOf(ritm.getAssignedTo().getAgentId()) : null,
                        String.valueOf(assignedTo.getAgentId())));
                ritm.setAssignedTo(assignedTo);
                ritm.setUpdatedBy(actor.getAgentId());
            }

            TicketMaster updatedRitm = ticketMasterRepository.saveAndFlush(ritm);
            if (!auditDetails.isEmpty()) {
                ticketAuditDetailRepository.saveAllAndFlush(auditDetails);
            }

            recordSystemAudit("RitmMaster", updatedRitm.getTicketId(), UPDATE, null,
                    updatedRitm.getTicketNumber(), actor.getAgentId(),
                    updatedRitm.getCompany() != null ? updatedRitm.getCompany().getCompanyId() : null,
                    SUCCESS, null);

            notificationService.notifyRitmAssigned(updatedRitm, actor);

            log.info(generateLog(EXIT, this.getClass().getName()));
            return mapper.toRitmMasterVo(updatedRitm);
        } catch (FlickzzDeskException e) {
            recordSystemAudit("RitmMaster", ritmVO != null ? ritmVO.getRitmId() : null, UPDATE, null,
                    null, ritmVO != null ? ritmVO.getAssignedBy() : null,
                    null, FAILED, e.getDescription());
            throw e;
        } catch (Exception e) {
            recordSystemAudit("RitmMaster", ritmVO != null ? ritmVO.getRitmId() : null, UPDATE, null,
                    null, ritmVO != null ? ritmVO.getAssignedBy() : null,
                    null, FAILED, e.getMessage());
            log.error("Exception in assignRitm method in RitmService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<TicketMasterVO> getRitmListByAgentAndRequestType(Long agentId, String requestType) {
        log.info(generateLog("getRitmListByAgentAndRequestType", this.getClass().getName()));
        try {
            if (agentId == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Agent ID is required"));
            }
            if (agentId <= 0) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Agent ID is invalid"));
            }
            if (requestType == null || requestType.isBlank()) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Request type is required"));
            }

            AgentMaster agent = agentMasterRepository.findById(agentId)
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Agent with ID " + agentId)));

            List<TicketMaster> ritmList;
            String normalizedRequestType = requestType.trim();
            if ("requestedByMe".equalsIgnoreCase(normalizedRequestType)) {
                ritmList = ticketMasterRepository.findByRequestedByAgentId(agent.getAgentId());
            } else if ("assignedToMe".equalsIgnoreCase(normalizedRequestType)) {
                ritmList = ticketMasterRepository.findByAssignedToAgentId(agent.getAgentId());
            } else {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Request type must be requestedByMe or assignedToMe"));
            }

            return ritmList.stream().map(mapper::toRitmMasterVo).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getRitmListByAgentAndRequestType method in RitmService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<TicketMasterVO> getAllRitmList(Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            return ticketMasterRepository.findByCompanyCompanyId(orgId).stream().map(ritm -> mapper.toRitmMasterVo(ritm)).toList();
        } catch (Exception e) {
            log.error("Error occurred while fetching RITM list for organization: {}", orgId, e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<TicketMasterVO> getTicketsByReferenceId(Long ticketReferenceId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (ticketReferenceId == null || ticketReferenceId <= 0) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Ticket reference ID is required and must be valid"));
            }

            return ticketMasterRepository.findByTicketReferenceTicketId(ticketReferenceId)
                    .stream()
                    .map(mapper::toRitmMasterVo)
                    .toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getTicketsByReferenceId method in TicketService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public TicketMasterVO getRitmDetailsById(Long ritmId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (ritmId == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM ID is required"));
            }
            if (ritmId <= 0) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM ID is invalid"));
            }

            TicketMaster ritm = ticketMasterRepository.findById(ritmId)
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "RITM with ID " + ritmId)));

            log.info(generateLog(EXIT, this.getClass().getName()));
            return mapper.toRitmMasterVo(ritm);
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getRitmDetailsById method in RitmService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<TicketCommentVO> getRitmCommentsById(Long ritmId) {
        log.info(generateLog("getRitmCommentsById", this.getClass().getName()));
        try {
            if (ritmId == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM ID is required"));
            }
            if (ritmId <= 0) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM ID is invalid"));
            }

            if (!ticketMasterRepository.existsById(ritmId)) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "RITM with ID " + ritmId));
            }

            List<TicketComment> comments = ticketCommentRepository.findAllByTicket_TicketIdOrderByCommentIdDesc(ritmId);

            return comments.stream().map(comment -> TicketCommentVO.builder()
                    .commentId(comment.getCommentId())
                    .ticketId(comment.getTicket() != null ? comment.getTicket().getTicketId() : null)
                    .commentType(comment.getCommentType())
                    .commentText(comment.getCommentText())
                    .isInternal(comment.getIsInternal())
                    .createdBy(comment.getCreatedBy())
                    .createdAt(comment.getCreatedAt())
                    .updatedBy(comment.getUpdatedBy())
                    .updatedAt(comment.getUpdatedAt())
                    .isActive(comment.getIsActive())
                    .build()).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getRitmCommentsById method in RitmService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<TicketAuditVO> getRitmAuditHistoryById(Long ritmId) {
        log.info(generateLog("getRitmAuditHistoryById", this.getClass().getName()));
        try {
            if (ritmId == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM ID is required"));
            }
            if (ritmId <= 0) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM ID is invalid"));
            }

            TicketMaster ritm = ticketMasterRepository.findById(ritmId)
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "RITM with ID " + ritmId)));

            List<TicketAudit> audits = ticketAuditRepository.findAllByTicket_TicketIdOrderByAuditIdDesc(ritmId);

            return audits.stream().map(audit -> TicketAuditVO.builder()
                    .auditId(audit.getAuditId())
                    .actionType(audit.getActionType())
                    .description(audit.getDescription())
                    .auditDetails(audit.getAuditDetails() != null ? audit.getAuditDetails().stream().map(detail -> TicketAuditDetailVO.builder()
                            .auditDetailId(detail.getAuditDetailId())
                            .fieldName(detail.getFieldName())
                            .oldValue(detail.getOldValue())
                            .newValue(detail.getNewValue())
                            .build()).toList() : null)
                    .changedBy(audit.getChangedBy())
                    .changedAt(audit.getChangedAt())
                    .build()).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getRitmAuditHistoryById method in RitmService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<TicketMasterVO> getRitmsByAssignment(RitmAssignmentRequestVO request) {
        log.info(generateLog("getRitmsByAssignment", this.getClass().getName()));
        try {
            if (request == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM assignment request data is required"));
            }

            List<Long> supportGroupIds = request.getSupportGroupIds() == null ? Collections.emptyList() : request.getSupportGroupIds().stream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
            supportGroupIds.forEach(supportGroupId -> {
                if (supportGroupId <= 0) {
                    throw new FlickzzDeskException(INVALID_REQUEST,
                            getDescription(INVALID_REQUEST.getDescription(), "Support group ID is invalid"));
                }
            });

            Long agentId = request.getAgentId();
            if (agentId != null) {
                if (agentId <= 0) {
                    throw new FlickzzDeskException(INVALID_REQUEST,
                            getDescription(INVALID_REQUEST.getDescription(), "Agent ID is invalid"));
                }
                agentMasterRepository.findById(agentId)
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), "Agent with ID " + agentId)));
            }

            List<TicketMaster> ritms;
            if (agentId != null) {
                ritms = ticketMasterRepository.findByAssignedToAgentId(agentId);
            } else {
                ritms = ticketMasterRepository.findByAssignedToIsNullAndSupportGroupSupportGroupIdIn(supportGroupIds);
            }

            return ritms.stream().map(mapper::toRitmMasterVo).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getRitmsByAssignment method in RitmService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<TicketMasterVO> getUnassignedRitms(Long supportGroupId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            List<TicketMaster> ritms = ticketMasterRepository.findByAssignedToIsNullAndSupportGroupSupportGroupId(supportGroupId);
            return ritms.stream().map(mapper::toRitmMasterVo).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<TicketMasterVO> getRitmListByStatus(Long statusId, Long supportGroupId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            List<TicketMaster> ritms = new ArrayList<>();
            if (statusId == -1) {
                ritms = ticketMasterRepository.findBySupportGroupSupportGroupIdAndStatusIsActiveFalse(supportGroupId);
            } else {
                ritms = ticketMasterRepository.findByStatusStatusIdAndSupportGroupSupportGroupId(statusId, supportGroupId);
            }
            return ritms.stream().map(mapper::toRitmMasterVo).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }
}
