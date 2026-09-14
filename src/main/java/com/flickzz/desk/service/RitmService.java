package com.flickzz.desk.service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.*;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.vo.RitmAuditDetailVO;
import com.flickzz.desk.vo.RitmAuditVO;
import com.flickzz.desk.vo.RitmCommentVO;
import com.flickzz.desk.vo.RitmMasterVO;
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

            RitmMaster ritm = RitmMaster.builder()
                    .ritmNumber(ritmNumber)
                    .company(company)
                    .requestedBy(openedBy)
                    .requestedFor(requestedFor)
                    .category(category)
                    .subCategory(subCategory)
                    .supportGroup(supportGroup)
                    .priority(priority)
                    .shortDescription(ritmVO.getShortDescription())
                    .description(ritmVO.getDescription())
                    .stepsToReproduce(ritmVO.getStepsToReproduce())
                    .otherNotes(ritmVO.getOtherNotes())
//                    .assignedTo(requestedFor)
                    .status(CREATED_STATUS)
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
            if (ritmVO.getAttachments() != null && !ritmVO.getAttachments().isEmpty()) {
                for (String attachmentName : ritmVO.getAttachments()) {
                    if (attachmentName == null || attachmentName.isBlank()) {
                        continue;
                    }
                    attachmentEntities.add(RitmAttachment.builder()
                            .ritm(savedRitm)
                            .fileName(attachmentName)
                            .originalFileName(attachmentName)
                            .storageType("REFERENCE")
                            .storagePath("ritm/" + savedRitm.getRitmId() + "/" + attachmentName)
                            .fileHash("ritm/" + savedRitm.getRitmId() + "/" + attachmentName)
                            .isActive(true)
                            .uploadedBy(createdBy)
                            .build());
                }
            }
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

            RitmAudit ritmAudit = ritmAuditRepository.saveAndFlush(RitmAudit.builder()
                    .ritm(savedRitm)
                    .actionType("CREATE")
                    .description("RITM created successfully")
                    .changedBy(createdBy)
                    .build());

            createNotificationEntries(savedRitm, supportGroup, company, createdBy, openedBy, uniqueWatchAgents);

            log.info(generateLog(EXIT, this.getClass().getName()));
            return mapper.toRitmMasterVo(savedRitm);
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in createRitm method in RitmService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
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
    public RitmMasterVO updateRitm(RitmRequestVO ritmVO) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (ritmVO == null || ritmVO.getRitmId() == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM ID is required"));
            }

            RitmMaster ritm = ritmMasterRepository.findById(ritmVO.getRitmId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "RITM with ID " + ritmVO.getRitmId())));

            Long requestedForId = ritm.getRequestedFor() != null ? ritm.getRequestedFor().getAgentId() : null;
            Long actingUserId = ritmVO.getUpdatedBy() != null ? ritmVO.getUpdatedBy()
                    : ritmVO.getCreatedBy() != null ? ritmVO.getCreatedBy()
                    : ritmVO.getOpenedBy() != null ? ritmVO.getOpenedBy()
                    : ritmVO.getRequestedFor() != null ? ritmVO.getRequestedFor()
                    : null;
            if (actingUserId == null || !Objects.equals(actingUserId, requestedForId)) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM can only be updated by the requested for user"));
            }

            List<RitmAuditDetail> auditDetails = new ArrayList<>();
            RitmAudit ritmAudit = ritmAuditRepository.saveAndFlush(RitmAudit.builder()
                    .ritm(ritm)
                    .actionType("UPDATE")
                    .description("RITM details updated")
                    .changedBy(actingUserId)
                    .build());

            if (ritmVO.getShortDescription() != null && !Objects.equals(ritm.getShortDescription(), ritmVO.getShortDescription())) {
                auditDetails.add(buildAuditDetail(ritmAudit, "SHORT_DESCRIPTION", ritm.getShortDescription(), ritmVO.getShortDescription()));
                ritm.setShortDescription(ritmVO.getShortDescription());
            }
            if (ritmVO.getDescription() != null && !Objects.equals(ritm.getDescription(), ritmVO.getDescription())) {
                auditDetails.add(buildAuditDetail(ritmAudit, "DESCRIPTION", ritm.getDescription(), ritmVO.getDescription()));
                ritm.setDescription(ritmVO.getDescription());
            }
            if (ritmVO.getStepsToReproduce() != null && !Objects.equals(ritm.getStepsToReproduce(), ritmVO.getStepsToReproduce())) {
                auditDetails.add(buildAuditDetail(ritmAudit, "STEPS_TO_REPRODUCE", ritm.getStepsToReproduce(), ritmVO.getStepsToReproduce()));
                ritm.setStepsToReproduce(ritmVO.getStepsToReproduce());
            }
            if (ritmVO.getOtherNotes() != null && !Objects.equals(ritm.getOtherNotes(), ritmVO.getOtherNotes())) {
                auditDetails.add(buildAuditDetail(ritmAudit, "OTHER_NOTES", ritm.getOtherNotes(), ritmVO.getOtherNotes()));
                ritm.setOtherNotes(ritmVO.getOtherNotes());
            }
            if (ritmVO.getActionReason() != null && !Objects.equals(ritm.getActionReason(), ritmVO.getActionReason())) {
                auditDetails.add(buildAuditDetail(ritmAudit, "ACTION_REASON", ritm.getActionReason(), ritmVO.getActionReason()));
                ritm.setActionReason(ritmVO.getActionReason());
            }
            if (ritmVO.getDueDate() != null && !Objects.equals(ritm.getDueDate(), ritmVO.getDueDate())) {
                auditDetails.add(buildAuditDetail(ritmAudit, "DUE_DATE", ritm.getDueDate() == null ? null : ritm.getDueDate().toString(), ritmVO.getDueDate().toString()));
                ritm.setDueDate(ritmVO.getDueDate());
            }
            if (ritmVO.getStatus() != null && !Objects.equals(ritm.getStatus(), ritmVO.getStatus())) {
                auditDetails.add(buildAuditDetail(ritmAudit, "STATUS", ritm.getStatus(), ritmVO.getStatus()));
                ritm.setStatus(ritmVO.getStatus());
            }
            if (ritmVO.getResolvedAt() != null && !Objects.equals(ritm.getResolvedAt(), ritmVO.getResolvedAt())) {
                auditDetails.add(buildAuditDetail(ritmAudit, "RESOLVED_AT", ritm.getResolvedAt() == null ? null : ritm.getResolvedAt().toString(), ritmVO.getResolvedAt().toString()));
                ritm.setResolvedAt(ritmVO.getResolvedAt());
            }
            if (ritmVO.getClosedAt() != null && !Objects.equals(ritm.getClosedAt(), ritmVO.getClosedAt())) {
                auditDetails.add(buildAuditDetail(ritmAudit, "CLOSED_AT", ritm.getClosedAt() == null ? null : ritm.getClosedAt().toString(), ritmVO.getClosedAt().toString()));
                ritm.setClosedAt(ritmVO.getClosedAt());
            }
            if (ritmVO.getCancelledAt() != null && !Objects.equals(ritm.getCancelledAt(), ritmVO.getCancelledAt())) {
                auditDetails.add(buildAuditDetail(ritmAudit, "CANCELLED_AT", ritm.getCancelledAt() == null ? null : ritm.getCancelledAt().toString(), ritmVO.getCancelledAt().toString()));
                ritm.setCancelledAt(ritmVO.getCancelledAt());
            }

            Long supportGroupId = ritmVO.getAssignmentGroup() != null ? ritmVO.getAssignmentGroup() : ritmVO.getSupportGroup();
            if (supportGroupId != null) {
                BPSupportGroup supportGroup = supportGroupRepository.findById(supportGroupId)
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), "Support group with ID " + supportGroupId)));
                if (!Objects.equals(ritm.getSupportGroup() != null ? ritm.getSupportGroup().getSupportGroupId() : null, supportGroup.getSupportGroupId())) {
                    auditDetails.add(buildAuditDetail(ritmAudit, "SUPPORT_GROUP_ID",
                            ritm.getSupportGroup() != null ? String.valueOf(ritm.getSupportGroup().getSupportGroupId()) : null,
                            String.valueOf(supportGroup.getSupportGroupId())));
                    ritm.setSupportGroup(supportGroup);
                }
            }

            if (ritmVO.getCategory() != null) {
                BPCategory category = categoryRepository.findByCategoryIdAndIsActive(ritmVO.getCategory(), true)
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), "Category with ID " + ritmVO.getCategory())));
                if (!Objects.equals(ritm.getCategory() != null ? ritm.getCategory().getCategoryId() : null, category.getCategoryId())) {
                    auditDetails.add(buildAuditDetail(ritmAudit, "CATEGORY_ID",
                            ritm.getCategory() != null ? String.valueOf(ritm.getCategory().getCategoryId()) : null,
                            String.valueOf(category.getCategoryId())));
                    ritm.setCategory(category);
                }
            }

            if (ritmVO.getSubCategory() != null) {
                BPSubCategory subCategory = subCategoryRepository.findBySubCategoryIdAndIsActive(ritmVO.getSubCategory(), true)
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), "Sub category with ID " + ritmVO.getSubCategory())));
                if (!Objects.equals(ritm.getSubCategory() != null ? ritm.getSubCategory().getSubCategoryId() : null, subCategory.getSubCategoryId())) {
                    auditDetails.add(buildAuditDetail(ritmAudit, "SUB_CATEGORY_ID",
                            ritm.getSubCategory() != null ? String.valueOf(ritm.getSubCategory().getSubCategoryId()) : null,
                            String.valueOf(subCategory.getSubCategoryId())));
                    ritm.setSubCategory(subCategory);
                }
            }

            if (ritmVO.getPriority() != null) {
                BPPriority priority = priorityRepository.findByPriorityIdAndIsActive(ritmVO.getPriority(), true)
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), "Priority with ID " + ritmVO.getPriority())));
                if (!Objects.equals(ritm.getPriority() != null ? ritm.getPriority().getPriorityId() : null, priority.getPriorityId())) {
                    auditDetails.add(buildAuditDetail(ritmAudit, "PRIORITY_ID",
                            ritm.getPriority() != null ? String.valueOf(ritm.getPriority().getPriorityId()) : null,
                            String.valueOf(priority.getPriorityId())));
                    ritm.setPriority(priority);
                }
            }

            if (ritmVO.getAssignedTo() != null) {
                AgentMaster assignedTo = agentMasterRepository.findById(ritmVO.getAssignedTo())
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), "Agent with ID " + ritmVO.getAssignedTo())));
                if (!Objects.equals(ritm.getAssignedTo() != null ? ritm.getAssignedTo().getAgentId() : null, assignedTo.getAgentId())) {
                    auditDetails.add(buildAuditDetail(ritmAudit, "ASSIGNED_TO",
                            ritm.getAssignedTo() != null ? String.valueOf(ritm.getAssignedTo().getAgentId()) : null,
                            String.valueOf(assignedTo.getAgentId())));
                    ritm.setAssignedTo(assignedTo);
                }
            }

            ritm.setUpdatedBy(actingUserId);
            ritm.setIsUpdaterAdmin(Boolean.TRUE.equals(ritmVO.getIsUpdaterAdmin()));

            RitmMaster updatedRitm = ritmMasterRepository.saveAndFlush(ritm);
            if (!auditDetails.isEmpty()) {
                ritmAuditDetailRepository.saveAllAndFlush(auditDetails);
            }

            if (updatedRitm.getAssignedTo() != null && !auditDetails.isEmpty()) {
                AgentMaster assignedAgent = updatedRitm.getAssignedTo();
                AgentMaster actingUser = agentMasterRepository.findById(actingUserId).orElse(null);
                ConfigChangeNotification notification = ConfigChangeNotification.builder()
                        .title("RITM updated")
                        .message("RITM " + updatedRitm.getRitmNumber() + " has been updated and requires attention.")
                        .notificationType("RITM")
                        .action("UPDATE")
                        .referenceType("RITM")
                        .referenceId(updatedRitm.getRitmId())
                        .triggeredByUser(actingUser != null ? actingUser.getAgentName() : "System")
                        .triggeredUserOrg(updatedRitm.getCompany() != null ? updatedRitm.getCompany().getCompanyName() : null)
                        .recipientUserId(assignedAgent.getAgentId())
                        .recipientUserName(assignedAgent.getAgentName())
                        .recipientOrgId(updatedRitm.getCompany() != null ? updatedRitm.getCompany().getCompanyId() : 0L)
                        .isRead(false)
                        .createdBy(actingUserId)
                        .createdOn(LocalDateTime.now())
                        .build();
                configChangeNotificationRepository.saveAndFlush(notification);
            }

            log.info(generateLog(EXIT, this.getClass().getName()));
            return mapper.toRitmMasterVo(updatedRitm);
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in updateRitm method in RitmService", e);
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
            throw e;
        } catch (Exception e) {
            log.error("Exception in saveRitmComment method in RitmService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
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
            if (ritmVO.getUpdatedBy() == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Updated by is required"));
            }

            RitmMaster ritm = ritmMasterRepository.findById(ritmVO.getRitmId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "RITM with ID " + ritmVO.getRitmId())));

            BPSupportGroup supportGroup = ritm.getSupportGroup();
            if (supportGroup == null || supportGroup.getSupportGroupId() == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM support group is required for assignment"));
            }

            AgentMaster actor = agentMasterRepository.findById(ritmVO.getUpdatedBy())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Agent with ID " + ritmVO.getUpdatedBy())));
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

            if (!Objects.equals(ritm.getAssignedTo() != null ? ritm.getAssignedTo().getAgentId() : null, assignedTo.getAgentId())) {
                auditDetails.add(buildAuditDetail(ritmAudit, "ASSIGNED_TO",
                        ritm.getAssignedTo() != null ? String.valueOf(ritm.getAssignedTo().getAgentId()) : null,
                        String.valueOf(assignedTo.getAgentId())));
                ritm.setAssignedTo(assignedTo);
                ritm.setUpdatedBy(actor.getAgentId());
            }

            RitmMaster updatedRitm = ritmMasterRepository.saveAndFlush(ritm);
            if (!auditDetails.isEmpty()) {
                ritmAuditDetailRepository.saveAllAndFlush(auditDetails);
            }

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
            throw e;
        } catch (Exception e) {
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
}
