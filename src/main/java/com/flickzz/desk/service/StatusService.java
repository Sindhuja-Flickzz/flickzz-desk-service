package com.flickzz.desk.service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.CompanyMaster;
import com.flickzz.desk.model.StatusMaster;
import com.flickzz.desk.model.StatusVisibility;
import com.flickzz.desk.model.WorkItem;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.vo.StatusMasterVO;
import com.flickzz.desk.vo.StatusVisibilityVO;
import com.flickzz.desk.vo.request.StatusVisibilityUpdateRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

@Service
public class StatusService {

    private static final Logger log = LoggerFactory.getLogger(StatusService.class);
    private static final String RITM_REQUEST_TYPE = "RITM";
    @Autowired
    CommonMapper mapper;
    @Autowired
    private CompanyMasterRepository companyMasterRepository;
    @Autowired
    private AgentMasterRepository agentMasterRepository;
    @Autowired
    private StatusMasterRepository statusMasterRepository;
    @Autowired
    private StatusVisibilityRepository statusVisibilityRepository;
    @Autowired
    private WorkItemRepository workItemRepository;
    @Autowired
    private AuditService auditService;

    @Transactional
    public List<StatusMasterVO> createStatus(List<StatusMasterVO> statusVOS) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (statusVOS == null || statusVOS.isEmpty()) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM status data is required"));
            }

            StatusMasterVO firstStatus = statusVOS.get(0);
            if (firstStatus == null || firstStatus.getCompanyId() == null || firstStatus.getCompanyId() <= 0) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Company ID is required and must be valid"));
            }
            Long companyId = firstStatus.getCompanyId();
            CompanyMaster companyMaster = companyMasterRepository.findByCompanyIdAndIsActiveTrue(companyId)
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Company with ID " + companyId)));
            WorkItem workItem = workItemRepository.findByCodeAndIsActiveTrue(statusVOS.get(0).getRequestType())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "RITM work item")));

            Set<String> statusCodes = new HashSet<>();
            Set<Integer> sequenceNumbers = new HashSet<>();
            Set<String> visibleStatusCodes = new HashSet<>();
            List<StatusMaster> statusesToSave = new ArrayList<>();
            for (StatusMasterVO statusVO : statusVOS) {
                if (statusVO == null) {
                    throw new FlickzzDeskException(INVALID_REQUEST,
                            getDescription(INVALID_REQUEST.getDescription(), "RITM status data is required"));
                }
                if (!Objects.equals(statusVO.getCompanyId(), companyId)) {
                    throw new FlickzzDeskException(INVALID_REQUEST,
                            getDescription(INVALID_REQUEST.getDescription(), "All RITM statuses must use the same company"));
                }
                if (statusVO.getStatusCode() == null || statusVO.getStatusCode().isBlank()
                        || statusVO.getStatusCode().length() > 50) {
                    throw new FlickzzDeskException(INVALID_REQUEST,
                            getDescription(INVALID_REQUEST.getDescription(), "Status code is required and must not exceed 50 characters"));
                }
                if (statusVO.getSequenceNo() == null || statusVO.getSequenceNo() < 0) {
                    throw new FlickzzDeskException(INVALID_REQUEST,
                            getDescription(INVALID_REQUEST.getDescription(), "Sequence number is required and must be non-negative"));
                }
                if (statusVO.getStatusColor() != null && !statusVO.getStatusColor().matches("^#[0-9A-Fa-f]{6}$")) {
                    throw new FlickzzDeskException(INVALID_REQUEST,
                            getDescription(INVALID_REQUEST.getDescription(), "Status color must be a six-digit hex color"));
                }
                if (statusVO.getVisibleStatuses() == null) {
                    throw new FlickzzDeskException(INVALID_REQUEST,
                            getDescription(INVALID_REQUEST.getDescription(), "Visible statuses are required"));
                }

                String statusCode = statusVO.getStatusCode();
                Integer sequenceNo = statusVO.getSequenceNo();
                if (!statusCodes.add(statusCode)
                        || statusMasterRepository.existsByCompany_CompanyIdAndWorkItem_ItemIdAndStatusCode(
                        companyId, workItem.getItemId(), statusCode)) {
                    throw new FlickzzDeskException(ALREADY_EXISTS,
                            getDescription(ALREADY_EXISTS.getDescription(), "RITM status code"));
                }
                if (!sequenceNumbers.add(sequenceNo)
                        || statusMasterRepository.existsByCompany_CompanyIdAndWorkItem_ItemIdAndSequenceNo(
                        companyId, workItem.getItemId(), sequenceNo)) {
                    throw new FlickzzDeskException(ALREADY_EXISTS,
                            getDescription(ALREADY_EXISTS.getDescription(), "RITM status sequence"));
                }

                Set<String> currentVisibleStatusCodes = new HashSet<>();
                for (String visibleStatusCode : getVisibleStatusCodes(statusVO.getVisibleStatuses())) {
                    if (visibleStatusCode == null || visibleStatusCode.isBlank() || visibleStatusCode.length() > 50) {
                        throw new FlickzzDeskException(INVALID_REQUEST,
                                getDescription(INVALID_REQUEST.getDescription(), "Visible status codes must be non-empty and not exceed 50 characters"));
                    }
                    if (!currentVisibleStatusCodes.add(visibleStatusCode)) {
                        throw new FlickzzDeskException(INVALID_REQUEST,
                                getDescription(INVALID_REQUEST.getDescription(), "Duplicate visible status code"));
                    }
                    visibleStatusCodes.add(visibleStatusCode);
                }

                StatusMaster status = StatusMaster.builder()
                        .company(companyMaster)
                        .workItem(workItem)
                        .statusCode(statusCode)
                        .sequenceNo(sequenceNo)
                        .statusColor(statusVO.getStatusColor())
                        .isActive(statusVO.getIsActive() != null ? statusVO.getIsActive() : true)
                        .createdBy(statusVO.getCreatedBy())
                        .isCreatorAdmin(statusVO.getIsCreatorAdmin() != null ? statusVO.getIsCreatorAdmin() : false)
                        .build();
                statusesToSave.add(status);
            }

            if (!visibleStatusCodes.isEmpty()) {
                Set<String> availableStatusCodes = new HashSet<>(statusCodes);
                statusMasterRepository.findByCompany_CompanyIdAndWorkItem_ItemIdAndStatusCodeIn(
                                companyId, workItem.getItemId(), new ArrayList<>(visibleStatusCodes))
                        .forEach(status -> availableStatusCodes.add(status.getStatusCode()));
                if (!availableStatusCodes.containsAll(visibleStatusCodes)) {
                    throw new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "One or more visible RITM statuses"));
                }
            }

            List<StatusMaster> savedStatuses = statusMasterRepository.saveAllAndFlush(statusesToSave);
            if (!visibleStatusCodes.isEmpty()) {
                Map<String, StatusMaster> statusesByCode = new HashMap<>();
                statusMasterRepository.findByCompany_CompanyIdAndWorkItem_ItemIdAndStatusCodeIn(
                                companyId, workItem.getItemId(), new ArrayList<>(visibleStatusCodes))
                        .forEach(status -> statusesByCode.put(status.getStatusCode(), status));
                savedStatuses.forEach(status -> statusesByCode.put(status.getStatusCode(), status));

                List<StatusVisibility> visibilityRows = new ArrayList<>();
                for (int index = 0; index < statusVOS.size(); index++) {
                    StatusMasterVO statusVO = statusVOS.get(index);
                    StatusMaster currentStatus = savedStatuses.get(index);
                    for (String visibleStatusCode : getVisibleStatusCodes(statusVO.getVisibleStatuses())) {
                        StatusMaster visibleStatus = statusesByCode.get(visibleStatusCode);
                        visibilityRows.add(new StatusVisibility(null, companyMaster, workItem, currentStatus,
                                visibleStatus, true, statusVO.getCreatedBy(), null,
                                statusVO.getIsCreatorAdmin() != null ? statusVO.getIsCreatorAdmin() : false,
                                false, null, null));
                    }
                }
                statusVisibilityRepository.saveAllAndFlush(visibilityRows);
            }

            for (StatusMaster savedStatus : savedStatuses) {
                recordSystemAudit("RitmStatus", savedStatus.getStatusId(), CREATE, null,
                        savedStatus.getStatusCode(), savedStatus.getCreatedBy(), savedStatus.getCompany().getCompanyId(), SUCCESS, null);
            }
            return savedStatuses.stream().map(savedStatus -> StatusMasterVO.builder()
                    .statusId(savedStatus.getStatusId())
                    .statusCode(savedStatus.getStatusCode())
                    .isActive(savedStatus.getIsActive())
                    .sequenceNo(savedStatus.getSequenceNo())
                    .createdBy(savedStatus.getCreatedBy())
                    .isCreatorAdmin(savedStatus.getIsCreatorAdmin())
                    .build()).toList();
        } catch (FlickzzDeskException e) {
            StatusMasterVO firstStatus = statusVOS != null
                    ? statusVOS.stream().filter(Objects::nonNull).findFirst().orElse(null)
                    : null;
            recordSystemAudit("RitmStatus", null, CREATE, null,
                    firstStatus != null ? firstStatus.getStatusCode() : null,
                    firstStatus != null ? firstStatus.getCreatedBy() : null,
                    firstStatus != null ? firstStatus.getCompanyId() : null, FAILED, e.getDescription());
            throw e;
        } catch (Exception e) {
            StatusMasterVO firstStatus = statusVOS != null
                    ? statusVOS.stream().filter(Objects::nonNull).findFirst().orElse(null)
                    : null;
            recordSystemAudit("RitmStatus", null, CREATE, null,
                    firstStatus != null ? firstStatus.getStatusCode() : null,
                    firstStatus != null ? firstStatus.getCreatedBy() : null,
                    firstStatus != null ? firstStatus.getCompanyId() : null, FAILED, e.getMessage());
            log.error("Exception in createRitmStatus method in RitmService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    private List<String> getVisibleStatusCodes(List<StatusVisibilityVO> visibleStatuses) {
        if (visibleStatuses == null) {
            return Collections.emptyList();
        }
        return visibleStatuses.stream()
                .map(visibility -> visibility == null ? null
                        : visibility.getStatusCode() != null
                        ? visibility.getStatusCode()
                        : visibility.getVisibleStatus() != null
                        ? visibility.getVisibleStatus().getStatusCode()
                        : null)
                .toList();
    }

    private void recordSystemAudit(String entityName, Long entityId, String action, String oldValue,
                                   String newValue, Long userId, Long companyId, String status,
                                   String errorMessage) {
        auditService.recordAudit(mapper.toSystemAuditRequest(
                "RITM", "RITM", entityName, entityId, action, newValue, oldValue,
                null, userId, null, companyId, status, errorMessage));
    }

    @Transactional(readOnly = true)
    public List<StatusMasterVO> getStatusByOrgId(Long orgId, Boolean active) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        List<StatusMaster> statuses = Boolean.TRUE.equals(active)
                ? statusMasterRepository.findByCompanyCompanyIdAndIsActiveTrue(orgId)
                : statusMasterRepository.findByCompanyCompanyId(orgId);
        return statuses.stream().map(mapper::toStatusMasterVo).toList();
    }

    @Transactional
    public void deleteStatus(Long statusId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        StatusMaster status = null;
        try {
            if (statusId == null || statusId <= 0) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "RITM status ID is required and must be valid"));
            }

            status = statusMasterRepository.findById(statusId)
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "RITM status with ID " + statusId)));

            Long companyId = status.getCompany() != null ? status.getCompany().getCompanyId() : null;
            Long actorId = status.getUpdatedBy() != null ? status.getUpdatedBy() : status.getCreatedBy();
            String statusCode = status.getStatusCode();

            statusMasterRepository.delete(status);
            statusMasterRepository.flush();
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

    @Transactional
    public void updateStatus(StatusVisibilityUpdateRequestVO status) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        StatusMaster existingStatus = null;
        try {
            if (status == null
                    || status.getStatusId() == null || status.getStatusId() <= 0
                    || status.getCompanyId() == null || status.getCompanyId() <= 0
                    || status.getUpdatedBy() == null || status.getUpdatedBy() <= 0
                    || status.getRequestType() == null || status.getRequestType().isBlank()
                    || status.getStatusCode() == null || status.getStatusCode().isBlank()
                    || status.getVisibleStatuses() == null
                    || status.getIsUpdaterAdmin() == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(),
                                "Status ID, company, request type, status code, visibility list, updater, and updater admin flag are required and must be valid"));
            }

            existingStatus = statusMasterRepository.findById(status.getStatusId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "RITM status with ID " + status.getStatusId())));

            Long companyId = existingStatus.getCompany() == null ? null : existingStatus.getCompany().getCompanyId();
            Long workItemId = existingStatus.getWorkItem() == null ? null : existingStatus.getWorkItem().getItemId();
            if (companyId == null || workItemId == null || !Objects.equals(status.getCompanyId(), companyId)) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(),
                                "RITM status does not belong to the provided company or has no valid work item"));
            }
            CompanyMaster company = companyMasterRepository.findByCompanyIdAndIsActiveTrue(companyId)
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Company with ID " + companyId)));
            WorkItem requestWorkItem = workItemRepository.findByCodeAndIsActiveTrue(status.getRequestType().trim())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(),
                                    "Work item with code " + status.getRequestType().trim())));
            if (!Objects.equals(requestWorkItem.getItemId(), workItemId)) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(),
                                "Status does not belong to the provided request type"));
            }
            var actor = agentMasterRepository.findById(status.getUpdatedBy())
                    .filter(agent -> Boolean.TRUE.equals(agent.getIsActive())
                            && agent.getOrganization() != null
                            && Objects.equals(agent.getOrganization().getCompanyId(), companyId))
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(),
                                    "Active agent with ID " + status.getUpdatedBy() + " in company " + companyId)));
            if (!Objects.equals(existingStatus.getStatusCode(), status.getStatusCode().trim())) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Status code does not match the requested status ID"));
            }

            Set<String> visibleStatusCodes = new LinkedHashSet<>();
            for (String visibleStatusCode : status.getVisibleStatuses()) {
                if (visibleStatusCode == null || visibleStatusCode.isBlank()
                        || visibleStatusCode.trim().length() > 50) {
                    throw new FlickzzDeskException(INVALID_REQUEST,
                            getDescription(INVALID_REQUEST.getDescription(),
                                    "Visible status codes must be non-empty and not exceed 50 characters"));
                }
                if (!visibleStatusCodes.add(visibleStatusCode.trim())) {
                    throw new FlickzzDeskException(INVALID_REQUEST,
                            getDescription(INVALID_REQUEST.getDescription(), "Duplicate visible status code"));
                }
            }

            Map<String, StatusMaster> statusesByCode = new HashMap<>();
            if (!visibleStatusCodes.isEmpty()) {
                statusMasterRepository.findByCompany_CompanyIdAndWorkItem_ItemIdAndStatusCodeIn(
                                companyId, workItemId, new ArrayList<>(visibleStatusCodes))
                        .forEach(visibleStatus -> statusesByCode.put(visibleStatus.getStatusCode(), visibleStatus));
                if (!statusesByCode.keySet().containsAll(visibleStatusCodes)) {
                    Set<String> missing = new LinkedHashSet<>(visibleStatusCodes);
                    missing.removeAll(statusesByCode.keySet());
                    throw new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(),
                                    "Visible RITM status(es) not found: " + String.join(", ", missing)));
                }
            }

            List<StatusVisibility> existingVisibility = statusVisibilityRepository
                    .findByCompanyCompanyIdAndWorkItemItemIdAndCurrentStatusStatusId(
                            companyId, workItemId, existingStatus.getStatusId());
            Map<Long, StatusVisibility> visibilityByStatusId = new HashMap<>();
            for (StatusVisibility visibility : existingVisibility) {
                if (visibility.getVisibleStatus() != null) {
                    visibilityByStatusId.put(visibility.getVisibleStatus().getStatusId(), visibility);
                }
            }

            List<StatusVisibility> visibilityUpdates = new ArrayList<>();
            for (String visibleStatusCode : visibleStatusCodes) {
                StatusMaster visibleStatus = statusesByCode.get(visibleStatusCode);
                StatusVisibility visibility = visibilityByStatusId.remove(visibleStatus.getStatusId());
                if (visibility == null) {
                    visibility = new StatusVisibility(null, company, requestWorkItem, existingStatus,
                            visibleStatus, true, actor.getAgentId(), null,
                            Boolean.FALSE, status.getIsUpdaterAdmin(), actor.getAgentId(), null);
                } else {
                    visibility.setIsActive(true);
                    visibility.setUpdatedBy(actor.getAgentId());
                    visibility.setIsUpdaterAdmin(status.getIsUpdaterAdmin());
                }
                visibilityUpdates.add(visibility);
            }

            for (StatusVisibility removedVisibility : visibilityByStatusId.values()) {
                removedVisibility.setIsActive(false);
                removedVisibility.setUpdatedBy(actor.getAgentId());
                removedVisibility.setIsUpdaterAdmin(status.getIsUpdaterAdmin());
                visibilityUpdates.add(removedVisibility);
            }
            if (!visibilityUpdates.isEmpty()) {
                statusVisibilityRepository.saveAllAndFlush(visibilityUpdates);
            }
            existingStatus.setStatusColor(status.getStatusColor());
            statusMasterRepository.save(existingStatus);

            recordSystemAudit("RitmStatus", existingStatus.getStatusId(), UPDATE,
                    null, String.join(", ", visibleStatusCodes),
                    actor.getAgentId(), companyId, SUCCESS, null);
            log.info(generateLog(EXIT, this.getClass().getName()));
        } catch (FlickzzDeskException e) {
            recordSystemAudit("RitmStatus", status != null ? status.getStatusId() : null,
                    UPDATE, status != null ? status.getStatusCode() : null,
                    null,
                    status != null ? status.getUpdatedBy() : null,
                    status != null ? status.getCompanyId() : null,
                    FAILED, e.getDescription());
            throw e;
        } catch (Exception e) {
            recordSystemAudit("RitmStatus", status != null ? status.getStatusId() : null,
                    UPDATE, status != null ? status.getStatusCode() : null,
                    null,
                    status != null ? status.getUpdatedBy() : null,
                    status != null ? status.getCompanyId() : null,
                    FAILED, e.getMessage());
            log.error("Exception in updateStatus method in StatusService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    @Transactional
    public void changeStatus(StatusMasterVO status) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        StatusMaster existingStatus = null;
        try {
            if (status == null || status.getStatusId() == null || status.getStatusId() <= 0
                    || status.getIsActive() == null
                    || status.getUpdatedBy() == null || status.getUpdatedBy() <= 0
                    || status.getIsUpdaterAdmin() == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(),
                                "Status ID, active flag, updater, and updater admin flag are required"));
            }

            existingStatus = statusMasterRepository.findById(status.getStatusId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(),
                                    "RITM status with ID " + status.getStatusId())));

            Long companyId = existingStatus.getCompany() != null
                    ? existingStatus.getCompany().getCompanyId() : null;
            Long workItemId = existingStatus.getWorkItem() != null
                    ? existingStatus.getWorkItem().getItemId() : null;
            if (companyId == null || workItemId == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(),
                                "Status must belong to a valid company and work item"));
            }

            var actor = agentMasterRepository.findById(status.getUpdatedBy())
                    .filter(agent -> Boolean.TRUE.equals(agent.getIsActive())
                            && agent.getOrganization() != null
                            && Objects.equals(agent.getOrganization().getCompanyId(), companyId))
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(),
                                    "Active agent with ID " + status.getUpdatedBy()
                                            + " in company " + companyId)));

            Boolean previousIsActive = existingStatus.getIsActive();
            existingStatus.setIsActive(status.getIsActive());
            existingStatus.setUpdatedBy(actor.getAgentId());
            existingStatus.setIsUpdaterAdmin(status.getIsUpdaterAdmin());
            statusMasterRepository.saveAndFlush(existingStatus);

            List<StatusVisibility> visibilityRows =
                    statusVisibilityRepository.findByCompanyCompanyIdAndWorkItemItemIdAndCurrentStatusStatusId(
                            companyId, workItemId, existingStatus.getStatusId());
            for (StatusVisibility visibility : visibilityRows) {
                visibility.setIsActive(status.getIsActive());
                visibility.setUpdatedBy(actor.getAgentId());
                visibility.setIsUpdaterAdmin(status.getIsUpdaterAdmin());
            }
            if (!visibilityRows.isEmpty()) {
                statusVisibilityRepository.saveAllAndFlush(visibilityRows);
            }

            recordSystemAudit("RitmStatus", existingStatus.getStatusId(), UPDATE,
                    String.valueOf(previousIsActive), String.valueOf(status.getIsActive()),
                    actor.getAgentId(), companyId, SUCCESS, null);
            log.info(generateLog(EXIT, this.getClass().getName()));
        } catch (FlickzzDeskException e) {
            recordSystemAudit("RitmStatus", status != null ? status.getStatusId() : null,
                    UPDATE, existingStatus != null ? String.valueOf(existingStatus.getIsActive()) : null,
                    status != null && status.getIsActive() != null
                            ? String.valueOf(status.getIsActive()) : null,
                    status != null ? status.getUpdatedBy() : null,
                    existingStatus != null && existingStatus.getCompany() != null
                            ? existingStatus.getCompany().getCompanyId() : null,
                    FAILED, e.getDescription());
            throw e;
        } catch (Exception e) {
            recordSystemAudit("RitmStatus", status != null ? status.getStatusId() : null,
                    UPDATE, existingStatus != null ? String.valueOf(existingStatus.getIsActive()) : null,
                    status != null && status.getIsActive() != null
                            ? String.valueOf(status.getIsActive()) : null,
                    status != null ? status.getUpdatedBy() : null,
                    existingStatus != null && existingStatus.getCompany() != null
                            ? existingStatus.getCompany().getCompanyId() : null,
                    FAILED, e.getMessage());
            log.error("Exception in changeStatus method in StatusService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }
}