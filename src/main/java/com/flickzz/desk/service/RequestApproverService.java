package com.flickzz.desk.service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.AgentMaster;
import com.flickzz.desk.model.CompanyMaster;
import com.flickzz.desk.model.RequestApprover;
import com.flickzz.desk.model.RequestApproverConfig;
import com.flickzz.desk.repo.AgentMasterRepository;
import com.flickzz.desk.repo.CompanyMasterRepository;
import com.flickzz.desk.repo.RequestApproverConfigRepository;
import com.flickzz.desk.repo.RequestApproverRepository;
import com.flickzz.desk.vo.RequestApproverConfigVO;
import com.flickzz.desk.vo.RequestApproverVO;
import com.flickzz.desk.vo.request.RequestApproverRequestVO;
import com.flickzz.desk.vo.request.SystemAuditRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

@Service
public class RequestApproverService {

    @Autowired
    private RequestApproverConfigRepository configRepository;

    @Autowired
    private RequestApproverRepository approverRepository;

    @Autowired
    private CompanyMasterRepository companyMasterRepository;

    @Autowired
    private AgentMasterRepository agentMasterRepository;

    @Autowired
    private CommonMapper mapper;

    @Autowired
    private AuditService auditService;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public RequestApproverConfigVO createRequestApprover(RequestApproverRequestVO request) {
        validateRequest(request, false);
        CompanyMaster company = findCompany(request.getCompanyId());
        configRepository.findByApproverCodeAndCompany_CompanyIdAndIsActiveTrue(request.getApproverCode(), request.getCompanyId())
                .ifPresent(existing -> {
                    throw alreadyExists(request.getApproverCode());
                });

        RequestApproverConfig config = RequestApproverConfig.builder()
                .approverCode(request.getApproverCode()).company(company)
                .followSequence(Boolean.TRUE.equals(request.getFollowSequence()))
                .isAnyApprovalSufficient(Boolean.TRUE.equals(request.getIsAnyApprovalSufficient()))
                .createdBy(request.getCreatedBy()).isCreatorAdmin(request.getIsCreatedByAdmin())
                .updatedBy(request.getUpdatedBy()).isUpdaterAdmin(request.getIsUpdatedByAdmin()).build();
        RequestApproverConfig saved = configRepository.saveAndFlush(config);
        saveApprovers(saved, request, company);
        audit(request.getCreatedBy(), request.getIsCreatedByAdmin(), company.getCompanyId(), saved.getApproverConfigId(),
                CREATE, null, snapshot(saved, request.getApprovers()));
        return toConfigVO(saved);
    }

