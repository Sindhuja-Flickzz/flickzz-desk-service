package com.flickzz.desk.service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.*;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.service.notification.ConfigNotificationService;
import com.flickzz.desk.vo.*;
import com.flickzz.desk.vo.request.BpConfigRequestVO;
import com.flickzz.desk.vo.request.CompanyMasterRequestVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.*;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

@Service
@SuppressWarnings("unused")
public class BusinessPartnerService {

    private static final Logger log = LoggerFactory.getLogger(BusinessPartnerService.class);

    @Autowired
    CompanyMasterRepository companyMasterRepository;

    @Autowired
    BusinessPartnerRepository businessPartnerRepository;

    @Autowired
    BPConfigurationRepository bPConfigurationRepository;

    @Autowired
    TicketTypeMasterRepository ticketTypeMasterRepository;

    @Autowired
    BPPriorityRepository bPPriorityRepository;

    @Autowired
    BPSlaRepository bpSlaRepository;

    @Autowired
    BPCategoryRepository bpCategoryRepository;

    @Autowired
    BPSubCategoryRepository bpSubCategoryRepository;

    @Autowired
    BPSupportGroupRepository bpSupportGroupRepository;

    @Autowired
    BPSupportGroupMemberRepository bpSupportGroupMemberRepository;

    @Autowired
    BPSupportGroupManagerRepository bpSupportGroupManagerRepository;

    @Autowired
    BPAssignmentRepository bpAssignmentRepository;

    @Autowired
    AgentMasterRepository agentMasterRepository;

    @Autowired
    SystemAuditRepository systemAuditRepository;
    @Autowired
    AuditService auditService;
    @Autowired
    CommonService commonService;
    @Autowired
    CommonMapper mapper;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    BPConfigurationChangeRequestRepository bpConfigurationChangeRequestRepository;
    @Autowired
    BPConfigurationChangeRequestRemarkRepository bpConfigurationChangeRequestRemarkRepository;
    @Autowired
    CompanyApproverRepository companyApproverRepository;
    @Autowired
    ConfigChangeApprovalRepository configChangeApprovalRepository;
    @Autowired
    ConfigNotificationService configNotificationService;
    @Autowired
    ConfigurationChangeService configurationChangeService;
    @Autowired
    private UserRepository userRepository; // your DB repo
    @Autowired
    private EnquiryRegistrationRepository enquiryRegistrationRepository;

