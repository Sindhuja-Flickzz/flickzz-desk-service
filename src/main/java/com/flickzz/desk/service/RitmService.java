package com.flickzz.desk.service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.*;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.vo.*;
import com.flickzz.desk.vo.request.RitmAssignmentRequestVO;
import com.flickzz.desk.vo.request.RitmRequestVO;
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
public class RitmService {

    private static final Logger log = LoggerFactory.getLogger(RitmService.class);
    private static final String RITM_REQUEST_TYPE = "RITM";
    @Autowired
    CommonMapper mapper;
    @Value("${ritm.attachment.base-path}")
    private String ritmAttachmentBasePath;
    @Value("${ritm.storage.type}")
    private String ritmStorageType;
    @Autowired
    private RitmMasterRepository ritmMasterRepository;
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
    private RitmAttachmentRepository ritmAttachmentRepository;
    @Autowired
    private RitmWatchlistRepository ritmWatchlistRepository;
    @Autowired
    private RitmCommentRepository ritmCommentRepository;
    @Autowired
    private RitmAuditRepository ritmAuditRepository;
    @Autowired
    private RitmAuditDetailRepository ritmAuditDetailRepository;
    @Autowired
    private BPSupportGroupManagerRepository supportGroupManagerRepository;
    @Autowired
    private BPSupportGroupMemberRepository supportGroupMemberRepository;
    @Autowired
    private ConfigChangeNotificationRepository configChangeNotificationRepository;
    @Autowired
    private RequestConfigRepository requestConfigRepository;
    @Autowired
    private RitmFieldValueRepository ritmFieldValueRepository;
    @Autowired
    private TemplateFieldRepository templateFieldRepository;
    @Autowired
    private RitmStatusRepository ritmStatusRepository;
    @Autowired
    private AuditService auditService;