    private void validateRequest(RequestApproverRequestVO request, boolean update) {
        if (request == null || request.getCompanyId() == null
                || (!update && (request.getCreatedBy() == null || request.getIsCreatedByAdmin() == null))
                || (update && (request.getApproverConfigId() == null || request.getUpdatedBy() == null))
                || request.getApprovers() == null || request.getApprovers().isEmpty()
                || request.getApproverCode() == null || request.getApproverCode().isBlank()) {
            throw new FlickzzDeskException(INVALID_REQUEST, getDescription(INVALID_REQUEST.getDescription(), "Invalid request approver data"));
        }
        Set<Long> agentIds = new HashSet<>();
        Set<Integer> sequences = new HashSet<>();
        request.getApprovers().forEach(entry -> {
            if (entry == null || entry.getAgentId() == null || entry.getApproverSequence() == null
                    || entry.getApproverSequence() <= 0 || !agentIds.add(entry.getAgentId())
                    || !sequences.add(entry.getApproverSequence())) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Approvers must have unique positive agent IDs and sequences"));
            }
        });
    }

    private CompanyMaster findCompany(Long companyId) {
        return companyMasterRepository.findById(companyId).orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                getDescription(DOES_NOT_EXIST.getDescription(), "Company with ID " + companyId)));
    }

    private FlickzzDeskException alreadyExists(String code) {
        return new FlickzzDeskException(ALREADY_EXISTS,
                getDescription(ALREADY_EXISTS.getDescription(), "Request approver code " + code));
    }

    private void saveApprovers(RequestApproverConfig config, RequestApproverRequestVO request, CompanyMaster company) {
        List<RequestApprover> approvers = request.getApprovers().stream().map(entry -> {
            AgentMaster agent = agentMasterRepository.findById(entry.getAgentId())
                    .filter(value -> Boolean.TRUE.equals(value.getIsActive()))
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Active agent with ID " + entry.getAgentId())));
            if (agent.getOrganization() == null || !company.getCompanyId().equals(agent.getOrganization().getCompanyId())) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Agent belongs to a different company"));
            }
            return RequestApprover.builder().approverConfig(config).agent(agent)
                    .approverSequence(entry.getApproverSequence()).createdBy(request.getCreatedBy() != null
                            ? request.getCreatedBy() : config.getCreatedBy())
                    .updatedBy(request.getUpdatedBy()).build();
        }).toList();
        approverRepository.saveAll(approvers);
    }

    private void audit(Long userId, Boolean admin, Long companyId, Long entityId, String action, String oldValue, String newValue) {
        auditService.recordAudit(SystemAuditRequest.builder().module("Request").area("Request Approver")
                .entityName("RequestApproverConfig").entityId(entityId).action(action).oldValue(oldValue).newValue(newValue)
                .userId(userId).companyId(companyId).status(SUCCESS).build());
    }

    private String snapshot(RequestApproverConfig config, List<?> approvers) {
        List<Map<String, Object>> values = new ArrayList<>();
        approvers.forEach(approver -> {
            if (approver instanceof RequestApprover requestApprover) {
                values.add(Map.of(
                        "approverId", requestApprover.getApproverId(),
                        "agentId", requestApprover.getAgent().getAgentId(),
                        "approverSequence", requestApprover.getApproverSequence()));
            } else if (approver instanceof RequestApproverRequestVO.ApproverEntry entry) {
                values.add(Map.of(
                        "agentId", entry.getAgentId(),
                        "approverSequence", entry.getApproverSequence()));
            }
        });
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "approverCode", config.getApproverCode(),
                    "approvers", values));
        } catch (Exception ignored) {
            return null;
        }
    }

    private RequestApproverConfigVO toConfigVO(RequestApproverConfig config) {
        List<RequestApproverVO> approvers = approverRepository
                .findByApproverConfig_ApproverConfigIdAndIsActiveTrueOrderByApproverSequenceAsc(config.getApproverConfigId())
                .stream().map(approver -> RequestApproverVO.builder().approverId(approver.getApproverId())
                        .agent(mapper.toAgentMasterVO(approver.getAgent())).approverSequence(approver.getApproverSequence())
                        .isActive(approver.getIsActive()).createdBy(approver.getCreatedBy()).updatedBy(approver.getUpdatedBy())
                        .createdAt(approver.getCreatedAt()).updatedAt(approver.getUpdatedAt()).build()).toList();
        return RequestApproverConfigVO.builder().approverConfigId(config.getApproverConfigId()).approverCode(config.getApproverCode())
                .company(null).followSequence(config.getFollowSequence())
                .isAnyApprovalSufficient(config.getIsAnyApprovalSufficient()).isActive(config.getIsActive())
                .createdBy(config.getCreatedBy()).updatedBy(config.getUpdatedBy()).isCreatorAdmin(config.getIsCreatorAdmin())
                .isUpdaterAdmin(config.getIsUpdaterAdmin()).createdAt(config.getCreatedAt()).updatedAt(config.getUpdatedAt())
                .approvers(approvers).build();
    }

    @Transactional
    public RequestApproverConfigVO updateRequestApprover(RequestApproverRequestVO request) {
        validateRequest(request, true);
        RequestApproverConfig config = configRepository.findById(request.getApproverConfigId())
                .filter(value -> Boolean.TRUE.equals(value.getIsActive()))
                .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Request Approver with ID " + request.getApproverConfigId())));
        CompanyMaster company = findCompany(request.getCompanyId());
        if (config.getCompany() == null || !company.getCompanyId().equals(config.getCompany().getCompanyId())) {
            throw new FlickzzDeskException(INVALID_FIELD,
                    getDescription(INVALID_FIELD.getDescription(), "Request approver belongs to a different company"));
        }
        configRepository.findByApproverCodeAndCompany_CompanyIdAndIsActiveTrue(request.getApproverCode(), request.getCompanyId())
                .filter(existing -> !existing.getApproverConfigId().equals(config.getApproverConfigId()))
                .ifPresent(existing -> {
                    throw alreadyExists(request.getApproverCode());
                });

        String oldValue = snapshotApprovers(config, approverRepository
                .findByApproverConfig_ApproverConfigIdAndIsActiveTrueOrderByApproverSequenceAsc(config.getApproverConfigId()));
        config.setApproverCode(request.getApproverCode());
        config.setCompany(company);
        config.setFollowSequence(Boolean.TRUE.equals(request.getFollowSequence()));
        config.setIsAnyApprovalSufficient(Boolean.TRUE.equals(request.getIsAnyApprovalSufficient()));
        config.setUpdatedBy(request.getUpdatedBy());
        config.setIsUpdaterAdmin(request.getIsUpdatedByAdmin());
        config.getApprovers().clear();
        RequestApproverConfig saved = configRepository.saveAndFlush(config);
        List<RequestApprover> current = approverRepository.findByApproverConfig_ApproverConfigId(saved.getApproverConfigId());
        approverRepository.deleteAllInBatch(current);
        approverRepository.flush();
        saveApprovers(saved, request, company);
        audit(request.getUpdatedBy(), request.getIsUpdatedByAdmin(), company.getCompanyId(), saved.getApproverConfigId(),
                UPDATE, oldValue, snapshot(saved, request.getApprovers()));
        return toConfigVO(saved);
    }

    private String snapshotApprovers(RequestApproverConfig config, List<RequestApprover> approvers) {
        return snapshot(config, (List<?>) approvers);
    }

    @Transactional(readOnly = true)
    public List<RequestApproverConfigVO> listRequestApprovers(Long companyId) {
        findCompany(companyId);
        return configRepository.findByCompany_CompanyIdAndIsActiveTrue(companyId).stream().map(this::toConfigVO).toList();
    }

    @Transactional
    public void deleteRequestApprover(Long configId, Long deletedBy, Boolean isDeletedByAdmin) {
        RequestApproverConfig config = configRepository.findById(configId)
                .filter(value -> Boolean.TRUE.equals(value.getIsActive()))
                .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Request Approver with ID " + configId)));
        List<RequestApprover> approvers = approverRepository.findByApproverConfig_ApproverConfigId(configId);
        String oldValue = snapshotApprovers(config, approvers);
        configRepository.delete(config);
        audit(deletedBy, isDeletedByAdmin, config.getCompany().getCompanyId(), configId, DELETE, oldValue, null);
    }
}