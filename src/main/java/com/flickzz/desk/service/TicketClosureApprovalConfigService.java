package com.flickzz.desk.service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.CompanyMaster;
import com.flickzz.desk.model.StatusMaster;
import com.flickzz.desk.model.TicketClosureApprovalConfig;
import com.flickzz.desk.model.WorkItem;
import com.flickzz.desk.repo.CompanyMasterRepository;
import com.flickzz.desk.repo.StatusMasterRepository;
import com.flickzz.desk.repo.TicketClosureApprovalConfigRepository;
import com.flickzz.desk.repo.WorkItemRepository;
import com.flickzz.desk.vo.TicketClosureApprovalConfigVO;
import com.flickzz.desk.vo.request.SystemAuditRequest;
import com.flickzz.desk.vo.request.TicketClosureApprovalConfigRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.*;

import static com.flickzz.desk.config.FlickzzDeskConstants.CREATE;
import static com.flickzz.desk.config.FlickzzDeskConstants.SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

@Service
public class TicketClosureApprovalConfigService {

    private static final Logger log = LoggerFactory.getLogger(TicketClosureApprovalConfigService.class);
    private static final Set<String> AUTO_ACTION_UNITS = Set.of("MINUTES", "HOURS", "DAYS");

    @Autowired
    private TicketClosureApprovalConfigRepository configRepository;

    @Autowired
    private CompanyMasterRepository companyMasterRepository;

    @Autowired
    private WorkItemRepository workItemRepository;