    @Transactional
    public RitmMasterVO createRitm(RitmRequestVO ritmVO, List<MultipartFile> files) {
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

            Long openedById = ritmVO.getOpenedBy() != null ? ritmVO.getOpenedBy() : ritmVO.getCreatedBy();
            if (openedById == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Opened by is required"));
            }
            AgentMaster openedBy = agentMasterRepository.findById(openedById)
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Agent with ID " + openedById)));

            if (ritmVO.getRequestedFor() == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Requested for is required"));
            }
            AgentMaster requestedFor = agentMasterRepository.findById(ritmVO.getRequestedFor())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Agent with ID " + ritmVO.getRequestedFor())));

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

            String ritmNumber = generateFreshRitmNumber(ritmVO.getOrgId(), ritmVO.getRitmNumber());
            Long createdBy = ritmVO.getCreatedBy() != null ? ritmVO.getCreatedBy() : openedById;
            Long updatedBy = ritmVO.getUpdatedBy() != null ? ritmVO.getUpdatedBy() : createdBy;
            RitmStatus status = ritmStatusRepository.findFirstByCompanyCompanyIdAndIsActiveTrueOrderBySequenceNoAsc(ritmVO.getOrgId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "No active RITM status found for org " + ritmVO.getOrgId())));

            RitmMaster ritm = RitmMaster.builder()
                    .ritmNumber(ritmNumber)
                    .company(company)
                    .requestedBy(openedBy)
                    .requestedFor(requestedFor)
                    .category(category)
                    .subCategory(subCategory)
                    .supportGroup(supportGroup)
                    .priority(priority)
                    .status(status)
                    .requestedAt(LocalDateTime.now())
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

            RitmMaster savedRitm = ritmMasterRepository.saveAndFlush(ritm);

            List<RitmAttachment> attachmentEntities = saveRitmFiles(savedRitm, files, createdBy);
            if (!attachmentEntities.isEmpty()) {
                ritmAttachmentRepository.saveAllAndFlush(attachmentEntities);
            }

            List<RitmWatchlist> watchlistEntries = new ArrayList<>();
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
                watchlistEntries.add(RitmWatchlist.builder()
                        .ritm(savedRitm)
                        .watchedBy(watchAgent)
                        .isActive(true)
                        .build());
            }
            if (!watchlistEntries.isEmpty()) {
                ritmWatchlistRepository.saveAllAndFlush(watchlistEntries);
            }
            saveTemplateDetails(savedRitm, ritmVO, createdBy);

            RitmAudit ritmAudit = ritmAuditRepository.saveAndFlush(RitmAudit.builder()
                    .ritm(savedRitm)
                    .actionType("CREATE")
                    .description("RITM created successfully")
                    .changedBy(createdBy)
                    .build());

            recordSystemAudit("RitmMaster", savedRitm.getRitmId(), CREATE, null,
                    savedRitm.getRitmNumber(), createdBy, company.getCompanyId(), SUCCESS, null);

            createNotificationEntries(savedRitm, supportGroup, company, createdBy, openedBy, uniqueWatchAgents);

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

    @Transactional
    public RitmMasterVO updateRitm(RitmRequestVO request, List<MultipartFile> files) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (request == null || request.getRitmId() == null || request.getRitmId() <= 0) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM ID is required and must be valid"));
            }

            RitmMaster ritm = ritmMasterRepository.findById(request.getRitmId())
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

            RitmAudit audit = ritmAuditRepository.saveAndFlush(RitmAudit.builder()
                    .ritm(ritm)
                    .actionType("UPDATE")
                    .description("RITM details updated")
                    .changedBy(actorId)
                    .build());
            List<RitmAuditDetail> details = new ArrayList<>();

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
                RitmStatus status = ritmStatusRepository.findById(request.getStatus())
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
                ritmWatchlistRepository.deleteByRitmRitmId(ritm.getRitmId());
                ritmWatchlistRepository.flush();
                if (ritm.getWatchlist() != null) {
                    ritm.getWatchlist().clear();
                } else {
                    ritm.setWatchlist(new ArrayList<>());
                }
                for (Long watcherId : new LinkedHashSet<>(request.getWatchList())) {
                    if (watcherId != null) {
                        ritm.getWatchlist().add(RitmWatchlist.builder().ritm(ritm)
                                .watchedBy(findAgent(watcherId, "Watchlist agent"))
                                .isActive(true).build());
                    }
                }
            }

            ritm.setUpdatedBy(actorId);
            ritm.setIsUpdaterAdmin(Boolean.TRUE.equals(request.getIsUpdaterAdmin()));
            RitmMaster saved = ritmMasterRepository.saveAndFlush(ritm);
            if (!details.isEmpty()) {
                ritmAuditDetailRepository.saveAllAndFlush(details);
            }
            if (request.getTemplateDetails() != null) {
                saveTemplateDetails(saved, request, actorId);
            }
            List<RitmAttachment> attachments = saveRitmFiles(saved, files, actorId);
            if (!attachments.isEmpty()) {
                ritmAttachmentRepository.saveAllAndFlush(attachments);
            }
            recordSystemAudit("RitmMaster", saved.getRitmId(), UPDATE, null, saved.getRitmNumber(), actorId,
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
        if (entity instanceof RitmStatus status) return status.getStatusId();
        return null;
    }

    private String stringValue(Long value) {
        return value == null ? null : String.valueOf(value);
    }

    private String generateFreshRitmNumber(Long orgId, String providedRitmNumber) {
        if (providedRitmNumber != null && !providedRitmNumber.isBlank()) {
            log.info("Ignoring provided RITM number {} and generating a fresh sequence number for org {}", providedRitmNumber, orgId);
        }

        RequestConfig requestConfig = requestConfigRepository.findByRequestTypeAndCompany_CompanyIdAndIsActiveTrueAndIsEnabledTrue(RITM_REQUEST_TYPE, orgId)
                .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Request config for RITM in org " + orgId)));

        if (!Boolean.TRUE.equals(requestConfig.getIsActive()) || !Boolean.TRUE.equals(requestConfig.getIsEnabled())) {
            throw new FlickzzDeskException(INACTIVE_ERROR,
                    getDescription(INACTIVE_ERROR.getDescription(), "RITM request config is disabled"));
        }

        Integer nextRange = requestConfig.getCurrentRange() == null ? requestConfig.getRangeFrom() : requestConfig.getCurrentRange() + 1;
        if (nextRange > requestConfig.getRangeTo() || nextRange < requestConfig.getRangeFrom()) {
            throw new FlickzzDeskException(INVALID_REQUEST,
                    getDescription(INVALID_REQUEST.getDescription(), "No available RITM number range is left"));
        }

        requestConfig.setCurrentRange(nextRange);
        requestConfigRepository.saveAndFlush(requestConfig);
        return requestConfig.getRequestPrefix() + nextRange;
    }

    private List<RitmAttachment> saveRitmFiles(RitmMaster savedRitm, List<MultipartFile> files, Long createdBy) throws IOException {
        List<RitmAttachment> attachmentEntities = new ArrayList<>();
        if (files == null || files.isEmpty()) {
            return attachmentEntities;
        }

        Path baseDirectory = Paths.get(ritmAttachmentBasePath, String.valueOf(savedRitm.getRitmId()));
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
            attachmentEntities.add(RitmAttachment.builder()
                    .ritm(savedRitm)
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

    private void saveTemplateDetails(RitmMaster ritm, RitmRequestVO request, Long actorId) {
        List<RitmFieldValue> existingFieldValues = ritmFieldValueRepository.findAllByRitmRitmId(ritm.getRitmId());
        if (ritm.getFieldValues() == null) {
            ritm.setFieldValues(new ArrayList<>());
        } else {
            ritm.getFieldValues().clear();
        }
        if (!existingFieldValues.isEmpty()) {
            ritmFieldValueRepository.deleteAll(existingFieldValues);
        }
        ritmFieldValueRepository.flush();
        if (request.getTemplateDetails() == null || request.getTemplateDetails().isEmpty()) {
            return;
        }

        List<RitmFieldValue> fieldValues = new ArrayList<>();
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
            fieldValues.add(RitmFieldValue.builder()
                    .ritm(ritm)
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
            ritmFieldValueRepository.saveAllAndFlush(fieldValues);
            ritm.getFieldValues().addAll(fieldValues);
        }
    }

    private void recordSystemAudit(String entityName, Long entityId, String action, String oldValue,
                                   String newValue, Long userId, Long companyId, String status,
                                   String errorMessage) {
        auditService.recordAudit(mapper.toSystemAuditRequest(
                "RITM", "RITM", entityName, entityId, action, newValue, oldValue,
                null, userId, null, companyId, status, errorMessage));
    }

    private void createNotificationEntries(RitmMaster savedRitm,
                                           BPSupportGroup supportGroup,
                                           CompanyMaster company,
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

        for (Long recipientId : recipientIds) {
            if (recipientId == null) {
                continue;
            }
            AgentMaster recipient = agentMasterRepository.findById(recipientId).orElse(null);
            if (recipient == null) {
                continue;
            }

            ConfigChangeNotification notification = ConfigChangeNotification.builder()
                    .title("New RITM created")
                    .message("A new RITM " + savedRitm.getRitmNumber() + " has been created and requires attention.")
                    .notificationType("RITM")
                    .action("CREATE")
                    .referenceType("RITM")
                    .referenceId(savedRitm.getRitmId())
                    .triggeredByUser(openedBy != null ? openedBy.getAgentName() : "System")
                    .triggeredUserOrg(company != null ? company.getCompanyName() : null)
                    .recipientUserId(recipient.getAgentId())
                    .recipientUserName(recipient.getAgentName())
                    .recipientOrgId(company != null ? company.getCompanyId() : 0L)
                    .isRead(false)
                    .createdBy(createdBy)
                    .createdOn(LocalDateTime.now())
                    .build();

            configChangeNotificationRepository.saveAndFlush(notification);
        }
    }

    @Transactional
    public RitmCommentVO saveRitmComment(RitmCommentVO commentVO) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (commentVO == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM comment data is required"));
            }
            if (commentVO.getRitmId() == null || commentVO.getRitmId() == 0) {
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

            RitmMaster ritm = ritmMasterRepository.findById(commentVO.getRitmId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "RITM with ID " + commentVO.getRitmId())));

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

            RitmComment comment;
            if (commentVO.getCommentId() != null) {
                comment = ritmCommentRepository.findById(commentVO.getCommentId())
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), "RITM comment with ID " + commentVO.getCommentId())));
                if (!Objects.equals(comment.getRitm().getRitmId(), ritm.getRitmId())) {
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

                RitmAudit ritmAudit = ritmAuditRepository.saveAndFlush(RitmAudit.builder()
                        .ritm(ritm)
                        .actionType("COMMENT_UPDATE")
                        .description("RITM comment updated")
                        .changedBy(actorId)
                        .build());
                List<RitmAuditDetail> auditDetails = new ArrayList<>();
                if (!Objects.equals(previousText, commentVO.getCommentText())) {
                    auditDetails.add(buildAuditDetail(ritmAudit, "COMMENT_TEXT", previousText, commentVO.getCommentText()));
                }
                if (commentVO.getCommentType() != null && !Objects.equals(previousType, commentVO.getCommentType())) {
                    auditDetails.add(buildAuditDetail(ritmAudit, "COMMENT_TYPE", previousType, commentVO.getCommentType()));
                }
                if (!auditDetails.isEmpty()) {
                    ritmAuditDetailRepository.saveAllAndFlush(auditDetails);
                }
                comment = ritmCommentRepository.saveAndFlush(comment);
                recordSystemAudit("RitmComment", comment.getCommentId(), UPDATE, previousText,
                        comment.getCommentText(), actorId,
                        ritm.getCompany() != null ? ritm.getCompany().getCompanyId() : null, SUCCESS, null);
                return RitmCommentVO.builder()
                        .commentId(comment.getCommentId())
                        .ritmId(comment.getRitm().getRitmId())
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

            comment = RitmComment.builder()
                    .ritm(ritm)
                    .commentType(commentVO.getCommentType() != null ? commentVO.getCommentType() : "GENERAL")
                    .commentText(commentVO.getCommentText())
                    .isInternal(commentVO.getIsInternal() != null ? commentVO.getIsInternal() : false)
                    .createdBy(actorId)
                    .updatedBy(actorId)
                    .isActive(true)
                    .build();
            comment = ritmCommentRepository.saveAndFlush(comment);

            RitmAudit ritmAudit = ritmAuditRepository.saveAndFlush(RitmAudit.builder()
                    .ritm(ritm)
                    .actionType("COMMENT_CREATE")
                    .description("RITM comment added")
                    .changedBy(actorId)
                    .build());
            ritmAuditDetailRepository.saveAllAndFlush(List.of(buildAuditDetail(ritmAudit, "COMMENT_TEXT", null, comment.getCommentText())));
            recordSystemAudit("RitmComment", comment.getCommentId(), CREATE, null,
                    comment.getCommentText(), actorId,
                    ritm.getCompany() != null ? ritm.getCompany().getCompanyId() : null, SUCCESS, null);

            log.info(generateLog(EXIT, this.getClass().getName()));
            return RitmCommentVO.builder()
                    .commentId(comment.getCommentId())
                    .ritmId(comment.getRitm().getRitmId())
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

    private RitmAuditDetail buildAuditDetail(RitmAudit audit, String fieldName, String oldValue, String newValue) {
        return RitmAuditDetail.builder()
                .audit(audit)
                .fieldName(fieldName)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();
    }

    @Transactional
    public RitmMasterVO assignRitm(RitmRequestVO ritmVO) {
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

            RitmMaster ritm = ritmMasterRepository.findById(ritmVO.getRitmId())
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

            List<RitmAuditDetail> auditDetails = new ArrayList<>();
            RitmAudit ritmAudit = ritmAuditRepository.saveAndFlush(RitmAudit.builder()
                    .ritm(ritm)
                    .actionType("ASSIGN")
                    .description("RITM assigned to agent")
                    .changedBy(actor.getAgentId())
                    .build());
            RitmStatus status = ritmStatusRepository.findFirstByCompany_CompanyIdAndSequenceNoGreaterThanAndIsActiveTrueOrderBySequenceNoAsc(ritm.getCompany().getCompanyId(), ritm.getStatus().getSequenceNo())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Next status for RITM with ID " + ritm.getRitmId())));

            if (!Objects.equals(ritm.getAssignedTo() != null ? ritm.getAssignedTo().getAgentId() : null, assignedTo.getAgentId())) {
                auditDetails.add(buildAuditDetail(ritmAudit, "ASSIGNED_TO",
                        ritm.getAssignedTo() != null ? String.valueOf(ritm.getAssignedTo().getAgentId()) : null,
                        String.valueOf(assignedTo.getAgentId())));
                ritm.setAssignedTo(assignedTo);
                ritm.setStatus(status);
                ritm.setUpdatedBy(actor.getAgentId());
            }

            RitmMaster updatedRitm = ritmMasterRepository.saveAndFlush(ritm);
            if (!auditDetails.isEmpty()) {
                ritmAuditDetailRepository.saveAllAndFlush(auditDetails);
            }

            recordSystemAudit("RitmMaster", updatedRitm.getRitmId(), UPDATE, null,
                    updatedRitm.getRitmNumber(), actor.getAgentId(),
                    updatedRitm.getCompany() != null ? updatedRitm.getCompany().getCompanyId() : null,
                    SUCCESS, null);

            if (updatedRitm.getAssignedTo() != null) {
                ConfigChangeNotification notification = ConfigChangeNotification.builder()
                        .title("RITM assigned")
                        .message("RITM " + updatedRitm.getRitmNumber() + " has been assigned to you.")
                        .notificationType("RITM")
                        .action("ASSIGN")
                        .referenceType("RITM")
                        .referenceId(updatedRitm.getRitmId())
                        .triggeredByUser(actor.getAgentName())
                        .triggeredUserOrg(updatedRitm.getCompany() != null ? updatedRitm.getCompany().getCompanyName() : null)
                        .recipientUserId(updatedRitm.getAssignedTo().getAgentId())
                        .recipientUserName(updatedRitm.getAssignedTo().getAgentName())
                        .recipientOrgId(updatedRitm.getCompany() != null ? updatedRitm.getCompany().getCompanyId() : 0L)
                        .isRead(false)
                        .createdBy(actor.getAgentId())
                        .createdOn(LocalDateTime.now())
                        .build();
                configChangeNotificationRepository.saveAndFlush(notification);
            }

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

    public List<RitmMasterVO> getRitmListByAgentAndRequestType(Long agentId, String requestType) {
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

            List<RitmMaster> ritmList;
            String normalizedRequestType = requestType.trim();
            if ("requestedByMe".equalsIgnoreCase(normalizedRequestType)) {
                ritmList = ritmMasterRepository.findByRequestedByAgentId(agent.getAgentId());
            } else if ("assignedToMe".equalsIgnoreCase(normalizedRequestType)) {
                ritmList = ritmMasterRepository.findByAssignedToAgentId(agent.getAgentId());
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

    public List<RitmMasterVO> getAllRitmList(Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            return ritmMasterRepository.findByCompanyCompanyId(orgId).stream().map(ritm -> mapper.toRitmMasterVo(ritm)).toList();
        } catch (Exception e) {
            log.error("Error occurred while fetching RITM list for organization: {}", orgId, e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public RitmMasterVO getRitmDetailsById(Long ritmId) {
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

            RitmMaster ritm = ritmMasterRepository.findById(ritmId)
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

    public List<RitmCommentVO> getRitmCommentsById(Long ritmId) {
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

            if (!ritmMasterRepository.existsById(ritmId)) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "RITM with ID " + ritmId));
            }

            List<RitmComment> comments = ritmCommentRepository.findAllByRitmRitmIdOrderByCommentIdDesc(ritmId);

            return comments.stream().map(comment -> RitmCommentVO.builder()
                    .commentId(comment.getCommentId())
                    .ritmId(comment.getRitm() != null ? comment.getRitm().getRitmId() : null)
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

    public List<RitmAuditVO> getRitmAuditHistoryById(Long ritmId) {
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

            RitmMaster ritm = ritmMasterRepository.findById(ritmId)
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "RITM with ID " + ritmId)));

            List<RitmAudit> audits = ritmAuditRepository.findAllByRitmRitmIdOrderByAuditIdDesc(ritmId);

            return audits.stream().map(audit -> RitmAuditVO.builder()
                    .auditId(audit.getAuditId())
                    .actionType(audit.getActionType())
                    .description(audit.getDescription())
                    .auditDetails(audit.getAuditDetails() != null ? audit.getAuditDetails().stream().map(detail -> RitmAuditDetailVO.builder()
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

    public List<RitmMasterVO> getRitmsByAssignment(RitmAssignmentRequestVO request) {
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

            List<RitmMaster> ritms;
            if (agentId != null) {
                ritms = ritmMasterRepository.findByAssignedToAgentId(agentId);
            } else {
                ritms = ritmMasterRepository.findByAssignedToIsNullAndSupportGroupSupportGroupIdIn(supportGroupIds);
            }

            return ritms.stream().map(mapper::toRitmMasterVo).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getRitmsByAssignment method in RitmService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<RitmStatusVO> createRitmStatus(List<RitmStatusVO> statusVOS) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (statusVOS == null || statusVOS.isEmpty()) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM status data is required"));
            }

            List<RitmStatus> statusesToSave = new ArrayList<>();
            Set<String> statusCodes = new HashSet<>();
            Set<String> sequenceNumbers = new HashSet<>();
            companyMasterRepository.findByCompanyIdAndIsActiveTrue(statusVOS.get(0).getCompany().getCompanyId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Company with ID " + statusVOS.get(0).getCompany().getCompanyId())));
            for (RitmStatusVO statusVO : statusVOS) {
                if (statusVO == null) {
                    throw new FlickzzDeskException(INVALID_REQUEST,
                            getDescription(INVALID_REQUEST.getDescription(), "RITM status data is required"));
                }
                if (statusVO.getCompany().getCompanyId() == null || statusVO.getCompany().getCompanyId() <= 0) {
                    throw new FlickzzDeskException(INVALID_REQUEST,
                            getDescription(INVALID_REQUEST.getDescription(), "Company ID is required and must be valid"));
                }
                if (statusVO.getStatusCode() == null || statusVO.getStatusCode().isBlank()) {
                    throw new FlickzzDeskException(INVALID_REQUEST,
                            getDescription(INVALID_REQUEST.getDescription(), "Status code is required"));
                }
                if (statusVO.getSequenceNo() == null || statusVO.getSequenceNo() < 0) {
                    throw new FlickzzDeskException(INVALID_REQUEST,
                            getDescription(INVALID_REQUEST.getDescription(), "Sequence number is required and must be non-negative"));
                }

                String statusCodeKey = statusVO.getCompany().getCompanyId() + "|" + statusVO.getStatusCode();
                String sequenceNumberKey = statusVO.getCompany().getCompanyId() + "|" + statusVO.getSequenceNo();
                if (!statusCodes.add(statusCodeKey)
                        || ritmStatusRepository.existsByCompanyCompanyIdAndStatusCode(statusVO.getCompany().getCompanyId(), statusVO.getStatusCode())) {
                    throw new FlickzzDeskException(ALREADY_EXISTS,
                            getDescription(ALREADY_EXISTS.getDescription(), "RITM status code"));
                }
                if (!sequenceNumbers.add(sequenceNumberKey)
                        || ritmStatusRepository.existsByCompanyCompanyIdAndSequenceNo(statusVO.getCompany().getCompanyId(), statusVO.getSequenceNo())) {
                    throw new FlickzzDeskException(ALREADY_EXISTS,
                            getDescription(ALREADY_EXISTS.getDescription(), "RITM status sequence"));
                }

                RitmStatus status = RitmStatus.builder()
                        .statusCode(statusVO.getStatusCode())
                        .sequenceNo(statusVO.getSequenceNo())
                        .isActive(statusVO.getIsActive() != null ? statusVO.getIsActive() : true)
                        .createdBy(statusVO.getCreatedBy())
                        .isCreatorAdmin(statusVO.getIsCreatorAdmin() != null ? statusVO.getIsCreatorAdmin() : false)
                        .build();
                statusesToSave.add(status);
            }

            List<RitmStatus> savedStatuses = ritmStatusRepository.saveAllAndFlush(statusesToSave);
            for (RitmStatus savedStatus : savedStatuses) {
                recordSystemAudit("RitmStatus", savedStatus.getStatusId(), CREATE, null,
                        savedStatus.getStatusCode(), savedStatus.getCreatedBy(), savedStatus.getCompany().getCompanyId(), SUCCESS, null);
            }
            List<RitmStatusVO> response = savedStatuses.stream().map(savedStatus -> RitmStatusVO.builder()
                    .statusId(savedStatus.getStatusId())
                    .statusCode(savedStatus.getStatusCode())
                    .isActive(savedStatus.getIsActive())
                    .sequenceNo(savedStatus.getSequenceNo())
                    .createdBy(savedStatus.getCreatedBy())
                    .isCreatorAdmin(savedStatus.getIsCreatorAdmin())
                    .build()).toList();
            return response;
        } catch (FlickzzDeskException e) {
            RitmStatusVO firstStatus = statusVOS != null && !statusVOS.isEmpty() ? statusVOS.get(0) : null;
            recordSystemAudit("RitmStatus", null, CREATE, null,
                    firstStatus != null ? firstStatus.getStatusCode() : null,
                    firstStatus != null ? firstStatus.getCreatedBy() : null,
                    firstStatus != null ? firstStatus.getCompany().getCompanyId() : null, FAILED, e.getDescription());
            throw e;
        } catch (Exception e) {
            RitmStatusVO firstStatus = statusVOS != null && !statusVOS.isEmpty() ? statusVOS.get(0) : null;
            recordSystemAudit("RitmStatus", null, CREATE, null,
                    firstStatus != null ? firstStatus.getStatusCode() : null,
                    firstStatus != null ? firstStatus.getCreatedBy() : null,
                    firstStatus != null ? firstStatus.getCompany().getCompanyId() : null, FAILED, e.getMessage());
            log.error("Exception in createRitmStatus method in RitmService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<RitmStatusVO> getRitmStatusByOrgId(Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        return ritmStatusRepository.findByCompanyCompanyIdAndIsActiveTrue(orgId).stream().map(status -> RitmStatusVO.builder()
                .statusId(status.getStatusId())
                .statusCode(status.getStatusCode())
                .isActive(status.getIsActive())
                .sequenceNo(status.getSequenceNo())
                .createdBy(status.getCreatedBy())
                .updatedBy(status.getUpdatedBy())
                .build()).toList();
    }

    @Transactional
    public void deleteRitmStatus(Long statusId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        RitmStatus status = null;
        try {
            if (statusId == null || statusId <= 0) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM status ID is required and must be valid"));
            }

            status = ritmStatusRepository.findById(statusId)
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "RITM status with ID " + statusId)));

            Long companyId = status.getCompany() != null ? status.getCompany().getCompanyId() : null;
            Long actorId = status.getUpdatedBy() != null ? status.getUpdatedBy() : status.getCreatedBy();
            String statusCode = status.getStatusCode();

            ritmStatusRepository.delete(status);
            ritmStatusRepository.flush();
            recordSystemAudit("RitmStatus", statusId, DELETE, statusCode, null,
                    actorId, companyId, SUCCESS, null);
            log.info(generateLog(EXIT, this.getClass().getName()));
        } catch (FlickzzDeskException e) {
            recordSystemAudit("RitmStatus", statusId, DELETE,
                    status != null ? status.getStatusCode() : null, null,
                    status != null ? (status.getUpdatedBy() != null ? status.getUpdatedBy() : status.getCreatedBy()) : null,
                    status != null && status.getCompany() != null ? status.getCompany().getCompanyId() : null,
                    FAILED, e.getDescription());
            throw e;
        } catch (Exception e) {
            recordSystemAudit("RitmStatus", statusId, DELETE,
                    status != null ? status.getStatusCode() : null, null,
                    status != null ? (status.getUpdatedBy() != null ? status.getUpdatedBy() : status.getCreatedBy()) : null,
                    status != null && status.getCompany() != null ? status.getCompany().getCompanyId() : null,
                    FAILED, e.getMessage());
            log.error("Exception in deleteRitmStatus method in RitmService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }
}