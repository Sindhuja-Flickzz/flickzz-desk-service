package com.flickzz.desk.service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.*;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.vo.RitmMasterVO;
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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
                    .assignedTo(requestedFor)
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

            List<RitmAuditDetail> auditDetails = new ArrayList<>();
            auditDetails.add(buildAuditDetail(ritmAudit, "RITM_NUMBER", null, savedRitm.getRitmNumber()));
            auditDetails.add(buildAuditDetail(ritmAudit, "SHORT_DESCRIPTION", null, savedRitm.getShortDescription()));
            auditDetails.add(buildAuditDetail(ritmAudit, "DESCRIPTION", null, savedRitm.getDescription()));
            auditDetails.add(buildAuditDetail(ritmAudit, "STEPS_TO_REPRODUCE", null, savedRitm.getStepsToReproduce()));
            auditDetails.add(buildAuditDetail(ritmAudit, "OTHER_NOTES", null, savedRitm.getOtherNotes()));
            auditDetails.add(buildAuditDetail(ritmAudit, "SUPPORT_GROUP_ID", null, String.valueOf(supportGroup.getSupportGroupId())));
            auditDetails.add(buildAuditDetail(ritmAudit, "CATEGORY_ID", null, String.valueOf(category.getCategoryId())));
            auditDetails.add(buildAuditDetail(ritmAudit, "SUB_CATEGORY_ID", null, String.valueOf(subCategory.getSubCategoryId())));
            auditDetails.add(buildAuditDetail(ritmAudit, "PRIORITY_ID", null, String.valueOf(priority.getPriorityId())));
            auditDetails.add(buildAuditDetail(ritmAudit, "LOCATION", null, ritmVO.getLocation()));
            auditDetails.add(buildAuditDetail(ritmAudit, "AVAILABILITY_TIME", null, ritmVO.getAvailabilityTime()));
            auditDetails.add(buildAuditDetail(ritmAudit, "CURRENT_TIME", null, ritmVO.getCurrentTime()));
            auditDetails.add(buildAuditDetail(ritmAudit, "REQUEST_TYPE", null, ritmVO.getRequestType()));
            if (ritmVO.getWatchList() != null && !ritmVO.getWatchList().isEmpty()) {
                auditDetails.add(buildAuditDetail(ritmAudit, "WATCHLIST", null, ritmVO.getWatchList().toString()));
            }
            if (!auditDetails.isEmpty()) {
                ritmAuditDetailRepository.saveAllAndFlush(auditDetails);
            }

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

    private RitmAuditDetail buildAuditDetail(RitmAudit audit, String fieldName, String oldValue, String newValue) {
        return RitmAuditDetail.builder()
                .audit(audit)
                .fieldName(fieldName)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();
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

    public List<RitmMasterVO> getAllRitmList(Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            return ritmMasterRepository.findByCompanyCompanyId(orgId).stream().map(ritm -> mapper.toRitmMasterVo(ritm)).toList();
        } catch (Exception e) {
            log.error("Error occurred while fetching RITM list for organization: {}", orgId, e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }
}