    @Autowired
    private StatusMasterRepository statusMasterRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private CommonMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public TicketClosureApprovalConfigVO create(TicketClosureApprovalConfigRequestVO request) {
        try {
            validateRequest(request, false);
            CompanyMaster company = companyMasterRepository.findById(request.getCompanyId())
                    .filter(value -> Boolean.TRUE.equals(value.getIsActive()))
                    .orElseThrow(() -> notFound("Active company with ID " + request.getCompanyId()));
            WorkItem workItem = workItemRepository.findById(request.getWorkItemId())
                    .filter(value -> Boolean.TRUE.equals(value.getIsActive()))
                    .orElseThrow(() -> notFound("Active work item with ID " + request.getWorkItemId()));
            if (workItem.getCompany() == null
                    || !company.getCompanyId().equals(workItem.getCompany().getCompanyId())) {
                throw invalidField("Work item does not belong to the provided company");
            }

            StatusMaster pendingStatus = request.getApprovalPendingStatusId() == null ? null
                    : statusMasterRepository.findById(request.getApprovalPendingStatusId())
                    .filter(value -> Boolean.TRUE.equals(value.getIsActive()))
                    .orElseThrow(() -> notFound("Active approval pending status with ID "
                            + request.getApprovalPendingStatusId()));
            if (pendingStatus != null) {
                validateStatusScope(pendingStatus, company.getCompanyId(), workItem.getItemId(),
                        "Approval pending status");
            }

            boolean autoActionEnabled = request.getAutoActionTimeline() != null;
            String autoActionUnit = null;
            StatusMaster autoApproveStatus = null;
            if (autoActionEnabled) {
                autoActionUnit = request.getAutoActionTimeUnit().trim().toUpperCase(Locale.ROOT);
                autoApproveStatus = statusMasterRepository.findById(request.getStatusAfterAutoAction())
                        .filter(value -> Boolean.TRUE.equals(value.getIsActive()))
                        .orElseThrow(() -> notFound("Active status with ID "
                                + request.getStatusAfterAutoAction()));
                validateStatusScope(autoApproveStatus, company.getCompanyId(), workItem.getItemId(),
                        "Post-action status");
            }

            TicketClosureApprovalConfig config = TicketClosureApprovalConfig.builder()
                    .company(company)
                    .workItem(workItem)
                    .isApprovalRequired(request.getIsApprovalRequired())
                    .approvalPendingStatus(pendingStatus)
                    .autoActionEnabled(autoActionEnabled)
                    .autoActionAfter(request.getAutoActionTimeline())
                    .autoActionUnit(autoActionUnit)
                    .autoAction(autoActionEnabled ? "APPROVE" : null)
                    .autoApproveStatus(autoApproveStatus)
                    .isActive(true)
                    .createdBy(request.getCreatedBy())
                    .isCreatorAdmin(request.getIsCreatedByAdmin())
                    .updatedBy(request.getUpdatedBy())
                    .isUpdaterAdmin(Boolean.TRUE.equals(request.getIsUpdatedByAdmin()))
                    .build();

            TicketClosureApprovalConfig saved = configRepository.saveAndFlush(config);
            TicketClosureApprovalConfigVO response = toVO(saved);
            auditService.recordAudit(mapper.toSystemAuditRequest(
                    "Ticket", "Approval Workflow", "TicketClosureApprovalConfig",
                    saved.getConfigId(), CREATE, request.toString(), null, null,
                    request.getCreatedBy(), null, company.getCompanyId(), SUCCESS, null));
            return response;
        } catch (FlickzzDeskException e) {
            auditException(request, "CREATE", e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to create ticket closure approval workflow", e);
            auditException(request, "CREATE", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    private void validateRequest(TicketClosureApprovalConfigRequestVO request, boolean updating) {
        if (request == null
                || (updating && (request.getConfigId() == null || request.getConfigId() <= 0))
                || request.getCompanyId() == null || request.getCompanyId() <= 0
                || request.getWorkItemId() == null || request.getWorkItemId() <= 0
                || request.getIsApprovalRequired() == null
                || (Boolean.TRUE.equals(request.getIsApprovalRequired())
                && (request.getApprovalPendingStatusId() == null || request.getApprovalPendingStatusId() <= 0))
                || (request.getApprovalPendingStatusId() != null && request.getApprovalPendingStatusId() <= 0)
                || (request.getStatusAfterAutoAction() != null && request.getStatusAfterAutoAction() <= 0)
                || (updating && (request.getUpdatedBy() == null || request.getUpdatedBy() <= 0
                || request.getIsUpdatedByAdmin() == null))
                || (!updating && (request.getCreatedBy() == null || request.getCreatedBy() <= 0
                || request.getIsCreatedByAdmin() == null))) {
            throw new FlickzzDeskException(INVALID_REQUEST,
                    getDescription(INVALID_REQUEST.getDescription(), "Invalid ticket approval workflow data"));
        }

        boolean hasTimeline = request.getAutoActionTimeline() != null;
        boolean hasUnit = request.getAutoActionTimeUnit() != null
                && !request.getAutoActionTimeUnit().isBlank();
        boolean hasStatus = request.getStatusAfterAutoAction() != null;
        if (hasTimeline != hasUnit || hasTimeline != hasStatus) {
            throw new FlickzzDeskException(INVALID_REQUEST,
                    getDescription(INVALID_REQUEST.getDescription(),
                            "Timeline, time unit, and post-action status must be provided together"));
        }
        if (hasTimeline && request.getAutoActionTimeline() <= 0) {
            throw invalidField("Auto action timeline must be greater than zero");
        }
        if (hasTimeline && !AUTO_ACTION_UNITS.contains(
                request.getAutoActionTimeUnit().trim().toUpperCase(Locale.ROOT))) {
            throw invalidField("Auto action time unit must be MINUTES, HOURS, or DAYS");
        }
    }

    private FlickzzDeskException notFound(String entity) {
        return new FlickzzDeskException(DOES_NOT_EXIST,
                getDescription(DOES_NOT_EXIST.getDescription(), entity));
    }

    private FlickzzDeskException invalidField(String field) {
        return new FlickzzDeskException(INVALID_FIELD,
                getDescription(INVALID_FIELD.getDescription(), field));
    }

    private void validateStatusScope(StatusMaster status, Long companyId, Long workItemId, String fieldName) {
        if (status.getCompany() == null || status.getWorkItem() == null
                || !companyId.equals(status.getCompany().getCompanyId())
                || !workItemId.equals(status.getWorkItem().getItemId())) {
            throw invalidField(fieldName + " does not belong to the provided company and work item");
        }
    }

    private TicketClosureApprovalConfigVO toVO(TicketClosureApprovalConfig config) {
        return TicketClosureApprovalConfigVO.builder()
                .configId(config.getConfigId())
                .companyId(config.getCompany().getCompanyId())
                .workItemId(config.getWorkItem().getItemId())
                .isApprovalRequired(config.getIsApprovalRequired())
                .approvalPendingStatusId(config.getApprovalPendingStatus() == null
                        ? null : config.getApprovalPendingStatus().getStatusId())
                .approvalPendingStatusCode(config.getApprovalPendingStatus() == null
                        ? null : config.getApprovalPendingStatus().getStatusCode())
                .autoActionEnabled(config.getAutoActionEnabled())
                .autoActionTimeline(config.getAutoActionAfter())
                .autoActionTimeUnit(config.getAutoActionUnit())
                .statusAfterAutoAction(config.getAutoApproveStatus() == null
                        ? null : config.getAutoApproveStatus().getStatusId())
                .statusAfterAutoActionCode(config.getAutoApproveStatus() == null
                        ? null : config.getAutoApproveStatus().getStatusCode())
                .isActive(config.getIsActive())
                .createdBy(config.getCreatedBy())
                .isCreatedByAdmin(config.getIsCreatorAdmin())
                .createdAt(config.getCreatedAt())
                .updatedBy(config.getUpdatedBy())
                .isUpdatedByAdmin(config.getIsUpdaterAdmin())
                .updatedAt(config.getUpdatedAt())
                .build();
    }

    private void auditException(TicketClosureApprovalConfigRequestVO request, String action, Exception exception) {
        if (request == null) {
            return;
        }
        SystemAuditRequest auditRequest = mapper.toSystemAuditRequest(
                "Ticket", "Approval Workflow", "TicketClosureApprovalConfig",
                request.getConfigId(), action, request.toString(), null, null,
                "UPDATE".equals(action) ? request.getUpdatedBy() : request.getCreatedBy(),
                null, request.getCompanyId(), "ERROR",
                exception.getMessage());
        auditService.recordExceptionAudit(auditRequest, exception);
    }

    @Transactional(readOnly = true)
    public List<TicketClosureApprovalConfigVO> list(Long orgId) {
        if (orgId == null || orgId <= 0) {
            throw new FlickzzDeskException(INVALID_REQUEST,
                    getDescription(INVALID_REQUEST.getDescription(), "Organization ID is required"));
        }
        try {
            return configRepository
                    .findByCompany_CompanyIdAndIsActiveTrueOrderByConfigIdAsc(orgId)
                    .stream()
                    .map(this::toVO)
                    .toList();
        } catch (Exception e) {
            log.error("Failed to list ticket closure approval workflows for organization {}", orgId, e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    @Transactional(readOnly = true)
    public List<TicketClosureApprovalConfigVO> list(Long workItemId, Long orgId) {
        if (workItemId == null || workItemId <= 0 || orgId == null || orgId <= 0) {
            throw new FlickzzDeskException(INVALID_REQUEST,
                    getDescription(INVALID_REQUEST.getDescription(),
                            "Work item ID and organization ID are required"));
        }
        try {
            return configRepository
                    .findByWorkItem_ItemIdAndCompany_CompanyIdAndIsActiveTrueOrderByConfigIdAsc(
                            workItemId, orgId)
                    .stream()
                    .map(this::toVO)
                    .toList();
        } catch (Exception e) {
            log.error("Failed to list ticket closure approval workflows for work item {} and organization {}",
                    workItemId, orgId, e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    @Transactional
    public TicketClosureApprovalConfigVO update(TicketClosureApprovalConfigRequestVO request) {
        try {
            validateRequest(request, true);
            TicketClosureApprovalConfig config = configRepository
                    .findByConfigIdAndCompany_CompanyId(request.getConfigId(), request.getCompanyId())
                    .filter(value -> Boolean.TRUE.equals(value.getIsActive()))
                    .orElseThrow(() -> notFound("Ticket approval workflow with ID " + request.getConfigId()));

            CompanyMaster company = companyMasterRepository.findById(request.getCompanyId())
                    .filter(value -> Boolean.TRUE.equals(value.getIsActive()))
                    .orElseThrow(() -> notFound("Active company with ID " + request.getCompanyId()));
            WorkItem workItem = workItemRepository.findById(request.getWorkItemId())
                    .filter(value -> Boolean.TRUE.equals(value.getIsActive()))
                    .orElseThrow(() -> notFound("Active work item with ID " + request.getWorkItemId()));
            if (workItem.getCompany() == null
                    || !company.getCompanyId().equals(workItem.getCompany().getCompanyId())) {
                throw invalidField("Work item does not belong to the provided company");
            }

            StatusMaster pendingStatus = request.getApprovalPendingStatusId() == null ? null
                    : statusMasterRepository.findById(request.getApprovalPendingStatusId())
                    .filter(value -> Boolean.TRUE.equals(value.getIsActive()))
                    .orElseThrow(() -> notFound("Active approval pending status with ID "
                            + request.getApprovalPendingStatusId()));
            if (pendingStatus != null) {
                validateStatusScope(pendingStatus, company.getCompanyId(), workItem.getItemId(),
                        "Approval pending status");
            }

            boolean autoActionEnabled = request.getAutoActionTimeline() != null;
            String autoActionUnit = null;
            StatusMaster autoApproveStatus = null;
            if (autoActionEnabled) {
                autoActionUnit = request.getAutoActionTimeUnit().trim().toUpperCase(Locale.ROOT);
                autoApproveStatus = statusMasterRepository.findById(request.getStatusAfterAutoAction())
                        .filter(value -> Boolean.TRUE.equals(value.getIsActive()))
                        .orElseThrow(() -> notFound("Active status with ID "
                                + request.getStatusAfterAutoAction()));
                validateStatusScope(autoApproveStatus, company.getCompanyId(), workItem.getItemId(),
                        "Post-action status");
            }

            String oldValue = serializeSnapshot(config);
            config.setCompany(company);
            config.setWorkItem(workItem);
            config.setIsApprovalRequired(request.getIsApprovalRequired());
            config.setApprovalPendingStatus(pendingStatus);
            config.setAutoActionEnabled(autoActionEnabled);
            config.setAutoActionAfter(request.getAutoActionTimeline());
            config.setAutoActionUnit(autoActionUnit);
            config.setAutoAction(autoActionEnabled ? "APPROVE" : null);
            config.setAutoApproveStatus(autoApproveStatus);
            config.setUpdatedBy(request.getUpdatedBy());
            config.setIsUpdaterAdmin(request.getIsUpdatedByAdmin());
            config.setUpdatedAt(LocalDateTime.now());

            TicketClosureApprovalConfig saved = configRepository.saveAndFlush(config);
            TicketClosureApprovalConfigVO response = toVO(saved);
            auditService.recordAudit(mapper.toSystemAuditRequest(
                    "Ticket", "Approval Workflow", "TicketClosureApprovalConfig",
                    saved.getConfigId(), "UPDATE", serializeSnapshot(saved), oldValue,
                    "[\"companyId\",\"workItemId\",\"isApprovalRequired\",\"approvalPendingStatusId\","
                            + "\"autoActionEnabled\",\"autoActionTimeline\",\"autoActionTimeUnit\","
                            + "\"statusAfterAutoAction\",\"updatedBy\",\"isUpdatedByAdmin\"]",
                    request.getUpdatedBy(), null, company.getCompanyId(), SUCCESS, null));
            return response;
        } catch (FlickzzDeskException e) {
            auditException(request, "UPDATE", e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to update ticket closure approval workflow", e);
            auditException(request, "UPDATE", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    private String serializeSnapshot(TicketClosureApprovalConfig config) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("configId", config.getConfigId());
        snapshot.put("companyId", config.getCompany().getCompanyId());
        snapshot.put("workItemId", config.getWorkItem().getItemId());
        snapshot.put("isApprovalRequired", config.getIsApprovalRequired());
        snapshot.put("approvalPendingStatusId", config.getApprovalPendingStatus() == null
                ? null : config.getApprovalPendingStatus().getStatusId());
        snapshot.put("autoActionEnabled", config.getAutoActionEnabled());
        snapshot.put("autoActionTimeline", config.getAutoActionAfter());
        snapshot.put("autoActionTimeUnit", config.getAutoActionUnit());
        snapshot.put("autoAction", config.getAutoAction());
        snapshot.put("statusAfterAutoAction", config.getAutoApproveStatus() == null
                ? null : config.getAutoApproveStatus().getStatusCode());
        snapshot.put("isActive", config.getIsActive());
        snapshot.put("createdBy", config.getCreatedBy());
        snapshot.put("isCreatorAdmin", config.getIsCreatorAdmin());
        snapshot.put("createdAt", config.getCreatedAt());
        snapshot.put("updatedBy", config.getUpdatedBy());
        snapshot.put("isUpdaterAdmin", config.getIsUpdaterAdmin());
        snapshot.put("updatedAt", config.getUpdatedAt());
        try {
            return objectMapper.writeValueAsString(snapshot);
        } catch (Exception e) {
            log.error("Unable to serialize ticket closure approval workflow audit snapshot", e);
            throw new IllegalStateException("Unable to serialize ticket closure approval workflow audit snapshot", e);
        }
    }

    @Transactional
    public void delete(TicketClosureApprovalConfigRequestVO request) {
        TicketClosureApprovalConfig config = null;
        try {
            if (request.getConfigId() == null || request.getConfigId() <= 0 || request.getCompanyId() == null || request.getCompanyId() <= 0) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(),
                                "Configuration ID and organization ID are required"));
            }
            config = configRepository.findByConfigIdAndCompany_CompanyId(request.getConfigId(), request.getCompanyId())
                    .orElseThrow(() -> notFound("Ticket approval workflow with ID " + request.getConfigId()));

            String oldValue = serializeSnapshot(config);
            config.setIsActive(false);
            config.setUpdatedAt(LocalDateTime.now());
            TicketClosureApprovalConfig saved = configRepository.saveAndFlush(config);
            auditService.recordAudit(mapper.toSystemAuditRequest(
                    "Ticket", "Approval Workflow", "TicketClosureApprovalConfig", saved.getConfigId(),
                    "DELETE", serializeSnapshot(saved), oldValue, "[\"isActive\",\"updatedAt\"]",
                    request.getDeletedBy(), null, request.getCompanyId(), SUCCESS, null));
        } catch (FlickzzDeskException e) {
            auditException(request.getConfigId(), request.getCompanyId(), null, "DELETE", e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete ticket closure approval workflow {}", request.getConfigId(), e);
            auditException(request.getConfigId(), request.getCompanyId(), null, "DELETE", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    private void auditException(Long configId, Long orgId, Long userId, String action, Exception exception) {
        SystemAuditRequest auditRequest = mapper.toSystemAuditRequest(
                "Ticket", "Approval Workflow", "TicketClosureApprovalConfig",
                configId, action, null, null, null, userId, null, orgId, "ERROR",
                exception.getMessage());
        auditService.recordExceptionAudit(auditRequest, exception);
    }
}