    public BusinessPartnerVO createBusinessPartner(CompanyMasterRequestVO request) {
        log.info(generateLog("createBusinessPartner", this.getClass().getName()));
        try {
            if (request == null || request.getCompanyId() == null) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), COMPANY));
            }

            if (request == null || request.getBpUid() == null) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        "Update Company profile to create configuration");
            }

            if (request.getCompanyId().equals(request.getBpUid())) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Business Partner"));
            }

            CompanyMaster bpCompany = companyMasterRepository.findByUidAndIsActive(request.getBpUid(), ACTIVE)
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), COMPANY)));

            AtomicInteger newVersion = new AtomicInteger(INITIAL_VERSION);
            businessPartnerRepository.getBusinessPartnerMapping(request.getCompanyId(), bpCompany.getCompanyId()).ifPresent(bp -> {
                if (bp.getIsActive()) {
                    throw new FlickzzDeskException(ALREADY_EXISTS,
                            getDescription(ALREADY_EXISTS.getDescription(), "Business Partner mapping"));
                } else {
                    newVersion.set(bp.getVersion() + 1);
                }
            });

            BusinessPartner entity = new BusinessPartner();
            entity.setCompany(companyMasterRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), COMPANY))));
            entity.setMappedCompany(bpCompany);
            entity.setIsBoth(Boolean.TRUE);
            entity.setCreatedBy(request.getCreatedBy());
            entity.setIsCreatorAdmin(request.getIsCreatedByAdmin());
            entity.setCallHorizon(request.getCallHorizonDays());
            entity.setValidFrom(request.getValidFrom());
            entity.setValidTo(request.getValidTo());
            entity.setRefNo(request.getRefNumber());
            entity.setRefDate(request.getRefDate());
            entity.setVersion(newVersion.get());
            businessPartnerRepository.save(entity);

            // Record CREATE audit
            String newSnapshot = null;
            try {
                newSnapshot = objectMapper.writeValueAsString(buildBusinessPartnerSnapshot(entity));
            } catch (Exception ignore) {
            }

            java.util.Map<String, Object> changed = new java.util.HashMap<>();
            changed.put("isBoth", entity.getIsBoth());
            changed.put("callHorizon", entity.getCallHorizon());
            changed.put("validFrom", entity.getValidFrom());
            changed.put("version", entity.getVersion());
            changed.put("validTo", entity.getValidTo());
            String changedStr = null;
            try {
                changedStr = objectMapper.writeValueAsString(changed);
            } catch (Exception ignore) {
            }

            auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "Partner", "BusinessPartner",
                    entity.getBusinessPartnerId(),
                    CREATE,
                    newSnapshot,
                    null,
                    changedStr,
                    request.getCreatedBy(),
                    commonService.loadUserNameByUserId(Long.valueOf(request.getCreatedBy()), request.getIsCreatedByAdmin()),
                    request.getCompanyId(),
                    SUCCESS,
                    null));

            return mapper.toBusinessPartnerVO(entity);
        } catch (FlickzzDeskException e) {
            // record FlickzzDeskException as ERROR audit and rethrow
            String attempted = null;
            try {
                java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
                attemptedMap.put("isBoth", Boolean.TRUE);
                attemptedMap.put("callHorizon", request != null ? request.getCallHorizonDays() : null);
                attemptedMap.put("validFrom", request != null ? request.getValidFrom() : null);
                attemptedMap.put("validTo", request != null ? request.getValidTo() : null);
                attempted = objectMapper.writeValueAsString(attemptedMap);
            } catch (Exception ignore) {
            }
            auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Partner", "BusinessPartner",
                    null,
                    CREATE,
                    attempted,
                    null,
                    null,
                    request != null ? request.getCreatedBy() : null,
                    commonService.loadUserNameByUserId(Long.valueOf(request.getCreatedBy()), request.getIsCreatedByAdmin()),
                    request != null ? request.getCompanyId() : null,
                    FAILED,
                    e.getDescription()), e);
            throw e;
        } catch (Exception e) {
            // record unexpected exception as ERROR audit
            String attempted = null;
            try {
                java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
                attemptedMap.put("isBoth", Boolean.TRUE);
                attemptedMap.put("callHorizon", request != null ? request.getCallHorizonDays() : null);
                attemptedMap.put("validFrom", request != null ? request.getValidFrom() : null);
                attemptedMap.put("validTo", request != null ? request.getValidTo() : null);
                attempted = objectMapper.writeValueAsString(attemptedMap);
            } catch (Exception ignore) {
            }
            auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Partner", "BusinessPartner",
                    null,
                    CREATE,
                    attempted,
                    null,
                    null,
                    request != null ? request.getCreatedBy() : null,
                    commonService.loadUserNameByUserId(Long.valueOf(request.getCreatedBy()), request.getIsCreatedByAdmin()),
                    request != null ? request.getCompanyId() : null,
                    FAILED,
                    e.getMessage()), e);
            log.error("Exception in createBusinessPartner method in BusinessPartnerService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    private Map<String, Object> buildBusinessPartnerSnapshot(BusinessPartner bp) {
        Map<String, Object> snapshot = new HashMap<>();
        if (bp == null) {
            return snapshot;
        }
        try {
            snapshot.put("businessPartnerId", bp.getBusinessPartnerId());
            snapshot.put("isBoth", bp.getIsBoth());
            snapshot.put("isCreatorAdmin", bp.getIsCreatorAdmin());
            snapshot.put("callHorizon", bp.getCallHorizon());
            snapshot.put("validFrom", bp.getValidFrom());
            snapshot.put("validTo", bp.getValidTo());
            snapshot.put("refNo", bp.getRefNo());
            snapshot.put("refDate", bp.getRefDate());
            snapshot.put("createdBy", bp.getCreatedBy());
            snapshot.put("updatedBy", bp.getUpdatedBy());
            snapshot.put("version", bp.getVersion());
            snapshot.put("createdAt", bp.getCreatedAt());
            snapshot.put("updatedAt", bp.getUpdatedAt());
            try {
                if (bp.getCompany() != null) {
                    snapshot.put("companyId", bp.getCompany().getCompanyId());
                }
            } catch (Exception ignore) {
            }
            try {
                if (bp.getMappedCompany() != null) {
                    snapshot.put("mappedCompanyId", bp.getMappedCompany().getCompanyId());
                }
            } catch (Exception ignore) {
            }
        } catch (Exception ignore) {
        }
        return snapshot;
    }

    public BPConfigurationVO getBusinessPartnerConfigurationList(String businessPartnerId) {
        log.info(generateLog("getBusinessPartnerConfigurationList", this.getClass().getName()));
        try {
            Optional<BPConfiguration> configurations = bPConfigurationRepository
                    .findByBusinessPartnerBusinessPartnerIdAndIsActiveTrue(Long.valueOf(businessPartnerId));
            return mapper.toBPConfigurationVO(configurations.get());
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getBusinessPartnerConfigurationList method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public BPPriorityVO createBusinessPartnerPriorityConfiguration(BpConfigRequestVO request) {

        log.info(generateLog("createBusinessPartnerPriorityConfiguration", this.getClass().getName()));
        try {
            if (request == null || request.getBusinessPartnerId() == null) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
            }

            Optional<BusinessPartner> businessPartner = businessPartnerRepository
                    .findByBusinessPartnerIdAndIsActive(request.getBusinessPartnerId(), ACTIVE);
            if (!businessPartner.isPresent()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
            }

            Optional<TicketTypeMaster> ticketType = ticketTypeMasterRepository
                    .findByTicketTypeIdAndIsActiveTrue(request.getTicketTypeId());
            if (ticketType == null) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), TICKET_TYPE));
            }

            Optional<BPConfiguration> existingConfig = bPConfigurationRepository
                    .findByBusinessPartnerBusinessPartnerIdAndIsActiveTrue(request.getBusinessPartnerId());

            BPConfiguration config = new BPConfiguration();

            if (!existingConfig.isPresent()) {
                config.setBusinessPartner(businessPartner.get());
                config.setCreatedBy(request.getCreatedBy());
                config.setIsCreatorAdmin(request.getIsCreatedByAdmin());
                bPConfigurationRepository.save(config);
            } else {
                config = existingConfig.get();
            }

            if (bPPriorityRepository.existsByConfigurationConfigurationIdAndTicketTypeTicketTypeIdAndLevel(
                    config.getConfigurationId(), request.getTicketTypeId(), request.getLevel())) {
                throw new FlickzzDeskException(ALREADY_EXISTS,
                        "Priority Level already exists for the selected Ticket Type.");
            }

            if (bPPriorityRepository.existsByConfigurationConfigurationIdAndTicketTypeTicketTypeIdAndCode(
                    config.getConfigurationId(), request.getTicketTypeId(), request.getCode())) {
                throw new FlickzzDeskException(ALREADY_EXISTS,
                        "Priority Code already exists for the selected Ticket Type.");
            }

            BPPriority bpPriority = mapper.toBPPriority(request, config, ticketType.get());
            bpPriority.setIsActive(INACTIVE);
            bpPriority.setVersion(INITIAL_VERSION);
            bpPriority.setIsUnderApproval(UNDER_APPROVAL);
            // Prepare new snapshot for audit
            String newSnapshot = null;
            try {
                newSnapshot = objectMapper.writeValueAsString(buildPrioritySnapshot(bpPriority));
            } catch (Exception ignore) {
            }
            bPPriorityRepository.save(bpPriority);

            configurationChangeService.addConfigurationChangeRequest(config, bpPriority.getPriorityId(), null, Boolean.TRUE, Boolean.FALSE,
                    Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, CREATE,
                    businessPartner.get().getCompany(), request.getCreatedBy(),
                    businessPartner.get().getMappedCompany(), request.getIsCreatedByAdmin(), request.getRemarks());
            java.util.Map<String, Object> changed = new java.util.HashMap<>();
            changed.put("level", bpPriority.getLevel());
            changed.put("code", bpPriority.getCode());
            changed.put("isActive", bpPriority.getIsActive());
            changed.put("description", bpPriority.getDescription());
            String changedStr = null;
            try {
                changedStr = objectMapper.writeValueAsString(changed);
            } catch (Exception ignore) {
            }

            auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "Priority", "BPPriority",
                    bpPriority.getPriorityId(),
                    CREATE,
                    newSnapshot,
                    null,
                    changedStr,
                    request.getCreatedBy(),
                    commonService.loadUserNameByUserId(Long.valueOf(request.getCreatedBy()), request.getIsCreatedByAdmin()),
                    request.getOrgId(),
                    SUCCESS,
                    null));

            return mapper.toBPPriorityVo(bpPriority);
        } catch (FlickzzDeskException e) {
            // record FlickzzDeskException as ERROR audit and rethrow
            String attempted = null;
            try {
                java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
                attemptedMap.put("level", request != null ? request.getLevel() : null);
                attemptedMap.put("code", request != null ? request.getCode() : null);
                attemptedMap.put("description", request != null ? request.getDescription() : null);
                attempted = objectMapper.writeValueAsString(attemptedMap);
            } catch (Exception ignore) {
            }
            auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Priority", "BPPriority", null, CREATE,
                    attempted, null, null, request != null ? request.getCreatedBy() : null, commonService.loadUserNameByUserId(Long.valueOf(request.getCreatedBy()), request.getIsCreatedByAdmin()), (request != null ? request.getOrgId() : null), FAILED, e.getDescription()), e);
            throw e;
        } catch (Exception e) {
            String attempted = null;
            try {
                java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
                attemptedMap.put("level", request != null ? request.getLevel() : null);
                attemptedMap.put("code", request != null ? request.getCode() : null);
                attemptedMap.put("description", request != null ? request.getDescription() : null);
                attempted = objectMapper.writeValueAsString(attemptedMap);
            } catch (Exception ignore) {
            }
            auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Priority", "BPPriority",
                    null,
                    CREATE,
                    attempted,
                    null,
                    null,
                    request != null ? request.getCreatedBy() : null,
                    commonService.loadUserNameByUserId(Long.valueOf(request.getCreatedBy()), request.getIsCreatedByAdmin()),
                    request != null ? request.getOrgId() : null,
                    FAILED,
                    e.getMessage()), e);
            log.error("Exception in createBusinessPartnerPriorityConfiguration method in BusinessPartnerService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public BPPriorityVO updateBusinessPartnerPriorityConfiguration(BpConfigRequestVO request) {
        log.info(generateLog("updateBusinessPartnerPriorityConfiguration", this.getClass().getName()));
        BPPriority existingPriority = null;
        try {
            if (request == null || request.getPriorityId() == null) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), PRIORITY));
            }

            Optional<BPPriority> existingPriorityOpt = bPPriorityRepository
                    .findByPriorityIdAndIsActive(request.getPriorityId(), ACTIVE);

            if (existingPriorityOpt.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), PRIORITY));
            } else if (existingPriorityOpt.get().getIsUnderApproval() != null && existingPriorityOpt.get().getIsUnderApproval()) {
                throw new FlickzzDeskException(ALREADY_EXISTS,
                        "Priority change is already under approval. Please wait for the approval process to complete.");
            }

            existingPriority = existingPriorityOpt.get();

            String oldValue = null;
            try {
                oldValue = objectMapper.writeValueAsString(buildPrioritySnapshot(existingPriority));
            } catch (Exception ignore) {
            }

            TicketTypeMaster ticketType = existingPriority.getTicketType();
            if (request.getTicketTypeId() != null && !request.getTicketTypeId().equals(ticketType.getTicketTypeId())) {
                Optional<TicketTypeMaster> ticketTypeOpt = ticketTypeMasterRepository
                        .findByTicketTypeIdAndIsActiveTrue(request.getTicketTypeId());
                if (ticketTypeOpt.isEmpty()) {
                    throw new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), TICKET_TYPE));
                }
                ticketType = ticketTypeOpt.get();
            }

            existingPriority.setIsUnderApproval(UNDER_APPROVAL);
            bPPriorityRepository.save(existingPriority);

            BPPriority newPriority = mapper.toBPPriority(request, existingPriority.getConfiguration(), ticketType);
            newPriority.setIsActive(INACTIVE);
            newPriority.setIsUnderApproval(UNDER_APPROVAL);
            int nextVersion = existingPriority.getVersion() + 1;
            newPriority.setVersion(nextVersion);
            newPriority = bPPriorityRepository.save(newPriority);

            configurationChangeService.addConfigurationChangeRequest(existingPriority.getConfiguration(), newPriority.getPriorityId(),
                    existingPriority.getPriorityId(), Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE,
                    Boolean.FALSE, UPDATE,
                    existingPriority.getConfiguration().getBusinessPartner().getCompany(), request.getUpdatedBy(),
                    existingPriority.getConfiguration().getBusinessPartner().getMappedCompany(), Boolean.FALSE, request.getRemarks());

            Map<String, Object> changedFields = new HashMap<>();
            addChangedField(changedFields, "code", existingPriority.getCode(), request.getCode());
            addChangedField(changedFields, "description", existingPriority.getDescription(), request.getDescription());
            addChangedField(changedFields, "level", existingPriority.getLevel(), request.getLevel());
            addChangedField(changedFields, "ticketTypeId", existingPriority.getTicketType().getTicketTypeId(), ticketType.getTicketTypeId());
            addChangedField(changedFields, "updatedBy", existingPriority.getUpdatedBy(), request.getUpdatedBy());

            String newSnapshot = null;
            try {
                newSnapshot = objectMapper.writeValueAsString(buildPrioritySnapshot(newPriority));
            } catch (Exception ignore) {
            }
            String changedValue = safeSerialize(changedFields);
            auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "Priority", "BPPriority",
                    newPriority.getPriorityId(), UPDATE, newSnapshot, oldValue, changedValue,
                    request.getUpdatedBy(), commonService.loadUserNameByUserId(Long.valueOf(request.getUpdatedBy()), request.getIsUpdatedByAdmin()),
                    request.getOrgId(), SUCCESS, null));

            return mapper.toBPPriorityVo(newPriority);
        } catch (FlickzzDeskException e) {
            String newValue = null;
            try {
                if (request != null) {
                    // create a simple representation of attempted new values
                    Map<String, Object> attempted = new HashMap<>();
                    attempted.put("code", request.getCode());
                    attempted.put("description", request.getDescription());
                    attempted.put("updatedBy", request.getUpdatedBy());
                    try {
                        newValue = objectMapper.writeValueAsString(attempted);
                    } catch (Exception ignore) {
                        newValue = null;
                    }
                }
            } catch (Exception ignore) {
            }
            auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Priority", "BPPriority", existingPriority != null ? existingPriority.getPriorityId() : null, UPDATE, newValue, null, null, request != null ? request.getUpdatedBy() : null, commonService.loadUserNameByUserId(Long.valueOf(request.getUpdatedBy()), request.getIsUpdatedByAdmin()), request.getOrgId(), FAILED, e.getMessage()), e);
            throw e;
        } catch (Exception e) {
            // Attempt to save error audit
            try {
                String oldValue = null;
                String newValue = null;
                try {
                    if (existingPriority != null) {
                        if (existingPriority.getPriorityId() != null) {
                            try {
                                oldValue = objectMapper.writeValueAsString(buildPrioritySnapshot(existingPriority));
                            } catch (Exception ignore) {
                                oldValue = null;
                            }
                        }
                    }
                } catch (Exception ignore) {
                }
                // newValue: attempt to capture requested values
                try {
                    if (request != null) {
                        // create a simple representation of attempted new values
                        Map<String, Object> attempted = new HashMap<>();
                        attempted.put("code", request.getCode());
                        attempted.put("description", request.getDescription());
                        attempted.put("updatedBy", request.getUpdatedBy());
                        try {
                            newValue = objectMapper.writeValueAsString(attempted);
                        } catch (Exception ignore) {
                            newValue = null;
                        }
                    }
                } catch (Exception ignore) {
                }
                try {
                    auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Priority", "BPPriority",
                            existingPriority != null ? existingPriority.getPriorityId() : null,
                            UPDATE,
                            newValue,
                            oldValue,
                            null,
                            request != null ? request.getUpdatedBy() : null,
                            commonService.loadUserNameByUserId(Long.valueOf(request.getCreatedBy()), request.getIsUpdatedByAdmin()),
                            request.getOrgId(),
                            FAILED,
                            e.getMessage()), e);
                } catch (Exception ignore) {
                    log.error("Failed to save error audit for updateBusinessPartnerPriorityConfiguration", ignore);
                }
            } catch (Exception ignore) {
                log.error("Unexpected error while recording audit", ignore);
            }
            log.error("Exception in updateBusinessPartnerPriorityConfiguration method in BusinessPartnerService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    private void addChangedField(Map<String, Object> changedFields, String field, Object oldValue, Object newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            Map<String, Object> change = new HashMap<>();
            change.put("old", oldValue);
            change.put("new", newValue);
            changedFields.put(field, change);
        }
    }

    private String safeSerialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ignore) {
            return null;
        }
    }

    public void deleteBusinessPartnerPriorityConfiguration(BpConfigRequestVO request) {

        log.info(generateLog("deleteBusinessPartnerPriorityConfiguration", this.getClass().getName()));
        BPPriority bpPriority = null;
        try {
            Optional<BPPriority> existingPriority = bPPriorityRepository
                    .findByPriorityIdAndIsActive(request.getPriorityId(), ACTIVE);

            if (existingPriority.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), PRIORITY));
            } else if (existingPriority.get().getIsUnderApproval() != null && existingPriority.get().getIsUnderApproval()) {
                throw new FlickzzDeskException(ALREADY_EXISTS,
                        "Priority change is already under approval. Please wait for the approval process to complete.");
            }

            bpPriority = existingPriority.get();

            // Capture old snapshot before deletion
            String oldValue = null;
            try {
                oldValue = objectMapper.writeValueAsString(buildPrioritySnapshot(bpPriority));
            } catch (Exception ignore) {
            }

            validatePrioritySlaForDeletion(bpPriority);

            bpPriority.setIsUnderApproval(UNDER_APPROVAL);
            bPPriorityRepository.save(bpPriority);

            // Create a new inactive copy for delete (leave existing record untouched)
            BPPriority newPriority = BPPriority.builder()
                    .configuration(bpPriority.getConfiguration())
                    .level(bpPriority.getLevel())
                    .code(bpPriority.getCode())
                    .description(bpPriority.getDescription())
                    .version(bpPriority.getVersion() != null ? bpPriority.getVersion() + 1 : 1)
                    .ticketType(bpPriority.getTicketType())
                    .isActive(INACTIVE)
                    .isUnderApproval(UNDER_APPROVAL)
                    .createdBy(bpPriority.getCreatedBy())
                    .updatedBy((request.getDeletedBy() != null) ? Long.valueOf(request.getDeletedBy()) : bpPriority.getUpdatedBy())
                    .build();
            bPPriorityRepository.save(newPriority);

            configurationChangeService.addConfigurationChangeRequest(newPriority.getConfiguration(), newPriority.getPriorityId(), newPriority.getPriorityId(),
                    Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, DELETE,
                    newPriority.getConfiguration().getBusinessPartner().getCompany(), request.getDeletedBy(),
                    newPriority.getConfiguration().getBusinessPartner().getMappedCompany(), Boolean.FALSE, request.getRemarks());

            Long companyId = bpPriority.getConfiguration() != null
                    && bpPriority.getConfiguration().getBusinessPartner() != null
                    && bpPriority.getConfiguration().getBusinessPartner().getCompany() != null
                    ? bpPriority.getConfiguration().getBusinessPartner().getCompany().getCompanyId()
                    : null;
            // Record DELETE audit
            auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "Priority", "BPPriority",
                    bpPriority.getPriorityId(),
                    DELETE,
                    oldValue,
                    null,
                    null,
                    request.getDeletedBy(),
                    commonService.loadUserNameByUserId(Long.valueOf(request.getDeletedBy()), request.getIsDeletedByAdmin()),
                    companyId,
                    SUCCESS,
                    null));
        } catch (FlickzzDeskException e) {
            // record FlickzzDeskException as ERROR audit and rethrow
            try {
                Long entityId = null;
                Long companyId = null;
                try {
                    entityId = Long.valueOf(request.getPriorityId());
                } catch (Exception ignore) {
                }
                if (bpPriority != null && bpPriority.getConfiguration() != null
                        && bpPriority.getConfiguration().getBusinessPartner() != null
                        && bpPriority.getConfiguration().getBusinessPartner().getCompany() != null) {
                    companyId = bpPriority.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                }
                auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Priority", "BPPriority",
                        entityId, DELETE,
                        null, null, null, request.getDeletedBy(), commonService.loadUserNameByUserId(Long.valueOf(request.getDeletedBy()), request.getIsDeletedByAdmin()), companyId, FAILED, e.getDescription()), e);
            } catch (Exception ignore) {
            }
            throw e;
        } catch (Exception e) {
            // record generic exception as ERROR audit
            try {
                Long entityId = null;
                Long companyId = null;
                try {
                    entityId = Long.valueOf(request.getPriorityId());
                } catch (Exception ignore) {
                }
                if (bpPriority != null && bpPriority.getConfiguration() != null
                        && bpPriority.getConfiguration().getBusinessPartner() != null
                        && bpPriority.getConfiguration().getBusinessPartner().getCompany() != null) {
                    companyId = bpPriority.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                }
                auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Priority", "BPPriority",
                        entityId, DELETE,
                        null, null, null, request.getDeletedBy(), commonService.loadUserNameByUserId(Long.valueOf(request.getDeletedBy()), request.getIsDeletedByAdmin()), companyId, FAILED, e.getMessage()), e);
            } catch (Exception ignore) {
            }
            log.error("Exception in deleteBusinessPartnerPriorityConfiguration method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    private void validatePrioritySlaForDeletion(BPPriority bpPriority) {
        if (bpPriority == null) {
            return;
        }
        boolean hasSla = bpSlaRepository.existsByConfigurationConfigurationIdAndPriorityPriorityIdAndIsActive(
                bpPriority.getConfiguration() != null ? bpPriority.getConfiguration().getConfigurationId() : null,
                bpPriority.getPriorityId(),
                ACTIVE);
        if (hasSla) {
            throw new FlickzzDeskException(INVALID_FIELD,
                    bpPriority.getCode() + " cannot be be deleted, as it associates SLA configured");
        }
    }

    public BPPriorityVO getBusinessPartnerPriorityConfigurationById(Long valueOf) {

        log.info(generateLog("getBusinessPartnerPriorityConfigurationById", this.getClass().getName()));
        try {
            Optional<BPPriority> existingPriority = bPPriorityRepository.findById(valueOf);

            if (existingPriority.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), PRIORITY));
            }

            return mapper.toBPPriorityVo(existingPriority.get());
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getBusinessPartnerPriorityConfigurationById method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<BPPriorityVO> getBusinessPartnerPriorityConfiguration(Long businessPartnerId, Boolean fetchActive) {
        log.info(generateLog("getBusinessPartnerPriorityConfiguration", this.getClass().getName()));
        try {

            Optional<BusinessPartner> businessPartner = businessPartnerRepository
                    .findByBusinessPartnerIdAndIsActive(businessPartnerId, ACTIVE);
            if (!businessPartner.isPresent()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
            }

            Optional<BPConfiguration> existingConfig = bPConfigurationRepository
                    .findByBusinessPartnerBusinessPartnerIdAndIsActiveTrue(businessPartnerId);

            if (!existingConfig.isPresent()) {
                return Collections.emptyList();
            }

            List<BPPriority> priorities = bPPriorityRepository
                    .findByConfigurationConfigurationId(existingConfig.get().getConfigurationId());
            if (fetchActive) {
                return priorities.stream().filter(p -> p.getIsActive()).map(mapper::toBPPriorityVo).toList();
            }
            return priorities.stream().map(mapper::toBPPriorityVo).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getBusinessPartnerPriorityConfiguration method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public BPSlaVO createBusinessPartnerSLAConfiguration(BpConfigRequestVO request) {
        log.info(generateLog("createBusinessPartnerSLAConfiguration", this.getClass().getName()));
        try {
            if (request == null || request.getBusinessPartnerId() == null) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
            }

            Optional<BPPriority> existingPriority = bPPriorityRepository
                    .findByPriorityIdAndIsActive(request.getPriorityId(), ACTIVE);

            if (existingPriority.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), PRIORITY));
            }

            Optional<BusinessPartner> businessPartner = businessPartnerRepository
                    .findByBusinessPartnerIdAndIsActive(request.getBusinessPartnerId(), ACTIVE);
            if (!businessPartner.isPresent()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
            }

            if (bpSlaRepository.existsByConfigurationConfigurationIdAndPriorityPriorityIdAndIsActive(
                    existingPriority.get().getConfiguration().getConfigurationId(),
                    existingPriority.get().getPriorityId(), ACTIVE)) {
                throw new FlickzzDeskException(ALREADY_EXISTS,
                        getDescription(ALREADY_EXISTS.getDescription(), "Business Partner SLA configuration"));
            }

            Optional<BPConfiguration> existingConfig = bPConfigurationRepository
                    .findByBusinessPartnerBusinessPartnerIdAndIsActiveTrue(request.getBusinessPartnerId());

            BPConfiguration config = new BPConfiguration();

            if (!existingConfig.isPresent()) {
                config.setBusinessPartner(businessPartner.get());
                config.setCreatedBy(request.getCreatedBy());
                config.setIsCreatorAdmin(request.getIsCreatedByAdmin());
                bPConfigurationRepository.save(config);
            } else {
                config = existingConfig.get();
            }

            BPSla bpSla = mapper.toBPSla(request, config, existingPriority.get());

            // Prepare new snapshot for audit
            String newSnapshot = null;
            try {
                newSnapshot = objectMapper.writeValueAsString(buildSlaSnapshot(bpSla));
            } catch (Exception ignore) {
            }
            bpSla.setIsActive(INACTIVE);
            bpSla.setIsUnderApproval(UNDER_APPROVAL);
            bpSlaRepository.save(bpSla);

            configurationChangeService.addConfigurationChangeRequest(config, bpSla.getSlaId(), null, Boolean.FALSE, Boolean.TRUE,
                    Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, CREATE,
                    businessPartner.get().getCompany(), request.getCreatedBy(),
                    businessPartner.get().getMappedCompany(), request.getIsCreatedByAdmin(), request.getRemarks());

            // Record CREATE audit
            java.util.Map<String, Object> changed = new java.util.HashMap<>();
            changed.put("firstResponseTime", bpSla.getFirstResponseTime());
            changed.put("firstResponseTerm", bpSla.getFirstResponseTerm());
            changed.put("resolutionTime", bpSla.getResolutionTime());
            changed.put("resolutionTerm", bpSla.getResolutionTerm());
            changed.put("updateFrequency", bpSla.getUpdateFrequency());
            changed.put("updateFrequencyTerm", bpSla.getUpdateFrequencyTerm());
            String changedStr = null;
            try {
                changedStr = objectMapper.writeValueAsString(changed);
            } catch (Exception ignore) {
            }

            auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "SLA", "BPSla",
                    bpSla.getSlaId(),
                    CREATE,
                    newSnapshot,
                    null,
                    changedStr,
                    request.getCreatedBy(),
                    commonService.loadUserNameByUserId(Long.valueOf(request.getCreatedBy()), request.getIsCreatedByAdmin()),
                    request.getOrgId(),
                    SUCCESS,
                    null));

            return mapper.toBPSlaVo(bpSla);
        } catch (FlickzzDeskException e) {
            // record FlickzzDeskException as ERROR audit and rethrow
            String attempted = null;
            try {
                java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
                attemptedMap.put("firstResponseTime", request != null ? request.getFirstResponseTime() : null);
                attemptedMap.put("firstResponseTerm", request != null ? request.getFirstResponseTerm() : null);
                attemptedMap.put("resolutionTime", request != null ? request.getResolutionTime() : null);
                attemptedMap.put("resolutionTerm", request != null ? request.getResolutionTerm() : null);
                attemptedMap.put("updateFrequency", request != null ? request.getUpdateFrequency() : null);
                attemptedMap.put("updateFrequencyTerm", request != null ? request.getUpdateFrequencyTerm() : null);
                attempted = objectMapper.writeValueAsString(attemptedMap);
            } catch (Exception ignore) {
            }
            auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SLA", "BPSla", null, CREATE,
                    attempted, null, null, request != null ? request.getCreatedBy() : null, commonService.loadUserNameByUserId(Long.valueOf(request.getCreatedBy()), request.getIsCreatedByAdmin()), request != null ? request.getOrgId() : null, FAILED, e.getDescription()), e);
            throw e;
        } catch (Exception e) {
            String attempted = null;
            try {
                java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
                attemptedMap.put("firstResponseTime", request != null ? request.getFirstResponseTime() : null);
                attemptedMap.put("firstResponseTerm", request != null ? request.getFirstResponseTerm() : null);
                attemptedMap.put("resolutionTime", request != null ? request.getResolutionTime() : null);
                attemptedMap.put("resolutionTerm", request != null ? request.getResolutionTerm() : null);
                attemptedMap.put("updateFrequency", request != null ? request.getUpdateFrequency() : null);
                attemptedMap.put("updateFrequencyTerm", request != null ? request.getUpdateFrequencyTerm() : null);
                attempted = objectMapper.writeValueAsString(attemptedMap);
            } catch (Exception ignore) {
            }
            auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SLA", "BPSla",
                    null,
                    CREATE,
                    attempted,
                    null,
                    null,
                    request != null ? request.getCreatedBy() : null,
                    commonService.loadUserNameByUserId(Long.valueOf(request.getCreatedBy()), request.getIsCreatedByAdmin()),
                    request != null ? request.getOrgId() : null,
                    FAILED,
                    e.getMessage()), e);
            log.error("Exception in createBusinessPartnerSLAConfiguration method in BusinessPartnerService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<BPSlaVO> getBusinessPartnerSLAConfiguration(Long valueOf) {
        log.info(generateLog("getBusinessPartnerSLAConfiguration", this.getClass().getName()));
        try {

            Optional<BusinessPartner> businessPartner = businessPartnerRepository
                    .findByBusinessPartnerIdAndIsActive(valueOf, ACTIVE);
            if (!businessPartner.isPresent()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
            }

            Optional<BPConfiguration> existingConfig = bPConfigurationRepository
                    .findByBusinessPartnerBusinessPartnerIdAndIsActiveTrue(valueOf);

            if (!existingConfig.isPresent()) {
                return Collections.emptyList();
            }

            List<BPSla> slas = bpSlaRepository
                    .findByConfigurationConfigurationId(existingConfig.get().getConfigurationId());
            return slas.stream().map(mapper::toBPSlaVo).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getBusinessPartnerSLAConfiguration method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public BPSlaVO updateBusinessPartnerSLAConfiguration(BpConfigRequestVO request) {

        log.info(generateLog("updateBusinessPartnerSLAConfiguration", this.getClass().getName()));
        BPSla bpSla = null;
        BPSla oldSla = null;
        try {
            if (request == null || request.getSlaId() == null) {
                throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), SLA));
            }

            Optional<BPSla> existingSla = bpSlaRepository.findBySlaIdAndIsActive(request.getSlaId(), ACTIVE);

            if (existingSla.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), SLA));
            } else if (existingSla.get().getIsUnderApproval() != null && existingSla.get().getIsUnderApproval()) {
                throw new FlickzzDeskException(ALREADY_EXISTS,
                        "SLA change is already under approval. Please wait for the approval process to complete.");
            }

            oldSla = existingSla.get();

            String oldValue = null;
            try {
                oldValue = objectMapper.writeValueAsString(buildSlaSnapshot(oldSla));
            } catch (Exception ignore) {
            }

            Map<String, Object> changedFields = new HashMap<>();
            addChangedField(changedFields, "firstResponseTime", oldSla.getFirstResponseTime(), request.getFirstResponseTime());
            addChangedField(changedFields, "firstResponseTerm", oldSla.getFirstResponseTerm(), request.getFirstResponseTerm());
            addChangedField(changedFields, "resolutionTime", oldSla.getResolutionTime(), request.getResolutionTime());
            addChangedField(changedFields, "resolutionTerm", oldSla.getResolutionTerm(), request.getResolutionTerm());
            addChangedField(changedFields, "updateFrequency", oldSla.getUpdateFrequency(), request.getUpdateFrequency());
            addChangedField(changedFields, "updateFrequencyTerm", oldSla.getUpdateFrequencyTerm(), request.getUpdateFrequencyTerm());
            addChangedField(changedFields, "updatedBy", oldSla.getUpdatedBy(), request.getUpdatedBy());

            bpSla.setIsUnderApproval(UNDER_APPROVAL);
            bpSlaRepository.save(bpSla);

            BPSla newSla = mapper.toBPSla(request, oldSla.getConfiguration(), oldSla.getPriority());
            newSla.setCreatedBy(oldSla.getCreatedBy());
            newSla.setUpdatedBy(request.getUpdatedBy() != null ? request.getUpdatedBy() : oldSla.getUpdatedBy());
            newSla.setIsActive(INACTIVE);
            newSla.setIsUnderApproval(UNDER_APPROVAL);
            newSla.setVersion(oldSla.getVersion() != null ? oldSla.getVersion() + 1 : INITIAL_VERSION);
            newSla = bpSlaRepository.save(newSla);

            bpSla = newSla;

            configurationChangeService.addConfigurationChangeRequest(oldSla.getConfiguration(), newSla.getSlaId(), oldSla.getSlaId(),
                    Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, UPDATE,
                    oldSla.getConfiguration().getBusinessPartner().getCompany(), request.getUpdatedBy(),
                    oldSla.getConfiguration().getBusinessPartner().getMappedCompany(), Boolean.FALSE, request.getRemarks());

            // Record UPDATE audit if there are changes
            if (!changedFields.isEmpty()) {
                String newSnapshot = safeSerialize(buildSlaSnapshot(newSla));
                String changedStr = safeSerialize(changedFields);
                Long companyId = null;
                try {
                    if (oldSla.getConfiguration() != null && oldSla.getConfiguration().getBusinessPartner() != null
                            && oldSla.getConfiguration().getBusinessPartner().getCompany() != null) {
                        companyId = bpSla.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                    }
                } catch (Exception ignore) {
                }
                auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "SLA", "BPSla",
                        bpSla.getSlaId(),
                        UPDATE,
                        newSnapshot,
                        oldValue,
                        changedStr,
                        request.getUpdatedBy(),
                        commonService.loadUserNameByUserId(Long.valueOf(request.getUpdatedBy()), request.getIsUpdatedByAdmin()),
                        companyId,
                        SUCCESS,
                        null));
            }

            return mapper.toBPSlaVo(bpSla);
        } catch (FlickzzDeskException e) {
            // Attempt to save error audit
            try {
                Long entityId = null;
                Long companyId = null;
                String oldSnap = null;
                if (bpSla != null) {
                    entityId = bpSla.getSlaId();
                    try {
                        oldSnap = objectMapper.writeValueAsString(buildSlaSnapshot(bpSla));
                    } catch (Exception ignore) {
                    }
                    try {
                        if (bpSla.getConfiguration() != null && bpSla.getConfiguration().getBusinessPartner() != null
                                && bpSla.getConfiguration().getBusinessPartner().getCompany() != null) {
                            companyId = bpSla.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                        }
                    } catch (Exception ignore) {
                    }
                }
                String newSnap = null;
                if (request != null) {
                    try {
                        Map<String, Object> attempted = new HashMap<>();
                        attempted.put("firstResponseTime", request.getFirstResponseTime());
                        attempted.put("firstResponseTerm", request.getFirstResponseTerm());
                        attempted.put("resolutionTime", request.getResolutionTime());
                        attempted.put("resolutionTerm", request.getResolutionTerm());
                        attempted.put("updateFrequency", request.getUpdateFrequency());
                        attempted.put("updateFrequencyTerm", request.getUpdateFrequencyTerm());
                        newSnap = objectMapper.writeValueAsString(attempted);
                    } catch (Exception ignore) {
                    }
                }
                auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SLA", "BPSla",
                        entityId, UPDATE, newSnap, oldSnap, null,
                        request != null ? request.getUpdatedBy() : null, commonService.loadUserNameByUserId(Long.valueOf(request.getUpdatedBy()), request.getIsUpdatedByAdmin()), companyId, FAILED, e.getDescription()), e);
            } catch (Exception ignore) {
            }
            throw e;
        } catch (Exception e) {
            // Attempt to save error audit
            try {
                Long entityId = null;
                Long companyId = null;
                String oldSnap = null;
                if (bpSla != null) {
                    entityId = bpSla.getSlaId();
                    try {
                        oldSnap = objectMapper.writeValueAsString(buildSlaSnapshot(bpSla));
                    } catch (Exception ignore) {
                    }
                    try {
                        if (bpSla.getConfiguration() != null && bpSla.getConfiguration().getBusinessPartner() != null
                                && bpSla.getConfiguration().getBusinessPartner().getCompany() != null) {
                            companyId = bpSla.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                        }
                    } catch (Exception ignore) {
                    }
                }
                String newSnap = null;
                if (request != null) {
                    try {
                        Map<String, Object> attempted = new HashMap<>();
                        attempted.put("firstResponseTime", request.getFirstResponseTime());
                        attempted.put("firstResponseTerm", request.getFirstResponseTerm());
                        attempted.put("resolutionTime", request.getResolutionTime());
                        attempted.put("resolutionTerm", request.getResolutionTerm());
                        attempted.put("updateFrequency", request.getUpdateFrequency());
                        attempted.put("updateFrequencyTerm", request.getUpdateFrequencyTerm());
                        newSnap = objectMapper.writeValueAsString(attempted);
                    } catch (Exception ignore) {
                    }
                }
                auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SLA", "BPSla",
                        entityId, UPDATE, newSnap, oldSnap, null,
                        request != null ? request.getUpdatedBy() : null, commonService.loadUserNameByUserId(Long.valueOf(request.getUpdatedBy()), request.getIsUpdatedByAdmin()), companyId, FAILED, e.getMessage()), e);
            } catch (Exception ignore) {
            }
            log.error("Exception in updateBusinessPartnerSLAConfiguration method in BusinessPartnerService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public void deleteBusinessPartnerSLAConfiguration(BpConfigRequestVO request) {

        log.info(generateLog("deleteBusinessPartnerSLAConfiguration", this.getClass().getName()));
        BPSla bpSla = null;
        try {
            Optional<BPSla> existingSla = bpSlaRepository.findBySlaIdAndIsActive(Long.valueOf(request.getSlaId()), ACTIVE);

            if (existingSla.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), SLA));
            } else if (existingSla.get().getIsUnderApproval() != null && existingSla.get().getIsUnderApproval()) {
                throw new FlickzzDeskException(ALREADY_EXISTS,
                        "SLA change is already under approval. Please wait for the approval process to complete.");
            }

            bpSla = existingSla.get();

            // Capture old snapshot before deletion
            String oldValue = null;
            try {
                oldValue = objectMapper.writeValueAsString(buildSlaSnapshot(bpSla));
            } catch (Exception ignore) {
            }

            bpSla.setIsUnderApproval(UNDER_APPROVAL);
            bpSlaRepository.save(bpSla);

            // Create a new inactive copy for delete (leave existing record untouched)
            BPSla newSla = BPSla.builder()
                    .configuration(bpSla.getConfiguration())
                    .priority(bpSla.getPriority())
                    .firstResponseTime(bpSla.getFirstResponseTime())
                    .firstResponseTerm(bpSla.getFirstResponseTerm())
                    .resolutionTime(bpSla.getResolutionTime())
                    .resolutionTerm(bpSla.getResolutionTerm())
                    .updateFrequency(bpSla.getUpdateFrequency())
                    .updateFrequencyTerm(bpSla.getUpdateFrequencyTerm())
                    .version(bpSla.getVersion() != null ? bpSla.getVersion() + 1 : 1)
                    .isActive(INACTIVE)
                    .isUnderApproval(UNDER_APPROVAL)
                    .createdBy(bpSla.getCreatedBy())
                    .updatedBy((request.getDeletedBy() != null) ? Long.valueOf(request.getDeletedBy()) : bpSla.getUpdatedBy())
                    .build();
            // ensure new entity will be inserted (no id)
            newSla.setSlaId(null);
            bpSlaRepository.save(newSla);

            configurationChangeService.addConfigurationChangeRequest(newSla.getConfiguration(), newSla.getSlaId(), newSla.getSlaId(),
                    Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, DELETE,
                    newSla.getConfiguration().getBusinessPartner().getCompany(), request.getDeletedBy(),
                    newSla.getConfiguration().getBusinessPartner().getMappedCompany(), Boolean.FALSE, request.getRemarks());

            // Record DELETE audit
            Long companyId = null;
            try {
                if (bpSla.getConfiguration() != null && bpSla.getConfiguration().getBusinessPartner() != null
                        && bpSla.getConfiguration().getBusinessPartner().getCompany() != null) {
                    companyId = bpSla.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                }
            } catch (Exception ignore) {
            }
            auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "SLA", "BPSla",
                    bpSla.getSlaId(),
                    DELETE,
                    oldValue,
                    null,
                    null,
                    request.getDeletedBy(),
                    commonService.loadUserNameByUserId(Long.valueOf(request.getDeletedBy()), request.getIsDeletedByAdmin()),
                    companyId,
                    SUCCESS,
                    null));
        } catch (FlickzzDeskException e) {
            // record FlickzzDeskException as ERROR audit and rethrow
            try {
                Long entityId = null;
                Long companyId = null;
                try {
                    entityId = Long.valueOf(request.getSlaId());
                } catch (Exception ignore) {
                }
                if (bpSla != null && bpSla.getConfiguration() != null
                        && bpSla.getConfiguration().getBusinessPartner() != null
                        && bpSla.getConfiguration().getBusinessPartner().getCompany() != null) {
                    companyId = bpSla.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                }
                auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SLA", "BPSla",
                        entityId, DELETE,
                        null, null, null, request.getDeletedBy(), commonService.loadUserNameByUserId(Long.valueOf(request.getDeletedBy()), request.getIsDeletedByAdmin()), companyId, FAILED, e.getDescription()), e);
            } catch (Exception ignore) {
            }
            throw e;
        } catch (Exception e) {
            // record generic exception as ERROR audit
            try {
                Long entityId = null;
                Long companyId = null;
                try {
                    entityId = Long.valueOf(request.getSlaId());
                } catch (Exception ignore) {
                }
                if (bpSla != null && bpSla.getConfiguration() != null
                        && bpSla.getConfiguration().getBusinessPartner() != null
                        && bpSla.getConfiguration().getBusinessPartner().getCompany() != null) {
                    companyId = bpSla.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                }
                auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SLA", "BPSla",
                        entityId, DELETE,
                        null, null, null, request.getDeletedBy(), commonService.loadUserNameByUserId(Long.valueOf(request.getDeletedBy()), request.getIsDeletedByAdmin()), companyId, FAILED, e.getMessage()), e);
            } catch (Exception ignore) {
            }
            log.error("Exception in deleteBusinessPartnerSLAConfiguration method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public BPSlaVO getBusinessPartnerSLAConfigurationById(Long valueOf) {

        log.info(generateLog("getBusinessPartnerSLAConfigurationById", this.getClass().getName()));
        try {
            Optional<BPSla> existingSla = bpSlaRepository.findById(valueOf);

            if (existingSla.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), SLA));
            }

            return mapper.toBPSlaVo(existingSla.get());
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getBusinessPartnerSLAConfigurationById method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public BPCategoryVO createBusinessPartnerCategoryConfiguration(BpConfigRequestVO request) {

        log.info(generateLog("createBusinessPartnerCategoryConfiguration", this.getClass().getName()));
        try {
            if (request == null || request.getBusinessPartnerId() == null) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
            }

            Optional<BusinessPartner> businessPartner = businessPartnerRepository
                    .findByBusinessPartnerIdAndIsActive(request.getBusinessPartnerId(), ACTIVE);
            if (!businessPartner.isPresent()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
            }

            Optional<BPConfiguration> existingConfig = bPConfigurationRepository
                    .findByBusinessPartnerBusinessPartnerIdAndIsActiveTrue(request.getBusinessPartnerId());

            BPConfiguration config = new BPConfiguration();

            if (!existingConfig.isPresent()) {
                config.setBusinessPartner(businessPartner.get());
                config.setCreatedBy(request.getCreatedBy());
                config.setIsCreatorAdmin(request.getIsCreatedByAdmin());
                bPConfigurationRepository.save(config);
            } else {
                config = existingConfig.get();
            }

            if (bpCategoryRepository.existsByConfigurationConfigurationIdAndCategoryNameAndIsActive(
                    config.getConfigurationId(), request.getCategoryName(), ACTIVE)) {
                throw new FlickzzDeskException(ALREADY_EXISTS,
                        getDescription(ALREADY_EXISTS.getDescription(), CATEGORY));
            }

            BPCategory bpCategory = mapper.toBPCategory(request, config);

            // Prepare new snapshot for audit
            String newSnapshot = null;
            try {
                newSnapshot = objectMapper.writeValueAsString(buildCategorySnapshot(bpCategory));
            } catch (Exception ignore) {
            }
            bpCategory.setIsActive(INACTIVE);
            bpCategory.setIsUnderApproval(UNDER_APPROVAL);
            bpCategoryRepository.save(bpCategory);

            configurationChangeService.addConfigurationChangeRequest(config, bpCategory.getCategoryId(), null, Boolean.FALSE, Boolean.FALSE,
                    Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, CREATE,
                    businessPartner.get().getCompany(), request.getCreatedBy(),
                    businessPartner.get().getMappedCompany(), request.getIsCreatedByAdmin(), request.getRemarks());

            if (request.getSubCategories() != null && !request.getSubCategories().isEmpty()) {
                for (String subCategoryName : request.getSubCategories()) {
                    if (subCategoryName == null || subCategoryName.trim().isEmpty()) {
                        continue;
                    }
                    upsertSubCategory(bpCategory, subCategoryName.trim(), request.getCreatedBy(), request.getUpdatedBy());
                }
            }

            // Record CREATE audit
            java.util.Map<String, Object> changed = new java.util.HashMap<>();
            changed.put("categoryName", bpCategory.getCategoryName());
            String changedStr = null;
            try {
                changedStr = objectMapper.writeValueAsString(changed);
            } catch (Exception ignore) {
            }

            auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "Category", "BPCategory",
                    bpCategory.getCategoryId(),
                    CREATE,
                    newSnapshot,
                    null,
                    changedStr,
                    request.getCreatedBy(),
                    commonService.loadUserNameByUserId(Long.valueOf(request.getCreatedBy()), request.getIsCreatedByAdmin()),
                    request.getOrgId(),
                    SUCCESS,
                    null));

            return mapper.toBPCategoryVo(bpCategory);
        } catch (FlickzzDeskException e) {
            // record FlickzzDeskException as ERROR audit and rethrow
            String attempted = null;
            try {
                java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
                attemptedMap.put("categoryName", request != null ? request.getCategoryName() : null);
                attempted = objectMapper.writeValueAsString(attemptedMap);
            } catch (Exception ignore) {
            }
            auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Category", "BPCategory", null, CREATE,
                    attempted, null, null, request != null ? request.getCreatedBy() : null, commonService.loadUserNameByUserId(Long.valueOf(request.getCreatedBy()), request.getIsCreatedByAdmin()), request != null ? request.getOrgId() : null, FAILED, e.getDescription()), e);
            throw e;
        } catch (Exception e) {
            String attempted = null;
            try {
                java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
                attemptedMap.put("categoryName", request != null ? request.getCategoryName() : null);
                attempted = objectMapper.writeValueAsString(attemptedMap);
            } catch (Exception ignore) {
            }
            auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Category", "BPCategory",
                    null,
                    CREATE,
                    attempted,
                    null,
                    null,
                    request != null ? request.getCreatedBy() : null,
                    commonService.loadUserNameByUserId(Long.valueOf(request.getCreatedBy()), request.getIsCreatedByAdmin()),
                    request != null ? request.getOrgId() : null,
                    FAILED,
                    e.getMessage()), e);
            log.error("Exception in createBusinessPartnerCategoryConfiguration method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    BPSubCategory upsertSubCategory(BPCategory bpCategory, String subCategoryName, Long createdBy, Long updatedBy) {
        if (bpCategory == null || subCategoryName == null || subCategoryName.trim().isEmpty()) {
            return null;
        }

        String normalizedName = subCategoryName.trim();
        Optional<BPSubCategory> existingSubCategory = bpSubCategoryRepository
                .findByCategoryCategoryIdAndSubCategoryName(bpCategory.getCategoryId(), normalizedName);
        if (existingSubCategory.isPresent()) {
            BPSubCategory subCategory = existingSubCategory.get();
            if (!Boolean.TRUE.equals(subCategory.getIsActive())) {
                subCategory.setIsActive(Boolean.TRUE);
                subCategory.setUpdatedBy(updatedBy);
                if (subCategory.getCreatedBy() == null && createdBy != null) {
                    subCategory.setCreatedBy(createdBy);
                }
                return bpSubCategoryRepository.save(subCategory);
            }
            return subCategory;
        }

        BPSubCategory subCategory = BPSubCategory.builder().category(bpCategory).subCategoryName(normalizedName)
                .createdBy(createdBy).updatedBy(updatedBy).build();
        return bpSubCategoryRepository.save(subCategory);
    }

    public BPCategoryVO updateBusinessPartnerCategoryConfiguration(BpConfigRequestVO request) {
        log.info(generateLog("updateBusinessPartnerCategoryConfiguration", this.getClass().getName()));
        BPCategory bpCategory = null;
        try {
            if (request == null || request.getCategoryId() == null) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), CATEGORY));
            }

            Optional<BPCategory> existingCategory = bpCategoryRepository
                    .findByCategoryIdAndIsActive(request.getCategoryId(), ACTIVE);
            if (existingCategory.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), CATEGORY));
            } else if (existingCategory.get().getIsUnderApproval() != null && existingCategory.get().getIsUnderApproval()) {
                throw new FlickzzDeskException(ALREADY_EXISTS,
                        "Category change is already under approval. Please wait for the approval process to complete.");
            }

            bpCategory = existingCategory.get();

            String trimmedCategoryName = request.getCategoryName() == null ? null : request.getCategoryName().trim();
            if (trimmedCategoryName == null || trimmedCategoryName.isEmpty()) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), CATEGORY));
            }

            if (!bpCategory.getCategoryName().equals(trimmedCategoryName)
                    && bpCategoryRepository.existsByConfigurationConfigurationIdAndCategoryNameAndIsActive(
                    bpCategory.getConfiguration().getConfigurationId(), trimmedCategoryName, ACTIVE)) {
                throw new FlickzzDeskException(ALREADY_EXISTS,
                        getDescription(ALREADY_EXISTS.getDescription(), CATEGORY));
            }

            String oldValue = safeSerialize(buildCategorySnapshot(bpCategory));

            List<String> existingSubCategoryNames = bpCategory.getSubCategories() == null ? Collections.emptyList()
                    : bpCategory.getSubCategories().stream().filter(BPSubCategory::getIsActive)
                    .map(BPSubCategory::getSubCategoryName).toList();

            Set<String> incomingSubCategories = new LinkedHashSet<>();
            if (request.getSubCategories() != null) {
                for (String subCategoryName : request.getSubCategories()) {
                    if (subCategoryName == null || subCategoryName.trim().isEmpty()) {
                        continue;
                    }
                    incomingSubCategories.add(subCategoryName.trim());
                }
            } else {
                incomingSubCategories.addAll(existingSubCategoryNames);
            }

            validateSubCategoryAssignmentsForRemoval(bpCategory, incomingSubCategories);

            Long effectiveUpdatedBy = request.getUpdatedBy() != null ? request.getUpdatedBy() : bpCategory.getUpdatedBy();
            Map<String, Object> changedFields = new HashMap<>();
            addChangedField(changedFields, "categoryName", bpCategory.getCategoryName(), trimmedCategoryName);
            addChangedField(changedFields, "subCategories", existingSubCategoryNames,
                    new ArrayList<>(incomingSubCategories));
            addChangedField(changedFields, "updatedBy", bpCategory.getUpdatedBy(), effectiveUpdatedBy);

            bpCategory.setIsUnderApproval(UNDER_APPROVAL);
            bpCategoryRepository.save(bpCategory);

            BPCategory newCategory = mapper.toBPCategory(request, bpCategory.getConfiguration());
            newCategory.setCategoryName(trimmedCategoryName);
            newCategory.setConfiguration(bpCategory.getConfiguration());
            newCategory.setCreatedBy(bpCategory.getCreatedBy());
            newCategory.setUpdatedBy(effectiveUpdatedBy);
            newCategory.setIsActive(INACTIVE);
            newCategory.setIsUnderApproval(UNDER_APPROVAL);
            newCategory.setVersion(bpCategory.getVersion() != null ? bpCategory.getVersion() + 1 : INITIAL_VERSION);
            newCategory = bpCategoryRepository.save(newCategory);

            List<BPSubCategory> newSubCategories = new ArrayList<>();
            for (String subCategoryName : incomingSubCategories) {
                BPSubCategory subCategory = BPSubCategory.builder().category(newCategory)
                        .subCategoryName(subCategoryName)
                        .createdBy(bpCategory.getCreatedBy())
                        .updatedBy(effectiveUpdatedBy)
                        .build();
                subCategory = bpSubCategoryRepository.save(subCategory);
                newSubCategories.add(subCategory);
            }
            newCategory.setSubCategories(newSubCategories);

            configurationChangeService.addConfigurationChangeRequest(bpCategory.getConfiguration(), newCategory.getCategoryId(), bpCategory.getCategoryId(),
                    Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, UPDATE,
                    bpCategory.getConfiguration().getBusinessPartner().getCompany(), effectiveUpdatedBy,
                    bpCategory.getConfiguration().getBusinessPartner().getMappedCompany(), Boolean.FALSE, request.getRemarks());

            if (!changedFields.isEmpty()) {
                String newSnapshot = safeSerialize(buildCategorySnapshot(newCategory));
                String changedStr = safeSerialize(changedFields);
                Long companyId = null;
                try {
                    if (bpCategory.getConfiguration() != null && bpCategory.getConfiguration().getBusinessPartner() != null
                            && bpCategory.getConfiguration().getBusinessPartner().getCompany() != null) {
                        companyId = bpCategory.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                    }
                } catch (Exception ignore) {
                }
                String userName = effectiveUpdatedBy != null ? commonService.loadUserNameByUserId(effectiveUpdatedBy, request.getIsUpdatedByAdmin()) : null;
                auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "Category", "BPCategory",
                        newCategory.getCategoryId(),
                        UPDATE,
                        newSnapshot,
                        oldValue,
                        changedStr,
                        effectiveUpdatedBy,
                        userName,
                        companyId,
                        SUCCESS,
                        null));
            }

            return mapper.toBPCategoryVo(newCategory);
        } catch (FlickzzDeskException e) {
            try {
                Long entityId = bpCategory != null ? bpCategory.getCategoryId() : null;
                Long companyId = null;
                String oldSnap = null;
                if (bpCategory != null) {
                    try {
                        oldSnap = objectMapper.writeValueAsString(buildCategorySnapshot(bpCategory));
                    } catch (Exception ignore) {
                    }
                    try {
                        if (bpCategory.getConfiguration() != null && bpCategory.getConfiguration().getBusinessPartner() != null
                                && bpCategory.getConfiguration().getBusinessPartner().getCompany() != null) {
                            companyId = bpCategory.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                        }
                    } catch (Exception ignore) {
                    }
                }
                String newSnap = null;
                if (request != null) {
                    try {
                        Map<String, Object> attempted = new HashMap<>();
                        attempted.put("categoryName", request.getCategoryName());
                        attempted.put("updatedBy", request.getUpdatedBy());
                        newSnap = objectMapper.writeValueAsString(attempted);
                    } catch (Exception ignore) {
                    }
                }
                auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Category", "BPCategory",
                        entityId, UPDATE, newSnap, oldSnap, null,
                        request != null ? request.getUpdatedBy() : null,
                        request != null && request.getUpdatedBy() != null ? commonService.loadUserNameByUserId(request.getUpdatedBy(), request.getIsUpdatedByAdmin()) : null, companyId, FAILED, e.getDescription()), e);
            } catch (Exception ignore) {
            }
            throw e;
        } catch (Exception e) {
            try {
                Long entityId = bpCategory != null ? bpCategory.getCategoryId() : null;
                Long companyId = null;
                String oldSnap = null;
                if (bpCategory != null) {
                    try {
                        oldSnap = objectMapper.writeValueAsString(buildCategorySnapshot(bpCategory));
                    } catch (Exception ignore) {
                    }
                    try {
                        if (bpCategory.getConfiguration() != null && bpCategory.getConfiguration().getBusinessPartner() != null
                                && bpCategory.getConfiguration().getBusinessPartner().getCompany() != null) {
                            companyId = bpCategory.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                        }
                    } catch (Exception ignore) {
                    }
                }
                String newSnap = null;
                if (request != null) {
                    try {
                        Map<String, Object> attempted = new HashMap<>();
                        attempted.put("categoryName", request.getCategoryName());
                        attempted.put("updatedBy", request.getUpdatedBy());
                        newSnap = objectMapper.writeValueAsString(attempted);
                    } catch (Exception ignore) {
                    }
                }
                auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Category", "BPCategory",
                        entityId, UPDATE, newSnap, oldSnap, null,
                        request != null ? request.getUpdatedBy() : null,
                        request != null && request.getUpdatedBy() != null ? commonService.loadUserNameByUserId(request.getUpdatedBy(), request.getIsUpdatedByAdmin()) : null, companyId, FAILED, e.getMessage()), e);
            } catch (Exception ignore) {
            }
            log.error("Exception in updateBusinessPartnerCategoryConfiguration method in BusinessPartnerService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    void validateSubCategoryAssignmentsForRemoval(BPCategory bpCategory, Set<String> incomingSubCategories) {
        if (bpCategory == null || bpCategory.getConfiguration() == null || incomingSubCategories == null) {
            return;
        }

        List<BPSubCategory> existingSubCategories = bpSubCategoryRepository
                .findByCategoryCategoryIdAndIsActive(bpCategory.getCategoryId(), ACTIVE);
        for (BPSubCategory subCategory : existingSubCategories) {
            if (incomingSubCategories.contains(subCategory.getSubCategoryName())) {
                continue;
            }
            validateSubCategoryAssignment(subCategory, bpCategory.getConfiguration().getConfigurationId());
        }
    }

    private void validateSubCategoryAssignment(BPSubCategory subCategory, Long configurationId) {
        if (subCategory == null) {
            return;
        }
        boolean hasAssignment = bpAssignmentRepository
                .findByConfigurationConfigurationIdAndSubCategorySubCategoryIdAndIsActive(
                        configurationId, subCategory.getSubCategoryId(), ACTIVE)
                .isPresent();
        if (hasAssignment) {
            throw new FlickzzDeskException(INVALID_FIELD,
                    "SubCategory " + subCategory.getSubCategoryName() + " cannot be removed as it has existing assignment");
        }
    }

    public void deleteBusinessPartnerCategoryConfiguration(BpConfigRequestVO request) {
        log.info(generateLog("deleteBusinessPartnerCategoryConfiguration", this.getClass().getName()));
        BPCategory bpCategory = null;
        try {
            Optional<BPCategory> existingCategory = bpCategoryRepository
                    .findByCategoryIdAndIsActive(Long.valueOf(request.getCategoryId()), ACTIVE);
            if (existingCategory.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), CATEGORY));
            }

            bpCategory = existingCategory.get();

            // Capture old snapshot before deletion
            String oldValue = null;
            try {
                oldValue = objectMapper.writeValueAsString(buildCategorySnapshot(bpCategory));
            } catch (Exception ignore) {
            }

            validateSubCategoryAssignmentsForDeletion(bpCategory);

            bpCategory.setIsUnderApproval(UNDER_APPROVAL);
            bpCategoryRepository.save(bpCategory);
            // Create a new inactive copy for delete (leave existing record untouched)
            BPCategory newCategory = BPCategory.builder()
                    .categoryName(bpCategory.getCategoryName())
                    .configuration(bpCategory.getConfiguration())
                    .createdBy(bpCategory.getCreatedBy())
                    .updatedBy(request.getDeletedBy() != null ? request.getDeletedBy() : bpCategory.getUpdatedBy())
                    .isActive(INACTIVE)
                    .version(bpCategory.getVersion() != null ? bpCategory.getVersion() + 1 : INITIAL_VERSION)
                    .build();
            // ensure new entity will be inserted (no id)
            newCategory.setCategoryId(null);
            newCategory = bpCategoryRepository.save(newCategory);

            List<BPSubCategory> existingSubCategories = bpSubCategoryRepository
                    .findByCategoryCategoryIdAndIsActive(bpCategory.getCategoryId(), ACTIVE);
            List<BPSubCategory> newSubCategories = new ArrayList<>();
            for (BPSubCategory subCategory : existingSubCategories) {
                BPSubCategory newSub = BPSubCategory.builder().category(newCategory)
                        .subCategoryName(subCategory.getSubCategoryName())
                        .createdBy(subCategory.getCreatedBy())
                        .updatedBy(request.getDeletedBy())
                        .isActive(Boolean.FALSE)
                        .isUnderApproval(UNDER_APPROVAL)
                        .build();
                newSub = bpSubCategoryRepository.save(newSub);
                newSubCategories.add(newSub);
            }
            newCategory.setSubCategories(newSubCategories);

            configurationChangeService.addConfigurationChangeRequest(newCategory.getConfiguration(), newCategory.getCategoryId(), bpCategory.getCategoryId(),
                    Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, DELETE,
                    newCategory.getConfiguration().getBusinessPartner().getCompany(), request.getDeletedBy(),
                    newCategory.getConfiguration().getBusinessPartner().getMappedCompany(), Boolean.FALSE, request.getRemarks());

            // Record DELETE audit
            Long companyId = null;
            try {
                if (bpCategory.getConfiguration() != null && bpCategory.getConfiguration().getBusinessPartner() != null
                        && bpCategory.getConfiguration().getBusinessPartner().getCompany() != null) {
                    companyId = bpCategory.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                }
            } catch (Exception ignore) {
            }
            auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "Category", "BPCategory",
                    bpCategory.getCategoryId(),
                    DELETE,
                    oldValue,
                    null,
                    null,
                    request.getDeletedBy(),
                    commonService.loadUserNameByUserId(Long.valueOf(request.getDeletedBy()), request.getIsDeletedByAdmin()),
                    companyId,
                    SUCCESS,
                    null));
        } catch (FlickzzDeskException e) {
            // record FlickzzDeskException as ERROR audit and rethrow
            try {
                Long entityId = null;
                Long companyId = null;
                try {
                    entityId = Long.valueOf(request.getCategoryId());
                } catch (Exception ignore) {
                }
                if (bpCategory != null && bpCategory.getConfiguration() != null
                        && bpCategory.getConfiguration().getBusinessPartner() != null
                        && bpCategory.getConfiguration().getBusinessPartner().getCompany() != null) {
                    companyId = bpCategory.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                }
                auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Category", "BPCategory",
                        entityId, DELETE,
                        null, null, null, request.getDeletedBy(), commonService.loadUserNameByUserId(Long.valueOf(request.getDeletedBy()), request.getIsDeletedByAdmin()), companyId, FAILED, e.getDescription()), e);
            } catch (Exception ignore) {
            }
            throw e;
        } catch (Exception e) {
            // record generic exception as ERROR audit
            try {
                Long entityId = null;
                Long companyId = null;
                try {
                    entityId = Long.valueOf(request.getCategoryId());
                } catch (Exception ignore) {
                }
                if (bpCategory != null && bpCategory.getConfiguration() != null
                        && bpCategory.getConfiguration().getBusinessPartner() != null
                        && bpCategory.getConfiguration().getBusinessPartner().getCompany() != null) {
                    companyId = bpCategory.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                }
                auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Category", "BPCategory",
                        entityId, DELETE,
                        null, null, null, request.getDeletedBy(), commonService.loadUserNameByUserId(Long.valueOf(request.getDeletedBy()), request.getIsDeletedByAdmin()), companyId, FAILED, e.getMessage()), e);
            } catch (Exception ignore) {
            }
            log.error("Exception in deleteBusinessPartnerCategoryConfiguration method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    void validateSubCategoryAssignmentsForDeletion(BPCategory bpCategory) {
        if (bpCategory == null || bpCategory.getConfiguration() == null) {
            return;
        }

        List<BPSubCategory> existingSubCategories = bpSubCategoryRepository
                .findByCategoryCategoryIdAndIsActive(bpCategory.getCategoryId(), ACTIVE);
        for (BPSubCategory subCategory : existingSubCategories) {
            validateSubCategoryAssignment(subCategory, bpCategory.getConfiguration().getConfigurationId());
        }
    }

    public BPCategoryVO getBusinessPartnerCategoryConfigurationById(Long categoryId) {
        log.info(generateLog("getBusinessPartnerCategoryConfigurationById", this.getClass().getName()));
        try {
            Optional<BPCategory> existingCategory = bpCategoryRepository.findById(categoryId);
            if (existingCategory.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), CATEGORY));
            }
            return mapper.toBPCategoryVo(existingCategory.get());
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getBusinessPartnerCategoryConfigurationById method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<BPCategoryVO> getBusinessPartnerCategoryConfiguration(Long businessPartnerId, Boolean isActiveList) {
        log.info(generateLog("getBusinessPartnerCategoryConfiguration", this.getClass().getName()));
        try {
            Optional<BusinessPartner> businessPartner = businessPartnerRepository
                    .findByBusinessPartnerIdAndIsActive(businessPartnerId, ACTIVE);
            if (!businessPartner.isPresent()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
            }

            Optional<BPConfiguration> existingConfig = bPConfigurationRepository
                    .findByBusinessPartnerBusinessPartnerIdAndIsActiveTrue(businessPartnerId);
            if (!existingConfig.isPresent()) {
                return Collections.emptyList();
            }

            List<BPCategory> categories;
            if (isActiveList) {
                categories = bpCategoryRepository
                        .findByConfigurationConfigurationIdAndIsActiveTrue(existingConfig.get().getConfigurationId());
            } else {
                categories = bpCategoryRepository
                        .findByConfigurationConfigurationId(existingConfig.get().getConfigurationId());
            }
            return categories.stream().map(mapper::toBPCategoryVo).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getBusinessPartnerCategoryConfiguration method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public BPSupportGroupVO createBusinessPartnerSupportGroupConfiguration(BpConfigRequestVO request) {
        log.info(generateLog("createBusinessPartnerSupportGroupConfiguration", this.getClass().getName()));
        try {
            if (request == null || request.getBusinessPartnerId() == null) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
            }
            if (request.getGroupName() == null || request.getGroupName().trim().isEmpty()) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Group name"));
            }
            if (request.getAgents() == null || request.getAgents().isEmpty()) {
                throw new FlickzzDeskException(SET_TEXT, getDescription(SET_TEXT.getDescription(), "agent list"));
            }

            Optional<BusinessPartner> businessPartner = businessPartnerRepository
                    .findByBusinessPartnerIdAndIsActive(request.getBusinessPartnerId(), ACTIVE);
            if (!businessPartner.isPresent()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
            }

            Optional<BPConfiguration> existingConfig = bPConfigurationRepository
                    .findByBusinessPartnerBusinessPartnerIdAndIsActiveTrue(request.getBusinessPartnerId());

            BPConfiguration config = new BPConfiguration();
            if (!existingConfig.isPresent()) {
                config.setBusinessPartner(businessPartner.get());
                config.setCreatedBy(request.getCreatedBy());
                config.setIsCreatorAdmin(request.getIsCreatedByAdmin());
                bPConfigurationRepository.save(config);
            } else {
                config = existingConfig.get();
            }

            if (bpSupportGroupRepository.existsByConfigurationConfigurationIdAndGroupNameAndIsActive(
                    config.getConfigurationId(), request.getGroupName().trim(), ACTIVE)) {
                throw new FlickzzDeskException(ALREADY_EXISTS,
                        getDescription(ALREADY_EXISTS.getDescription(), "Support group"));
            }

            Set<Long> agentIds = new LinkedHashSet<>();
            for (Long agentId : request.getAgents()) {
                if (agentId == null) {
                    continue;
                }
                agentIds.add(agentId);
            }
            if (agentIds.isEmpty()) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Agent list"));
            }

            for (Long agentId : agentIds) {
                Optional<AgentMaster> agent = agentMasterRepository.findById(agentId);
                if (agent.isEmpty() || !agent.get().getIsActive()) {
                    throw new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), AGENT));
                }
            }

            BPSupportGroup supportGroup = BPSupportGroup.builder().configuration(config)
                    .groupName(request.getGroupName().trim()).createdBy(request.getCreatedBy())
                    .isActive(INACTIVE).isUnderApproval(UNDER_APPROVAL)
                    .updatedBy(request.getUpdatedBy()).build();
            bpSupportGroupRepository.save(supportGroup);

            configurationChangeService.addConfigurationChangeRequest(config, supportGroup.getSupportGroupId(), null, Boolean.FALSE, Boolean.FALSE,
                    Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, CREATE,
                    businessPartner.get().getCompany(), request.getCreatedBy(),
                    businessPartner.get().getMappedCompany(), request.getIsCreatedByAdmin(), request.getRemarks());

            for (Long agentId : agentIds) {
                AgentMaster agent = agentMasterRepository.findById(agentId)
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), AGENT)));
                BPSupportGroupMember member = BPSupportGroupMember.builder().supportGroup(supportGroup).agent(agent)
                        .isGroupLead(Boolean.FALSE).build();
                bpSupportGroupMemberRepository.save(member);
            }

            // Handle managers (internal and business partner managers)
            Set<Long> internalManagerIds = new LinkedHashSet<>();
            if (request.getManagerInternalAgents() != null) {
                for (Long mId : request.getManagerInternalAgents()) {
                    if (mId != null) {
                        internalManagerIds.add(mId);
                    }
                }
            }

            Set<Long> bpManagerIds = new LinkedHashSet<>();
            if (request.getManagerBpAgents() != null) {
                for (Long mId : request.getManagerBpAgents()) {
                    if (mId != null) {
                        bpManagerIds.add(mId);
                    }
                }
            }

            // validate manager agents exist (if any)
            Set<Long> allManagerIds = new LinkedHashSet<>();
            allManagerIds.addAll(internalManagerIds);
            allManagerIds.addAll(bpManagerIds);
            for (Long managerId : allManagerIds) {
                if (managerId == null) continue;
                Optional<AgentMaster> agent = agentMasterRepository.findById(managerId);
                if (agent.isEmpty() || !agent.get().getIsActive()) {
                    throw new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), AGENT));
                }
            }

            // save internal managers
            for (Long managerId : internalManagerIds) {
                AgentMaster agent = agentMasterRepository.findById(managerId)
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), AGENT)));
                BPSupportGroupManager manager = BPSupportGroupManager.builder().supportGroup(supportGroup)
                        .agent(agent).isInternal(Boolean.TRUE).isBP(Boolean.FALSE).build();
                bpSupportGroupManagerRepository.save(manager);
            }

            // save BP managers
            for (Long managerId : bpManagerIds) {
                AgentMaster agent = agentMasterRepository.findById(managerId)
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), AGENT)));
                BPSupportGroupManager manager = BPSupportGroupManager.builder().supportGroup(supportGroup)
                        .agent(agent).isInternal(Boolean.FALSE).isBP(Boolean.TRUE).build();
                bpSupportGroupManagerRepository.save(manager);
            }

            // Prepare new snapshot for audit
            String newSnapshot = null;
            try {
                newSnapshot = objectMapper.writeValueAsString(buildSupportGroupSnapshot(supportGroup, agentIds, internalManagerIds, bpManagerIds));
            } catch (Exception ignore) {
            }

            // Record CREATE audit
            java.util.Map<String, Object> changed = new java.util.HashMap<>();
            changed.put("groupName", supportGroup.getGroupName());
            changed.put("members", agentIds);
            changed.put("internalManagers", internalManagerIds);
            changed.put("bpManagers", bpManagerIds);
            String changedStr = null;
            try {
                changedStr = objectMapper.writeValueAsString(changed);
            } catch (Exception ignore) {
            }

            auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "SupportGroup", "BPSupportGroup",
                    supportGroup.getSupportGroupId(),
                    CREATE,
                    newSnapshot,
                    null,
                    changedStr,
                    request.getCreatedBy(),
                    commonService.loadUserNameByUserId(Long.valueOf(request.getCreatedBy()), request.getIsCreatedByAdmin()),
                    request.getOrgId(),
                    SUCCESS,
                    null));

            return mapper.toSupportGroupVo(supportGroup);
        } catch (FlickzzDeskException e) {
            // record FlickzzDeskException as ERROR audit and rethrow
            String attempted = null;
            try {
                java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
                attemptedMap.put("groupName", request != null ? request.getGroupName() : null);
                attemptedMap.put("members", request != null ? request.getAgents() : null);
                attemptedMap.put("internalManagers", request != null ? request.getManagerInternalAgents() : null);
                attemptedMap.put("bpManagers", request != null ? request.getManagerBpAgents() : null);
                attempted = objectMapper.writeValueAsString(attemptedMap);
            } catch (Exception ignore) {
            }
            auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SupportGroup", "BPSupportGroup", null, CREATE,
                    attempted, null, null, request != null ? request.getCreatedBy() : null,
                    commonService.loadUserNameByUserId(Long.valueOf(request.getCreatedBy()), request.getIsCreatedByAdmin()), (request != null ? request.getOrgId() : null), FAILED, e.getDescription()), e);
            throw e;
        } catch (Exception e) {
            String attempted = null;
            try {
                java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
                attemptedMap.put("groupName", request != null ? request.getGroupName() : null);
                attemptedMap.put("members", request != null && request.getAgents() != null ? request.getAgents() : null);
                attemptedMap.put("internalManagers", request != null ? request.getManagerInternalAgents() : null);
                attemptedMap.put("bpManagers", request != null ? request.getManagerBpAgents() : null);
                attempted = objectMapper.writeValueAsString(attemptedMap);
            } catch (Exception ignore) {
            }
            auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SupportGroup", "BPSupportGroup",
                    null,
                    CREATE,
                    attempted,
                    null,
                    null,
                    request != null ? request.getCreatedBy() : null,
                    commonService.loadUserNameByUserId(Long.valueOf(request.getCreatedBy()), request.getIsCreatedByAdmin()),
                    request != null ? request.getOrgId() : null,
                    FAILED,
                    e.getMessage()), e);
            log.error("Exception in createBusinessPartnerSupportGroupConfiguration method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public BPSupportGroupVO updateBusinessPartnerSupportGroupConfiguration(BpConfigRequestVO request) {
        log.info(generateLog("updateBusinessPartnerSupportGroupConfiguration", this.getClass().getName()));
        BPSupportGroup supportGroup = null;
        Long auditUserId = null;
        try {
            if (request == null || request.getSupportGroupId() == null) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Support group"));
            }
            String trimmedGroupName = request.getGroupName() == null ? null : request.getGroupName().trim();
            if (trimmedGroupName == null || trimmedGroupName.isEmpty()) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Group name"));
            }
            if (request.getAgents() == null || request.getAgents().isEmpty()) {
                throw new FlickzzDeskException(SET_TEXT, getDescription(SET_TEXT.getDescription(), "agent list"));
            }

            Optional<BPSupportGroup> existingGroup = bpSupportGroupRepository
                    .findBySupportGroupIdAndIsActive(request.getSupportGroupId(), ACTIVE);
            if (existingGroup.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Support group"));
            } else if (existingGroup.get().getIsUnderApproval() != null && existingGroup.get().getIsUnderApproval()) {
                throw new FlickzzDeskException(ALREADY_EXISTS,
                        "Support group change is already under approval. Please wait for the approval process to complete.");
            }

            supportGroup = existingGroup.get();
            Long effectiveUpdatedBy = request.getUpdatedBy() != null ? request.getUpdatedBy() : supportGroup.getUpdatedBy();
            auditUserId = effectiveUpdatedBy;
            String oldValue = safeSerialize(buildSupportGroupSnapshot(supportGroup,
                    supportGroup.getMembers().stream().filter(BPSupportGroupMember::getIsActive).map(member -> member.getAgent().getAgentId()).collect(Collectors.toSet()),
                    supportGroup.getManagers().stream().filter(m -> Boolean.TRUE.equals(m.getIsActive()) && Boolean.TRUE.equals(m.getIsInternal())).map(manager -> manager.getAgent().getAgentId()).collect(Collectors.toSet()),
                    supportGroup.getManagers().stream().filter(m -> Boolean.TRUE.equals(m.getIsActive()) && Boolean.TRUE.equals(m.getIsBP())).map(manager -> manager.getAgent().getAgentId()).collect(Collectors.toSet())));

            Map<String, Object> changedFields = new HashMap<>();
            addChangedField(changedFields, "groupName", supportGroup.getGroupName(), trimmedGroupName);
            addChangedField(changedFields, "updatedBy", supportGroup.getUpdatedBy(), effectiveUpdatedBy);

            Set<Long> incomingAgentIds = new LinkedHashSet<>();
            for (Long agentId : request.getAgents()) {
                if (agentId != null) {
                    incomingAgentIds.add(agentId);
                }
            }
            if (incomingAgentIds.isEmpty()) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Agent list"));
            }
            for (Long agentId : incomingAgentIds) {
                Optional<AgentMaster> agent = agentMasterRepository.findById(agentId);
                if (agent.isEmpty() || !agent.get().getIsActive()) {
                    throw new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), AGENT));
                }
            }

            Set<Long> incomingInternalManagerIds = new LinkedHashSet<>();
            if (request.getManagerInternalAgents() != null) {
                for (Long mId : request.getManagerInternalAgents()) {
                    if (mId != null) incomingInternalManagerIds.add(mId);
                }
            }

            Set<Long> incomingBpManagerIds = new LinkedHashSet<>();
            if (request.getManagerBpAgents() != null) {
                for (Long mId : request.getManagerBpAgents()) {
                    if (mId != null) incomingBpManagerIds.add(mId);
                }
            }

            Set<Long> existingActiveAgentIds = supportGroup.getMembers().stream()
                    .filter(BPSupportGroupMember::getIsActive)
                    .map(member -> member.getAgent().getAgentId())
                    .collect(Collectors.toSet());
            if (!existingActiveAgentIds.equals(incomingAgentIds)) {
                changedFields.put("members", true);
            }

            Set<Long> existingActiveInternalManagerIds = supportGroup.getManagers().stream()
                    .filter(m -> Boolean.TRUE.equals(m.getIsActive()) && Boolean.TRUE.equals(m.getIsInternal()))
                    .map(manager -> manager.getAgent().getAgentId())
                    .collect(Collectors.toSet());
            if (!existingActiveInternalManagerIds.equals(incomingInternalManagerIds)) {
                changedFields.put("internalManagers", true);
            }

            Set<Long> existingActiveBpManagerIds = supportGroup.getManagers().stream()
                    .filter(m -> Boolean.TRUE.equals(m.getIsActive()) && Boolean.TRUE.equals(m.getIsBP()))
                    .map(manager -> manager.getAgent().getAgentId())
                    .collect(Collectors.toSet());
            if (!existingActiveBpManagerIds.equals(incomingBpManagerIds)) {
                changedFields.put("bpManagers", true);
            }

            supportGroup.setIsUnderApproval(ACTIVE);
            bpSupportGroupRepository.save(supportGroup);

            BPSupportGroup newSupportGroup = BPSupportGroup.builder()
                    .configuration(supportGroup.getConfiguration())
                    .groupName(trimmedGroupName)
                    .createdBy(supportGroup.getCreatedBy())
                    .updatedBy(effectiveUpdatedBy)
                    .isActive(INACTIVE)
                    .isUnderApproval(UNDER_APPROVAL)
                    .build();
            newSupportGroup.setIsActive(INACTIVE);
            newSupportGroup.setVersion(supportGroup.getVersion() != null ? supportGroup.getVersion() + 1 : INITIAL_VERSION);
            newSupportGroup = bpSupportGroupRepository.save(newSupportGroup);

            for (Long agentId : incomingAgentIds) {
                AgentMaster agent = agentMasterRepository.findById(agentId)
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), AGENT)));
                BPSupportGroupMember member = BPSupportGroupMember.builder().supportGroup(newSupportGroup).agent(agent)
                        .isGroupLead(Boolean.FALSE).build();
                bpSupportGroupMemberRepository.save(member);
            }

            for (Long managerId : incomingInternalManagerIds) {
                AgentMaster agent = agentMasterRepository.findById(managerId)
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), AGENT)));
                BPSupportGroupManager manager = BPSupportGroupManager.builder().supportGroup(newSupportGroup)
                        .agent(agent).isInternal(Boolean.TRUE).isBP(Boolean.FALSE).build();
                bpSupportGroupManagerRepository.save(manager);
            }

            for (Long managerId : incomingBpManagerIds) {
                AgentMaster agent = agentMasterRepository.findById(managerId)
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), AGENT)));
                BPSupportGroupManager manager = BPSupportGroupManager.builder().supportGroup(newSupportGroup)
                        .agent(agent).isInternal(Boolean.FALSE).isBP(Boolean.TRUE).build();
                bpSupportGroupManagerRepository.save(manager);
            }

            configurationChangeService.addConfigurationChangeRequest(supportGroup.getConfiguration(), newSupportGroup.getSupportGroupId(), supportGroup.getSupportGroupId(),
                    Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, UPDATE,
                    supportGroup.getConfiguration().getBusinessPartner().getCompany(), effectiveUpdatedBy,
                    supportGroup.getConfiguration().getBusinessPartner().getMappedCompany(), Boolean.FALSE, request.getRemarks());

            if (!changedFields.isEmpty()) {
                String newSnapshot = safeSerialize(buildSupportGroupSnapshot(newSupportGroup, incomingAgentIds, incomingInternalManagerIds, incomingBpManagerIds));
                String changedStr = safeSerialize(changedFields);
                Long companyId = null;
                try {
                    if (supportGroup.getConfiguration() != null && supportGroup.getConfiguration().getBusinessPartner() != null
                            && supportGroup.getConfiguration().getBusinessPartner().getCompany() != null) {
                        companyId = supportGroup.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                    }
                } catch (Exception ignore) {
                }
                String userName = effectiveUpdatedBy != null ? commonService.loadUserNameByUserId(effectiveUpdatedBy, request.getIsUpdatedByAdmin()) : null;
                auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "SupportGroup", "BPSupportGroup",
                        newSupportGroup.getSupportGroupId(),
                        UPDATE,
                        newSnapshot,
                        oldValue,
                        changedStr,
                        effectiveUpdatedBy,
                        userName,
                        companyId,
                        SUCCESS,
                        null));
            }

            return mapper.toSupportGroupVo(newSupportGroup);
        } catch (FlickzzDeskException e) {
            try {
                Long entityId = supportGroup != null ? supportGroup.getSupportGroupId() : null;
                Long companyId = null;
                String oldSnap = null;
                if (supportGroup != null) {
                    try {
                        oldSnap = objectMapper.writeValueAsString(buildSupportGroupSnapshot(supportGroup, supportGroup.getMembers().stream().map(member -> member.getAgent().getAgentId()).collect(Collectors.toSet()),
                                supportGroup.getManagers().stream().filter(manager -> manager.getIsInternal() == Boolean.TRUE).map(internalManager -> internalManager.getAgent().getAgentId()).collect(Collectors.toSet()),
                                supportGroup.getManagers().stream().filter(manager -> manager.getIsBP() == Boolean.TRUE).map(bpManager -> bpManager.getAgent().getAgentId()).collect(Collectors.toSet())));
                    } catch (Exception ignore) {
                    }
                    try {
                        if (supportGroup.getConfiguration() != null && supportGroup.getConfiguration().getBusinessPartner() != null
                                && supportGroup.getConfiguration().getBusinessPartner().getCompany() != null) {
                            companyId = supportGroup.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                        }
                    } catch (Exception ignore) {
                    }
                }
                String newSnap = null;
                if (request != null) {
                    try {
                        Map<String, Object> attempted = new HashMap<>();
                        attempted.put("groupName", request.getGroupName());
                        attempted.put("agentCount", request.getAgents() != null ? request.getAgents().size() : 0);
                        attempted.put("updatedBy", request.getUpdatedBy());
                        newSnap = objectMapper.writeValueAsString(attempted);
                    } catch (Exception ignore) {
                    }
                }
                auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SupportGroup", "BPSupportGroup",
                        entityId, UPDATE, newSnap, oldSnap, null,
                        auditUserId,
                        auditUserId != null ? commonService.loadUserNameByUserId(auditUserId, request.getIsUpdatedByAdmin()) : null, companyId, FAILED, e.getDescription()), e);
            } catch (Exception ignore) {
            }
            throw e;
        } catch (Exception e) {
            try {
                Long entityId = supportGroup != null ? supportGroup.getSupportGroupId() : null;
                Long companyId = null;
                String oldSnap = null;
                if (supportGroup != null) {
                    try {
                        oldSnap = objectMapper.writeValueAsString(buildSupportGroupSnapshot(supportGroup, supportGroup.getMembers().stream().map(member -> member.getAgent().getAgentId()).collect(Collectors.toSet()),
                                supportGroup.getManagers().stream().filter(manager -> manager.getIsInternal() == Boolean.TRUE).map(internalManager -> internalManager.getAgent().getAgentId()).collect(Collectors.toSet()),
                                supportGroup.getManagers().stream().filter(manager -> manager.getIsBP() == Boolean.TRUE).map(bpManager -> bpManager.getAgent().getAgentId()).collect(Collectors.toSet())));
                    } catch (Exception ignore) {
                    }
                    try {
                        if (supportGroup.getConfiguration() != null && supportGroup.getConfiguration().getBusinessPartner() != null
                                && supportGroup.getConfiguration().getBusinessPartner().getCompany() != null) {
                            companyId = supportGroup.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                        }
                    } catch (Exception ignore) {
                    }
                }
                String newSnap = null;
                if (request != null) {
                    try {
                        Map<String, Object> attempted = new HashMap<>();
                        attempted.put("groupName", request.getGroupName());
                        attempted.put("agentCount", request.getAgents() != null ? request.getAgents().size() : 0);
                        attempted.put("updatedBy", request.getUpdatedBy());
                        newSnap = objectMapper.writeValueAsString(attempted);
                    } catch (Exception ignore) {
                    }
                }
                auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SupportGroup", "BPSupportGroup",
                        entityId, UPDATE, newSnap, oldSnap, null,
                        auditUserId,
                        auditUserId != null ? commonService.loadUserNameByUserId(auditUserId, request.getIsUpdatedByAdmin()) : null, companyId, FAILED, e.getMessage()), e);
            } catch (Exception ignore) {
            }
            log.error("Exception in updateBusinessPartnerSupportGroupConfiguration method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public void deleteBusinessPartnerSupportGroupConfiguration(BpConfigRequestVO request) {
        log.info(generateLog("deleteBusinessPartnerSupportGroupConfiguration", this.getClass().getName()));
        BPSupportGroup supportGroup = null;
        try {
            Optional<BPSupportGroup> existingGroup = bpSupportGroupRepository
                    .findBySupportGroupIdAndIsActive(Long.valueOf(request.getSupportGroupId()), ACTIVE);
            if (existingGroup.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Support group"));
            } else if (existingGroup.get().getIsUnderApproval() != null && existingGroup.get().getIsUnderApproval()) {
                throw new FlickzzDeskException(ALREADY_EXISTS,
                        "Support group change is already under approval. Please wait for the approval process to complete.");
            }

            supportGroup = existingGroup.get();

            // Capture old snapshot before deletion
            String oldValue = null;
            try {
                oldValue = objectMapper.writeValueAsString(buildSupportGroupSnapshot(supportGroup,
                        supportGroup.getMembers().stream().map(member -> member.getAgent().getAgentId()).collect(Collectors.toSet()),
                        supportGroup.getManagers().stream().filter(manager -> manager.getIsInternal()).map(manager -> manager.getAgent().getAgentId()).collect(Collectors.toSet()),
                        supportGroup.getManagers().stream().filter(manager -> manager.getIsBP()).map(manager -> manager.getAgent().getAgentId()).collect(Collectors.toSet())));
            } catch (Exception ignore) {
            }

            validateSupportGroupAssignmentForRemoval(supportGroup);

            supportGroup.setIsUnderApproval(ACTIVE);
            bpSupportGroupRepository.save(supportGroup);

            // Create a new inactive copy for delete (leave existing record untouched)
            BPSupportGroup newGroup = BPSupportGroup.builder()
                    .configuration(supportGroup.getConfiguration())
                    .groupName(supportGroup.getGroupName())
                    .version(supportGroup.getVersion() != null ? supportGroup.getVersion() + 1 : 1)
                    .createdBy(supportGroup.getCreatedBy())
                    .updatedBy(request.getDeletedBy() != null ? request.getDeletedBy() : supportGroup.getUpdatedBy())
                    .isActive(INACTIVE)
                    .isUnderApproval(UNDER_APPROVAL)
                    .build();
            // ensure new entity will be inserted (no id)
            newGroup.setSupportGroupId(null);
            newGroup = bpSupportGroupRepository.save(newGroup);

            // copy members (mark inactive) and attach to new group
            List<BPSupportGroupMember> members = bpSupportGroupMemberRepository.findBySupportGroupSupportGroupIdAndIsActive(supportGroup.getSupportGroupId(), ACTIVE);
            List<BPSupportGroupMember> newMembers = new ArrayList<>();
            for (BPSupportGroupMember member : members) {
                BPSupportGroupMember newMember = BPSupportGroupMember.builder()
                        .supportGroup(newGroup)
                        .agent(member.getAgent())
                        .isGroupLead(member.getIsGroupLead())
                        .isActive(Boolean.FALSE)
                        .build();
                newMember = bpSupportGroupMemberRepository.save(newMember);
                newMembers.add(newMember);
            }
            newGroup.setMembers(newMembers);

            // copy managers (mark inactive) and attach to new group
            List<BPSupportGroupManager> managers = bpSupportGroupManagerRepository.findBySupportGroupSupportGroupIdAndIsActive(supportGroup.getSupportGroupId(), ACTIVE);
            List<BPSupportGroupManager> newManagers = new ArrayList<>();
            for (BPSupportGroupManager manager : managers) {
                BPSupportGroupManager newManager = BPSupportGroupManager.builder()
                        .supportGroup(newGroup)
                        .agent(manager.getAgent())
                        .isInternal(manager.getIsInternal())
                        .isBP(manager.getIsBP())
                        .isActive(Boolean.FALSE)
                        .build();
                newManager = bpSupportGroupManagerRepository.save(newManager);
                newManagers.add(newManager);
            }
            newGroup.setManagers(newManagers);

            configurationChangeService.addConfigurationChangeRequest(newGroup.getConfiguration(), newGroup.getSupportGroupId(), supportGroup.getSupportGroupId(),
                    Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE, DELETE,
                    newGroup.getConfiguration().getBusinessPartner().getCompany(), request.getDeletedBy(),
                    newGroup.getConfiguration().getBusinessPartner().getMappedCompany(), Boolean.FALSE, request.getRemarks());

            // Record DELETE audit
            Long companyId = null;
            try {
                if (supportGroup.getConfiguration() != null && supportGroup.getConfiguration().getBusinessPartner() != null
                        && supportGroup.getConfiguration().getBusinessPartner().getCompany() != null) {
                    companyId = supportGroup.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                }
            } catch (Exception ignore) {
            }
            auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "SupportGroup", "BPSupportGroup",
                    supportGroup.getSupportGroupId(),
                    DELETE,
                    oldValue,
                    null,
                    null,
                    request.getDeletedBy(),
                    commonService.loadUserNameByUserId(Long.valueOf(request.getDeletedBy()), request.getIsDeletedByAdmin()),
                    companyId,
                    SUCCESS,
                    null));
        } catch (FlickzzDeskException e) {
            // record FlickzzDeskException as ERROR audit and rethrow
            try {
                Long entityId = null;
                Long companyId = null;
                try {
                    entityId = request.getSupportGroupId();
                } catch (Exception ignore) {
                }
                if (supportGroup != null && supportGroup.getConfiguration() != null
                        && supportGroup.getConfiguration().getBusinessPartner() != null
                        && supportGroup.getConfiguration().getBusinessPartner().getCompany() != null) {
                    companyId = supportGroup.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                }
                auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SupportGroup", "BPSupportGroup",
                        entityId, DELETE,
                        null, null, null, request.getDeletedBy(), commonService.loadUserNameByUserId(Long.valueOf(request.getDeletedBy()), request.getIsDeletedByAdmin()), companyId, FAILED, e.getDescription()), e);
            } catch (Exception ignore) {
            }
            throw e;
        } catch (Exception e) {
            // record generic exception as ERROR audit
            try {
                Long entityId = null;
                Long companyId = null;
                try {
                    entityId = Long.valueOf(request.getSupportGroupId());
                } catch (Exception ignore) {
                }
                if (supportGroup != null && supportGroup.getConfiguration() != null
                        && supportGroup.getConfiguration().getBusinessPartner() != null
                        && supportGroup.getConfiguration().getBusinessPartner().getCompany() != null) {
                    companyId = supportGroup.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                }
                auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "SupportGroup", "BPSupportGroup",
                        entityId, DELETE,
                        null, null, null, request.getDeletedBy(), commonService.loadUserNameByUserId(Long.valueOf(request.getDeletedBy()), request.getIsDeletedByAdmin()), companyId, FAILED, e.getMessage()), e);
            } catch (Exception ignore) {
            }
            log.error("Exception in deleteBusinessPartnerSupportGroupConfiguration method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    private void validateSupportGroupAssignmentForRemoval(BPSupportGroup supportGroup) {
        if (supportGroup == null) {
            return;
        }
        boolean hasAssignment = bpAssignmentRepository
                .findByConfigurationConfigurationIdAndSupportGroupSupportGroupIdAndIsActive(
                        supportGroup.getConfiguration().getConfigurationId(), supportGroup.getSupportGroupId(), ACTIVE)
                .isPresent();
        if (hasAssignment) {
            throw new FlickzzDeskException(INVALID_FIELD,
                    supportGroup.getGroupName() + " cannot be removed as it has existing assignment");
        }
    }

    public BPSupportGroupVO getBusinessPartnerSupportGroupConfigurationById(Long supportGroupId) {
        log.info(generateLog("getBusinessPartnerSupportGroupConfigurationById", this.getClass().getName()));
        try {
            Optional<BPSupportGroup> existingGroup = bpSupportGroupRepository
                    .findById(supportGroupId);
            if (existingGroup.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Support group"));
            }
            return mapper.toSupportGroupVo(existingGroup.get());
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getBusinessPartnerSupportGroupConfigurationById method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<BPSupportGroupVO> getBusinessPartnerSupportGroupConfiguration(Long businessPartnerId) {
        log.info(generateLog("getBusinessPartnerSupportGroupConfiguration", this.getClass().getName()));
        try {
            Optional<BusinessPartner> businessPartner = businessPartnerRepository
                    .findByBusinessPartnerIdAndIsActive(businessPartnerId, ACTIVE);
            if (!businessPartner.isPresent()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
            }

            Optional<BPConfiguration> existingConfig = bPConfigurationRepository
                    .findByBusinessPartnerBusinessPartnerIdAndIsActiveTrue(businessPartnerId);
            if (!existingConfig.isPresent()) {
                return Collections.emptyList();
            }

            List<BPSupportGroup> groups = bpSupportGroupRepository
                    .findByConfigurationConfigurationId(existingConfig.get().getConfigurationId());
            return groups.stream().map(mapper::toNoBakcRefSupportGroupVo).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getBusinessPartnerSupportGroupConfiguration method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<BPSubCategoryVO> getBusinessPartnerSubCategoryConfiguration(Long valueOf) {

        log.info(generateLog("getBusinessPartnerSubCategoryConfiguration", this.getClass().getName()));
        try {
            Optional<BusinessPartner> businessPartner = businessPartnerRepository
                    .findByBusinessPartnerIdAndIsActive(valueOf, ACTIVE);
            if (!businessPartner.isPresent()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
            }

            Optional<BPConfiguration> existingConfig = bPConfigurationRepository
                    .findByBusinessPartnerBusinessPartnerIdAndIsActiveTrue(valueOf);
            if (!existingConfig.isPresent()) {
                return Collections.emptyList();
            }

            List<BPCategory> categories = bpCategoryRepository
                    .findByConfigurationConfigurationId(existingConfig.get().getConfigurationId());

            List<BPSubCategoryVO> subCategoryVOs = new ArrayList<>();
            for (BPCategory category : categories) {
                List<BPSubCategory> subCategories = bpSubCategoryRepository
                        .findByCategoryCategoryIdAndIsActive(category.getCategoryId(), ACTIVE);
                for (BPSubCategory subCategory : subCategories) {
                    BPSubCategoryVO vo = new BPSubCategoryVO();
                    vo.setSubCategoryId(subCategory.getSubCategoryId());
                    vo.setSubCategoryName(subCategory.getSubCategoryName());
                    subCategoryVOs.add(vo);
                }
            }
            return subCategoryVOs;
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getBusinessPartnerSubCategoryConfiguration method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public BPAssignmentVO createBusinessPartnerAssignmentConfiguration(BpConfigRequestVO request) {
        log.info(generateLog("createBusinessPartnerAssignmentConfiguration", this.getClass().getName()));
        try {
            if (request == null || request.getBusinessPartnerId() == null) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
            }
            if (request.getSupportGroupId() == null) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Support group"));
            }
            if (request.getSubCategoryId() == null) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Sub category"));
            }

            Optional<BusinessPartner> businessPartner = businessPartnerRepository
                    .findByBusinessPartnerIdAndIsActive(request.getBusinessPartnerId(), ACTIVE);
            if (!businessPartner.isPresent()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
            }

            Optional<BPConfiguration> existingConfig = bPConfigurationRepository
                    .findByBusinessPartnerBusinessPartnerIdAndIsActiveTrue(request.getBusinessPartnerId());
            BPConfiguration config = existingConfig.orElseGet(() -> {
                BPConfiguration newConfig = new BPConfiguration();
                newConfig.setBusinessPartner(businessPartner.get());
                newConfig.setCreatedBy(request.getCreatedBy());
                newConfig.setIsCreatorAdmin(request.getIsCreatedByAdmin());
                bPConfigurationRepository.save(newConfig);
                return newConfig;
            });

            Optional<BPSupportGroup> supportGroup = bpSupportGroupRepository
                    .findBySupportGroupIdAndIsActive(request.getSupportGroupId(), ACTIVE);
            if (supportGroup.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Support group"));
            }
            if (!Objects.equals(supportGroup.get().getConfiguration().getConfigurationId(),
                    config.getConfigurationId())) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Support group"));
            }

            Optional<BPSubCategory> subCategory = bpSubCategoryRepository
                    .findBySubCategoryIdAndIsActive(request.getSubCategoryId(), ACTIVE);
            if (subCategory.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Sub category"));
            }
            if (!Objects.equals(subCategory.get().getCategory().getConfiguration().getConfigurationId(),
                    config.getConfigurationId())) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Sub category"));
            }

            Optional<BPAssignment> existingAssignmentBySupportGroup = bpAssignmentRepository
                    .findByConfigurationConfigurationIdAndSupportGroupSupportGroupIdAndIsActive(
                            config.getConfigurationId(), request.getSupportGroupId(), ACTIVE);
            if (existingAssignmentBySupportGroup.isPresent()) {
                throw new FlickzzDeskException(ALREADY_EXISTS,
                        getDescription(ALREADY_EXISTS.getDescription(), "Assignment for support group"));
            }

            Optional<BPAssignment> existingAssignmentBySubCategory = bpAssignmentRepository
                    .findByConfigurationConfigurationIdAndSubCategorySubCategoryIdAndIsActive(
                            config.getConfigurationId(), request.getSubCategoryId(), ACTIVE);
            if (existingAssignmentBySubCategory.isPresent()) {
                throw new FlickzzDeskException(ALREADY_EXISTS,
                        getDescription(ALREADY_EXISTS.getDescription(), "Assignment for sub category"));
            }

            Optional<BPAssignment> existingAssignment = bpAssignmentRepository
                    .findByConfigurationConfigurationIdAndSupportGroupSupportGroupIdAndSubCategorySubCategoryId(
                            config.getConfigurationId(), request.getSupportGroupId(), request.getSubCategoryId());

            BPAssignment assignment;

            if (existingAssignment.isPresent()) {
//				assignment = existingAssignment.get();
//				assignment.setIsActive(Boolean.TRUE);
//				assignment.setUpdatedBy(request.getUpdatedBy());
                throw new FlickzzDeskException(ALREADY_EXISTS,
                        getDescription(ALREADY_EXISTS.getDescription(), "Assignment for support group and sub category"));
            } else {
                assignment = BPAssignment.builder().configuration(config).subCategory(subCategory.get())
                        .supportGroup(supportGroup.get()).createdBy(request.getCreatedBy())
                        .isActive(INACTIVE).isUnderApproval(UNDER_APPROVAL)
                        .updatedBy(request.getUpdatedBy()).build();
            }

            // Prepare new snapshot for audit
            String newSnapshot = null;
            try {
                newSnapshot = objectMapper.writeValueAsString(buildAssignmentSnapshot(assignment));
            } catch (Exception ignore) {
            }
            bpAssignmentRepository.save(assignment);

            configurationChangeService.addConfigurationChangeRequest(config, assignment.getAssignmentId(), null, Boolean.FALSE, Boolean.FALSE,
                    Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, CREATE,
                    businessPartner.get().getCompany(), request.getCreatedBy(),
                    businessPartner.get().getMappedCompany(), request.getIsCreatedByAdmin(), request.getRemarks());

            // Record CREATE audit
            java.util.Map<String, Object> changed = new java.util.HashMap<>();
            changed.put("supportGroupId", assignment.getSupportGroup().getSupportGroupId());
            changed.put("subCategoryId", assignment.getSubCategory().getSubCategoryId());
            String changedStr = null;
            try {
                changedStr = objectMapper.writeValueAsString(changed);
            } catch (Exception ignore) {
            }

            auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "Assignment", "BPAssignment",
                    assignment.getAssignmentId(),
                    CREATE,
                    newSnapshot,
                    null,
                    changedStr,
                    request.getCreatedBy(),
                    commonService.loadUserNameByUserId(Long.valueOf(request.getCreatedBy()), request.getIsCreatedByAdmin()),
                    request.getOrgId(),
                    SUCCESS,
                    null));

            return mapper.toBPAssignmentVo(assignment);
        } catch (FlickzzDeskException e) {
            // record FlickzzDeskException as ERROR audit and rethrow
            String attempted = null;
            try {
                java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
                attemptedMap.put("supportGroupId", request != null ? request.getSupportGroupId() : null);
                attemptedMap.put("subCategoryId", request != null ? request.getSubCategoryId() : null);
                attempted = objectMapper.writeValueAsString(attemptedMap);
            } catch (Exception ignore) {
            }
            auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Assignment", "BPAssignment", null, CREATE,
                    attempted, null, null, request != null ? request.getCreatedBy() : null,
                    commonService.loadUserNameByUserId(Long.valueOf(request.getCreatedBy()), request.getIsCreatedByAdmin()), (request != null ? request.getOrgId() : null), FAILED, e.getDescription()), e);
            throw e;
        } catch (Exception e) {
            String attempted = null;
            try {
                java.util.Map<String, Object> attemptedMap = new java.util.HashMap<>();
                attemptedMap.put("supportGroupId", request != null ? request.getSupportGroupId() : null);
                attemptedMap.put("subCategoryId", request != null ? request.getSubCategoryId() : null);
                attempted = objectMapper.writeValueAsString(attemptedMap);
            } catch (Exception ignore) {
            }
            auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Assignment", "BPAssignment",
                    null,
                    CREATE,
                    attempted,
                    null,
                    null,
                    request != null ? request.getCreatedBy() : null,
                    commonService.loadUserNameByUserId(Long.valueOf(request.getCreatedBy()), request.getIsCreatedByAdmin()),
                    request != null ? request.getOrgId() : null,
                    FAILED,
                    e.getMessage()), e);
            log.error("Exception in createBusinessPartnerAssignmentConfiguration method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public BPAssignmentVO updateBusinessPartnerAssignmentConfiguration(BpConfigRequestVO request) {
        log.info(generateLog("updateBusinessPartnerAssignmentConfiguration", this.getClass().getName()));
        BPAssignment assignment = null;
        try {
            if (request == null || request.getSupportGroupId() == null || request.getSubCategoryId() == null) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Assignment"));
            }

            Optional<BPAssignment> existingAssignment = bpAssignmentRepository
                    .findByAssignmentIdAndIsActive(request.getAssignmentId(), ACTIVE);
            if (existingAssignment.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Assignment"));
            } else if (existingAssignment.get().getIsUnderApproval() != null && existingAssignment.get().getIsUnderApproval()) {
                throw new FlickzzDeskException(ALREADY_EXISTS,
                        "Assignment change is already under approval. Please wait for the approval process to complete.");
            }

            assignment = existingAssignment.get();

            // Capture old values before update
            String oldValue = null;
            try {
                oldValue = objectMapper.writeValueAsString(buildAssignmentSnapshot(assignment));
            } catch (Exception ignore) {
            }

            // Track changed fields
            Map<String, Object> changedFields = new HashMap<>();
            if (!Objects.equals(assignment.getSubCategory().getSubCategoryId(), request.getSubCategoryId())) {
                changedFields.put("subCategoryId", true);
            }
            if (!Objects.equals(assignment.getUpdatedBy(), request.getUpdatedBy())) {
                changedFields.put("updatedBy", true);
            }

            Optional<BPSubCategory> targetSubCategory = bpSubCategoryRepository
                    .findBySubCategoryIdAndIsActive(request.getSubCategoryId(), ACTIVE);
            if (targetSubCategory.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Sub category"));
            }

            Optional<BPAssignment> assignmentForSubCategory = bpAssignmentRepository
                    .findByConfigurationConfigurationIdAndSubCategorySubCategoryIdAndIsActive(
                            assignment.getConfiguration().getConfigurationId(),
                            request.getSubCategoryId(), ACTIVE);
            if (assignmentForSubCategory.isPresent()
                    && !Objects.equals(assignmentForSubCategory.get().getAssignmentId(),
                    assignment.getAssignmentId())) {
                throw new FlickzzDeskException(ALREADY_EXISTS,
                        getDescription(ALREADY_EXISTS.getDescription(), "Assignment for sub category"));
            }

            assignment.setIsUnderApproval(UNDER_APPROVAL);
            bpAssignmentRepository.save(assignment);

            // Create a new inactive copy for update (leave existing record untouched)
            BPAssignment newAssignment = BPAssignment.builder()
                    .configuration(assignment.getConfiguration())
                    .subCategory(targetSubCategory.get())
                    .supportGroup(assignment.getSupportGroup())
                    .version(assignment.getVersion() != null ? assignment.getVersion() + 1 : INITIAL_VERSION)
                    .isActive(INACTIVE)
                    .isUnderApproval(UNDER_APPROVAL)
                    .createdBy(assignment.getCreatedBy())
                    .updatedBy(request.getUpdatedBy() != null ? request.getUpdatedBy() : assignment.getUpdatedBy())
                    .build();
            // ensure new entity will be inserted (no id)
            newAssignment.setAssignmentId(null);
            newAssignment = bpAssignmentRepository.save(newAssignment);

            configurationChangeService.addConfigurationChangeRequest(newAssignment.getConfiguration(), newAssignment.getAssignmentId(), assignment.getAssignmentId(),
                    Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, UPDATE,
                    newAssignment.getConfiguration().getBusinessPartner().getCompany(), request.getUpdatedBy(),
                    newAssignment.getConfiguration().getBusinessPartner().getMappedCompany(), Boolean.FALSE, request.getRemarks());

            // Record UPDATE audit if there are changes
            if (!changedFields.isEmpty()) {
                String newSnapshot = null;
                try {
                    newSnapshot = objectMapper.writeValueAsString(buildAssignmentSnapshot(newAssignment));
                } catch (Exception ignore) {
                }
                String changedStr = null;
                try {
                    changedStr = objectMapper.writeValueAsString(changedFields);
                } catch (Exception ignore) {
                }
                Long companyId = null;
                try {
                    if (newAssignment.getConfiguration() != null && newAssignment.getConfiguration().getBusinessPartner() != null
                            && newAssignment.getConfiguration().getBusinessPartner().getCompany() != null) {
                        companyId = newAssignment.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                    }
                } catch (Exception ignore) {
                }
                auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "Assignment", "BPAssignment",
                        newAssignment.getAssignmentId(),
                        UPDATE,
                        newSnapshot,
                        oldValue,
                        changedStr,
                        request.getUpdatedBy(),
                        commonService.loadUserNameByUserId(Long.valueOf(request.getUpdatedBy()), request.getIsUpdatedByAdmin()),
                        companyId,
                        SUCCESS,
                        null));
            }

            return mapper.toBPAssignmentVo(assignment);
        } catch (FlickzzDeskException e) {
            // Attempt to save error audit
            try {
                Long entityId = null;
                Long companyId = null;
                String oldSnap = null;
                if (assignment != null) {
                    entityId = assignment.getAssignmentId();
                    try {
                        oldSnap = objectMapper.writeValueAsString(buildAssignmentSnapshot(assignment));
                    } catch (Exception ignore) {
                    }
                    try {
                        if (assignment.getConfiguration() != null && assignment.getConfiguration().getBusinessPartner() != null
                                && assignment.getConfiguration().getBusinessPartner().getCompany() != null) {
                            companyId = assignment.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                        }
                    } catch (Exception ignore) {
                    }
                }
                String newSnap = null;
                if (request != null) {
                    try {
                        Map<String, Object> attempted = new HashMap<>();
                        attempted.put("supportGroupId", request.getSupportGroupId());
                        attempted.put("subCategoryId", request.getSubCategoryId());
                        attempted.put("updatedBy", request.getUpdatedBy());
                        newSnap = objectMapper.writeValueAsString(attempted);
                    } catch (Exception ignore) {
                    }
                }
                auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Assignment", "BPAssignment",
                        entityId, UPDATE, newSnap, oldSnap, null,
                        request != null ? request.getUpdatedBy() : null,
                        commonService.loadUserNameByUserId(Long.valueOf(request.getUpdatedBy()), request.getIsUpdatedByAdmin()), companyId, FAILED, e.getDescription()), e);
            } catch (Exception ignore) {
            }
            throw e;
        } catch (Exception e) {
            // Attempt to save error audit
            try {
                Long entityId = null;
                Long companyId = null;
                String oldSnap = null;
                if (assignment != null) {
                    entityId = assignment.getAssignmentId();
                    try {
                        oldSnap = objectMapper.writeValueAsString(buildAssignmentSnapshot(assignment));
                    } catch (Exception ignore) {
                    }
                    try {
                        if (assignment.getConfiguration() != null && assignment.getConfiguration().getBusinessPartner() != null
                                && assignment.getConfiguration().getBusinessPartner().getCompany() != null) {
                            companyId = assignment.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                        }
                    } catch (Exception ignore) {
                    }
                }
                String newSnap = null;
                if (request != null) {
                    try {
                        Map<String, Object> attempted = new HashMap<>();
                        attempted.put("supportGroupId", request.getSupportGroupId());
                        attempted.put("subCategoryId", request.getSubCategoryId());
                        attempted.put("updatedBy", request.getUpdatedBy());
                        newSnap = objectMapper.writeValueAsString(attempted);
                    } catch (Exception ignore) {
                    }
                }
                auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Assignment", "BPAssignment",
                        entityId, UPDATE, newSnap, oldSnap, null,
                        request != null ? request.getUpdatedBy() : null, commonService.loadUserNameByUserId(Long.valueOf(request.getUpdatedBy()), request.getIsUpdatedByAdmin()), companyId, FAILED, e.getMessage()), e);
            } catch (Exception ignore) {
            }
            log.error("Exception in updateBusinessPartnerAssignmentConfiguration method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public void deleteBusinessPartnerAssignmentConfiguration(BpConfigRequestVO request) {
        log.info(generateLog("deleteBusinessPartnerAssignmentConfiguration", this.getClass().getName()));
        BPAssignment assignment = null;
        try {
            Optional<BPAssignment> existingAssignment = bpAssignmentRepository
                    .findByAssignmentIdAndIsActive(request.getAssignmentId(), ACTIVE);
            if (existingAssignment.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Assignment"));
            } else if (existingAssignment.get().getIsUnderApproval() != null && existingAssignment.get().getIsUnderApproval()) {
                throw new FlickzzDeskException(ALREADY_EXISTS,
                        "Assignment change is already under approval. Please wait for the approval process to complete.");
            }

            assignment = existingAssignment.get();

            assignment.setIsUnderApproval(UNDER_APPROVAL);
            bpAssignmentRepository.save(assignment);

            // Capture old snapshot before deletion
            String oldValue = null;
            try {
                oldValue = objectMapper.writeValueAsString(buildAssignmentSnapshot(assignment));
            } catch (Exception ignore) {
            }

            // Create a new inactive copy for delete (leave existing record untouched)
            BPAssignment newAssignment = BPAssignment.builder()
                    .configuration(assignment.getConfiguration())
                    .subCategory(assignment.getSubCategory())
                    .supportGroup(assignment.getSupportGroup())
                    .version(assignment.getVersion() != null ? assignment.getVersion() + 1 : 1)
                    .isActive(INACTIVE)
                    .isUnderApproval(UNDER_APPROVAL)
                    .createdBy(assignment.getCreatedBy())
                    .updatedBy(request.getDeletedBy() != null ? request.getDeletedBy() : assignment.getUpdatedBy())
                    .build();
            // ensure new entity will be inserted (no id)
            newAssignment.setAssignmentId(null);
            newAssignment = bpAssignmentRepository.save(newAssignment);

            configurationChangeService.addConfigurationChangeRequest(newAssignment.getConfiguration(), newAssignment.getAssignmentId(), assignment.getAssignmentId(),
                    Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, DELETE,
                    newAssignment.getConfiguration().getBusinessPartner().getCompany(), request.getDeletedBy(),
                    newAssignment.getConfiguration().getBusinessPartner().getMappedCompany(), Boolean.FALSE, request.getRemarks());

            // Record DELETE audit
            Long companyId = null;
            try {
                if (assignment.getConfiguration() != null && assignment.getConfiguration().getBusinessPartner() != null
                        && assignment.getConfiguration().getBusinessPartner().getCompany() != null) {
                    companyId = assignment.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                }
            } catch (Exception ignore) {
            }
            auditService.recordAudit(mapper.toSystemAuditRequest("BusinessPartner", "Assignment", "BPAssignment",
                    assignment.getAssignmentId(),
                    DELETE,
                    oldValue,
                    null,
                    null,
                    request.getDeletedBy(),
                    commonService.loadUserNameByUserId(Long.valueOf(request.getDeletedBy()), request.getIsDeletedByAdmin()),
                    companyId,
                    SUCCESS,
                    null));
        } catch (FlickzzDeskException e) {
            // record FlickzzDeskException as ERROR audit and rethrow
            try {
                Long entityId = null;
                Long companyId = null;
                try {
                    entityId = request.getAssignmentId();
                } catch (Exception ignore) {
                }
                if (assignment != null && assignment.getConfiguration() != null
                        && assignment.getConfiguration().getBusinessPartner() != null
                        && assignment.getConfiguration().getBusinessPartner().getCompany() != null) {
                    companyId = assignment.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                }
                auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Assignment", "BPAssignment",
                        entityId, DELETE,
                        null, null, null, request.getDeletedBy(), commonService.loadUserNameByUserId(Long.valueOf(request.getDeletedBy()), request.getIsDeletedByAdmin()), companyId, FAILED, e.getDescription()), e);
            } catch (Exception ignore) {
            }
            throw e;
        } catch (Exception e) {
            // record generic exception as ERROR audit
            try {
                Long entityId = null;
                Long companyId = null;
                try {
                    entityId = Long.valueOf(request.getAssignmentId());
                } catch (Exception ignore) {
                }
                if (assignment != null && assignment.getConfiguration() != null
                        && assignment.getConfiguration().getBusinessPartner() != null
                        && assignment.getConfiguration().getBusinessPartner().getCompany() != null) {
                    companyId = assignment.getConfiguration().getBusinessPartner().getCompany().getCompanyId();
                }
                auditService.recordExceptionAudit(mapper.toSystemAuditRequest("BusinessPartner", "Assignment", "BPAssignment",
                        entityId, DELETE,
                        null, null, null, request.getDeletedBy(), commonService.loadUserNameByUserId(Long.valueOf(request.getDeletedBy()), request.getIsDeletedByAdmin()), companyId, FAILED, e.getMessage()), e);
            } catch (Exception ignore) {
            }
            log.error("Exception in deleteBusinessPartnerAssignmentConfiguration method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public BPAssignmentVO getBusinessPartnerAssignmentConfigurationBySupportGroupId(Long assignmentId) {
        log.info(generateLog("getBusinessPartnerAssignmentConfigurationBySupportGroupId", this.getClass().getName()));
        try {
            Optional<BPAssignment> existingAssignment = bpAssignmentRepository
                    .findById(assignmentId);
            if (existingAssignment.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Assignment"));
            }
            return mapper.toBPAssignmentVo(existingAssignment.get());
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "Exception in getBusinessPartnerAssignmentConfigurationBySupportGroupId method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<BPAssignmentVO> getBusinessPartnerAssignmentConfiguration(Long businessPartnerId) {
        log.info(generateLog("getBusinessPartnerAssignmentConfiguration", this.getClass().getName()));
        try {
            Optional<BusinessPartner> businessPartner = businessPartnerRepository
                    .findByBusinessPartnerIdAndIsActive(businessPartnerId, ACTIVE);
            if (!businessPartner.isPresent()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
            }

            Optional<BPConfiguration> existingConfig = bPConfigurationRepository
                    .findByBusinessPartnerBusinessPartnerIdAndIsActiveTrue(businessPartnerId);
            if (!existingConfig.isPresent()) {
                return Collections.emptyList();
            }

            List<BPAssignment> assignments = bpAssignmentRepository
                    .findByConfigurationConfigurationId(existingConfig.get().getConfigurationId());
            return assignments.stream().map(mapper::toBPAssignmentVo).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getBusinessPartnerAssignmentConfiguration method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<ConfigChangeApprovalVO> getBusinessPartnerApprovalList(Long userId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (userId == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "User ID"));
            }

            List<ConfigChangeApproval> approvals = configChangeApprovalRepository.findByApproverUserId(userId);
            log.info(generateLog(EXIT, this.getClass().getName()));
            return approvals.stream().map(mapper::toConfigChangeApprovalVO).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getBusinessPartnerApprovalList method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public ConfigChangeApprovalVO actionOnConfigApproval(BpConfigRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (request == null || StringUtils.isBlank(request.getAction())) {
                throw new FlickzzDeskException(INVALID_TEXT,
                        getDescription(INVALID_TEXT.getDescription(), "Request"));
            }

            Optional<CompanyApprover> approver = companyApproverRepository.findByAgentUserUserId(request.getUpdatedBy());
            if (approver.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Company Approver"));
            }

            CompanyApprover companyApprover = approver.get();
            if (companyApprover.getIsActive() == null || !companyApprover.getIsActive()) {
                throw new FlickzzDeskException(INVALID_FIELD, "Your previlege to approve change request is not valid");
            }

            if (companyApprover.getLevel() == null || companyApprover.getLevel() <= 0) {
                throw new FlickzzDeskException(INVALID_FIELD, "Your previlege to approve change request is not valid");
            }

            if (request.getApprovalId() == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Approval Id"));
            }

            ConfigChangeApproval approval = configChangeApprovalRepository.findById(request.getApprovalId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Config Change Approval")));

            if (!Objects.equals(approval.getApproverUserId(), request.getUpdatedBy())) {
                throw new FlickzzDeskException(INVALID_FIELD, "Approval does not belong to current approver");
            }

            String action = request.getAction();

            approval.setUpdatedBy(request.getUpdatedBy());
            approval.setUpdatedOn(LocalDateTime.now());
            approval.setStatus(request.getAction().equalsIgnoreCase(APPROVE) ? APPROVED : request.getAction().equalsIgnoreCase(DECLINE) ? DECLINED : REQUEST_CLARIFICATION);
            saveChangeRequestRemark(approval, request.getAction(), request.getRemarks());

            if (approval.getApproverLevel().equals(MANDATORY_APPROVER_LEVEL)) {
                if (DECLINE.equalsIgnoreCase(action)) {
                    if ("BP".equalsIgnoreCase(approval.getApproverType())) {
                        applyDeclineApproval(approval, request);
                    } else if ("Internal".equalsIgnoreCase(approval.getApproverType())) {
                        approval.setApprovedOn(LocalDateTime.now());
                        if (approval.getConfigChangeRequest() != null && approval.getConfigChangeRequest().getChangedRequestId() != null
                                && approval.getConfigChangeRequest().getSourceChangeId() != null &&
                                approval.getConfigChangeRequest().getChangedRequestId().equals(approval.getConfigChangeRequest().getSourceChangeId())) {
                            applyDeclineApproval(approval, request);
                        } else {
                            configNotificationService.notifyBpApprovalConfigChange(approval.getConfigChangeRequest(), approval.getApprovalType());
                        }
                    }
                } else if (REQUEST_CLARIFICATION.equalsIgnoreCase(action)) {
                    requestClarification(approval, request);
                } else if (APPROVE.equalsIgnoreCase(action) && "BP".equalsIgnoreCase(approval.getApproverType())) {
                    applyApproval(approval);
                    approval.setApprovedOn(LocalDateTime.now());
                } else if (APPROVE.equalsIgnoreCase(action) && "Internal".equalsIgnoreCase(approval.getApproverType())) {
                    approval.setApprovedOn(LocalDateTime.now());
                    if (approval.getConfigChangeRequest() != null && approval.getConfigChangeRequest().getChangedRequestId() != null
                            && approval.getConfigChangeRequest().getSourceChangeId() != null &&
                            approval.getConfigChangeRequest().getChangedRequestId().equals(approval.getConfigChangeRequest().getSourceChangeId())) {
                        applyApproval(approval);
                    } else {
                        configNotificationService.notifyBpApprovalConfigChange(approval.getConfigChangeRequest(), approval.getApprovalType());
                    }
                }
            } else {
                approval.setStatus(action);
            }

            updateChangeRequestProgress(approval, action);
            configChangeApprovalRepository.save(approval);
            return mapper.toConfigChangeApprovalVO(approval);
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in actionOnConfigApproval method in BusinessPartnerService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    private void saveChangeRequestRemark(ConfigChangeApproval approval, String action, String remarkText) {
        BPConfigurationChangeRequest changeRequest = approval.getConfigChangeRequest();
        if (changeRequest == null) {
            return;
        }
        BPConfigurationChangeRequestRemark remark = BPConfigurationChangeRequestRemark.builder()
                .configurationChangeRequest(changeRequest)
                .remarkType(action)
                .approval(approval)
                .approverLevel(approval.getApproverLevel())
                .approvalStatus(action.equalsIgnoreCase(APPROVE) ? APPROVED : action.equalsIgnoreCase(DECLINE) ? DECLINED : REQUEST_CLARIFICATION)
                .userId(approval.getApproverUserId())
                .organizationId(approval.getApproverOrgId())
                .remark(remarkText != null ? remarkText : "")
                .createdOn(LocalDateTime.now())
                .build();
        bpConfigurationChangeRequestRemarkRepository.save(remark);
    }

    private void applyDeclineApproval(ConfigChangeApproval approval, BpConfigRequestVO request) {
        BPConfigurationChangeRequest changeRequest = approval.getConfigChangeRequest();
        if (changeRequest == null || changeRequest.getChangedRequestId() == null) {
            return;
        }

        Long changedId = changeRequest.getChangedRequestId();
        if (Boolean.TRUE.equals(changeRequest.getBpPriority())) {
            bPPriorityRepository.findById(changedId).ifPresent(entity -> {
                entity.setIsActive(INACTIVE);
                entity.setIsUnderApproval(Boolean.FALSE);
                bPPriorityRepository.save(entity);
            });
            if (changeRequest.getOperation().equalsIgnoreCase(DELETE)) {
                bPPriorityRepository.findById(changeRequest.getSourceChangeId()).ifPresent(entity -> {
                    entity.setIsActive(INACTIVE);
                    entity.setIsUnderApproval(Boolean.FALSE);
                    bPPriorityRepository.save(entity);
                });
            }
        } else if (Boolean.TRUE.equals(changeRequest.getBpSla())) {
            bpSlaRepository.findById(changedId).ifPresent(entity -> {
                entity.setIsActive(INACTIVE);
                entity.setIsUnderApproval(Boolean.FALSE);
                bpSlaRepository.save(entity);
            });
            if (changeRequest.getOperation().equalsIgnoreCase(DELETE)) {
                bpSlaRepository.findById(changeRequest.getSourceChangeId()).ifPresent(entity -> {
                    entity.setIsActive(INACTIVE);
                    entity.setIsUnderApproval(Boolean.FALSE);
                    bpSlaRepository.save(entity);
                });
            }
        } else if (Boolean.TRUE.equals(changeRequest.getCategory())) {
            bpCategoryRepository.findById(changedId).ifPresent(entity -> {
                entity.setIsActive(INACTIVE);
                entity.setIsUnderApproval(Boolean.FALSE);
                bpCategoryRepository.save(entity);
            });
            if (changeRequest.getOperation().equalsIgnoreCase(DELETE)) {
                bpCategoryRepository.findById(changeRequest.getSourceChangeId()).ifPresent(entity -> {
                    entity.setIsActive(INACTIVE);
                    entity.setIsUnderApproval(Boolean.FALSE);
                    bpCategoryRepository.save(entity);
                });
            }
        } else if (Boolean.TRUE.equals(changeRequest.getSupportGroup())) {
            bpSupportGroupRepository.findById(changedId).ifPresent(entity -> {
                entity.setIsActive(INACTIVE);
                entity.setIsUnderApproval(Boolean.FALSE);
                bpSupportGroupRepository.save(entity);
            });
            if (changeRequest.getOperation().equalsIgnoreCase(DELETE)) {
                bpSupportGroupRepository.findById(changeRequest.getSourceChangeId()).ifPresent(entity -> {
                    entity.setIsActive(INACTIVE);
                    entity.setIsUnderApproval(Boolean.FALSE);
                    bpSupportGroupRepository.save(entity);
                });
            }
        } else if (Boolean.TRUE.equals(changeRequest.getAssignment())) {
            bpAssignmentRepository.findById(changedId).ifPresent(entity -> {
                entity.setIsActive(INACTIVE);
                entity.setIsUnderApproval(Boolean.FALSE);
                bpAssignmentRepository.save(entity);
            });
            if (changeRequest.getOperation().equalsIgnoreCase(DELETE)) {
                bpAssignmentRepository.findById(changeRequest.getSourceChangeId()).ifPresent(entity -> {
                    entity.setIsActive(INACTIVE);
                    entity.setIsUnderApproval(Boolean.FALSE);
                    bpAssignmentRepository.save(entity);
                });
            }
        }

        changeRequest.setStatus(DECLINED);
        changeRequest.setUpdatedBy(approval.getUpdatedBy());
        changeRequest.setUpdatedOn(LocalDateTime.now());
        changeRequest.setCompletedOn(LocalDateTime.now());
        bpConfigurationChangeRequestRepository.save(changeRequest);
        approval.setStatus(DECLINED);
    }

    private void requestClarification(ConfigChangeApproval approval, BpConfigRequestVO request) {
        approval.setStatus(REQUEST_CLARIFICATION);
        BPConfigurationChangeRequest changeRequest = approval.getConfigChangeRequest();
        if (changeRequest == null) {
            return;
        }
        changeRequest.setStatus(REQUEST_CLARIFICATION);
        changeRequest.setUpdatedBy(approval.getUpdatedBy());
        changeRequest.setUpdatedOn(LocalDateTime.now());
        bpConfigurationChangeRequestRepository.save(changeRequest);

    }

    private void applyApproval(ConfigChangeApproval approval) {
        BPConfigurationChangeRequest changeRequest = approval.getConfigChangeRequest();
        if (changeRequest == null || changeRequest.getChangedRequestId() == null) {
            return;
        }

        Long changedId = changeRequest.getChangedRequestId();
        if (Boolean.TRUE.equals(changeRequest.getBpPriority())) {
            bPPriorityRepository.findById(changedId).ifPresent(entity -> {
                entity.setIsActive(ACTIVE);
                entity.setIsUnderApproval(Boolean.FALSE);
                bPPriorityRepository.save(entity);
            });
            if (changeRequest.getOperation().equalsIgnoreCase(UPDATE) || changeRequest.getOperation().equalsIgnoreCase(DELETE)) {
                bPPriorityRepository.findById(changeRequest.getSourceChangeId()).ifPresent(entity -> {
                    entity.setIsActive(INACTIVE);
                    entity.setIsUnderApproval(Boolean.FALSE);
                    bPPriorityRepository.save(entity);
                });
            }
        } else if (Boolean.TRUE.equals(changeRequest.getBpSla())) {
            bpSlaRepository.findById(changedId).ifPresent(entity -> {
                entity.setIsActive(ACTIVE);
                entity.setIsUnderApproval(Boolean.FALSE);
                bpSlaRepository.save(entity);
            });
            if (changeRequest.getOperation().equalsIgnoreCase(UPDATE) || changeRequest.getOperation().equalsIgnoreCase(DELETE)) {
                bpSlaRepository.findById(changeRequest.getSourceChangeId()).ifPresent(entity -> {
                    entity.setIsActive(INACTIVE);
                    entity.setIsUnderApproval(Boolean.FALSE);
                    bpSlaRepository.save(entity);
                });
            }
        } else if (Boolean.TRUE.equals(changeRequest.getCategory())) {
            bpCategoryRepository.findById(changedId).ifPresent(entity -> {
                entity.setIsActive(ACTIVE);
                entity.setIsUnderApproval(Boolean.FALSE);
                bpCategoryRepository.save(entity);
            });
            if (changeRequest.getOperation().equalsIgnoreCase(UPDATE) || changeRequest.getOperation().equalsIgnoreCase(DELETE)) {
                bpCategoryRepository.findById(changeRequest.getSourceChangeId()).ifPresent(entity -> {
                    entity.setIsActive(INACTIVE);
                    entity.setIsUnderApproval(Boolean.FALSE);
                    bpCategoryRepository.save(entity);
                });
            }
        } else if (Boolean.TRUE.equals(changeRequest.getSupportGroup())) {
            bpSupportGroupRepository.findById(changedId).ifPresent(entity -> {
                entity.setIsActive(ACTIVE);
                entity.setIsUnderApproval(Boolean.FALSE);
                bpSupportGroupRepository.save(entity);
            });
            if (changeRequest.getOperation().equalsIgnoreCase(UPDATE) || changeRequest.getOperation().equalsIgnoreCase(DELETE)) {
                bpSupportGroupRepository.findById(changeRequest.getSourceChangeId()).ifPresent(entity -> {
                    entity.setIsActive(INACTIVE);
                    entity.setIsUnderApproval(Boolean.FALSE);
                    bpSupportGroupRepository.save(entity);
                });
            }
        } else if (Boolean.TRUE.equals(changeRequest.getAssignment())) {
            bpAssignmentRepository.findById(changedId).ifPresent(entity -> {
                entity.setIsActive(ACTIVE);
                entity.setIsUnderApproval(Boolean.FALSE);
                bpAssignmentRepository.save(entity);
            });
            if (changeRequest.getOperation().equalsIgnoreCase(UPDATE) || changeRequest.getOperation().equalsIgnoreCase(DELETE)) {
                bpAssignmentRepository.findById(changeRequest.getSourceChangeId()).ifPresent(entity -> {
                    entity.setIsActive(INACTIVE);
                    entity.setIsUnderApproval(Boolean.FALSE);
                    bpAssignmentRepository.save(entity);
                });
            }
        }
        approval.setStatus(APPROVE);
    }

    private void updateChangeRequestProgress(ConfigChangeApproval approval, String action) {
        BPConfigurationChangeRequest changeRequest = approval.getConfigChangeRequest();
        if (changeRequest == null) {
            return;
        }

        boolean saveRequest = false;
        if (APPROVE.equalsIgnoreCase(action) || DECLINE.equalsIgnoreCase(action)) {
            if ("Internal".equalsIgnoreCase(approval.getApproverType())) {
                changeRequest.setInternalApprovalCompleted(Boolean.TRUE);
                changeRequest.setCurrentInternalApprovalLevel(approval.getApproverLevel());
                changeRequest.setStatus(changeRequest.getChangedRequestId().equals(changeRequest.getSourceChangeId()) ? APPROVED : INTERNAL_APPROVED);
                saveRequest = true;
            } else if ("BP".equalsIgnoreCase(approval.getApproverType())) {
                changeRequest.setBpApprovalCompleted(Boolean.TRUE);
                changeRequest.setCurrentBpApprovalLevel(approval.getApproverLevel());
                changeRequest.setStatus(APPROVED);
                changeRequest.setCompletedOn(LocalDateTime.now());
                saveRequest = true;
            }
        } else if (REQUEST_CLARIFICATION.equalsIgnoreCase(action)) {
            changeRequest.setStatus(REQUEST_CLARIFICATION);
            saveRequest = true;
        } else {
            changeRequest.setStatus(action);
            saveRequest = true;
        }

        if (saveRequest) {
            changeRequest.setUpdatedBy(approval.getUpdatedBy());
            changeRequest.setUpdatedOn(LocalDateTime.now());
            bpConfigurationChangeRequestRepository.save(changeRequest);
        }
    }

    public List<BPConfigurationChangeRequestRemarkVO> getApprovalRemarks(Long approvalId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (approvalId == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Approval Id"));
            }

            Optional<ConfigChangeApproval> approval = configChangeApprovalRepository.findById(approvalId);
            if (approval.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Config Change Approval"));
            }

            List<BPConfigurationChangeRequestRemark> remarks = bpConfigurationChangeRequestRemarkRepository.findByConfigurationChangeRequest_CcrId(approval.get().getConfigChangeRequest().getCcrId());
            return remarks.stream().map(mapper::toBPConfigurationChangeRequestRemarkVO).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getApprovalRemarks method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<BPSubCategoryVO> getBusinessPartnerSubCategoryConfigurationByCategoryId(Long categoryId) {
        log.info(generateLog("getBusinessPartnerSubCategoryConfigurationByCategoryId", this.getClass().getName()));
        try {
            if (categoryId == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Category Id"));
            }

            List<BPSubCategory> bpSubCategories = bpSubCategoryRepository.findAllByCategoryCategoryIdAndIsActiveTrue(categoryId);
            return bpSubCategories.stream().map(mapper::toNoBackRefBPSubCategoryVo).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getBusinessPartnerSubCategoryConfigurationByCategoryId method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public BPSupportGroupVO getBusinessPartnerSupportGroupConfigurationBySubCategoryId(Long subCategoryId) {
        log.info(generateLog("getBusinessPartnerSupportGroupConfigurationBySubCategoryId", this.getClass().getName()));
        try {
            if (subCategoryId == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Sub Category Id"));
            }
            BPAssignment bpAssignment = bpAssignmentRepository.findBySubCategorySubCategoryIdAndIsActiveTrue(subCategoryId);

            if (bpAssignment == null || bpAssignment.getSupportGroup() == null) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Support Group"));
            }

            return mapper.toSupportGroupVo(bpAssignment.getSupportGroup());
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getBusinessPartnerSubCategoryConfigurationByCategoryId method in BusinessPartnerService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }
}
