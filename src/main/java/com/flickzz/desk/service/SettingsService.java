package com.flickzz.desk.service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.BusinessService;
import com.flickzz.desk.model.CompanyMaster;
import com.flickzz.desk.model.ServiceOffering;
import com.flickzz.desk.model.WorkItem;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.vo.BusinessServiceVO;
import com.flickzz.desk.vo.FieldTypeVO;
import com.flickzz.desk.vo.WorkItemVO;
import com.flickzz.desk.vo.request.BusinessServiceRequestVO;
import com.flickzz.desk.vo.request.WorkItemDeleteRequestVO;
import com.flickzz.desk.vo.request.WorkItemRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

@Service
public class SettingsService {

    private static final Logger log = LoggerFactory.getLogger(SettingsService.class);

    @Autowired
    private BusinessServiceRepository businessServiceRepository;

    @Autowired
    private ServiceOfferingRepository serviceOfferingRepository;

    @Autowired
    private WorkItemRepository workItemRepository;

    @Autowired
    private CompanyMasterRepository companyMasterRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FieldTypeRepository fieldTypeRepository;

    @Autowired
    private CommonMapper mapper;

    public BusinessServiceVO createBusinessService(BusinessServiceRequestVO businessServiceRequestVO) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (businessServiceRequestVO == null || businessServiceRequestVO.getServiceName() == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Service Name"));
            } else if (businessServiceRequestVO.getServiceOfferings() == null
                    || businessServiceRequestVO.getServiceOfferings().size() == 0) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Service Offerings"));
            }

            businessServiceRepository.findByServiceName(businessServiceRequestVO.getServiceName()).ifPresent(s -> {
                throw new FlickzzDeskException(ALREADY_EXISTS,
                        getDescription(ALREADY_EXISTS.getDescription(), "Business Service"));
            });

            BusinessService saved = businessServiceRepository
                    .save(mapper.toBusinessServiceEntity(businessServiceRequestVO));

            businessServiceRequestVO.getServiceOfferings().stream().forEach(so -> {
                ServiceOffering serviceOffering = mapper.toServiceOfferingEntity(so, saved);
                serviceOfferingRepository.save(serviceOffering);
            });

            return mapper.toBusinessServiceVO(saved);

        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in createBusinessService method in SettingsService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public BusinessServiceVO updateBusinessService(BusinessServiceRequestVO businessServiceRequestVO) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (businessServiceRequestVO == null || businessServiceRequestVO.getServiceId() == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Service Id"));
            } else if (businessServiceRequestVO.getServiceName() == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Service Name"));
            } else if (businessServiceRequestVO.getServiceOfferings() == null
                    || businessServiceRequestVO.getServiceOfferings().size() == 0) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Service Offerings"));
            }

            BusinessService existing = businessServiceRepository
                    .findByServiceId(businessServiceRequestVO.getServiceId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Business Service")));

            existing.getServiceOfferings().clear();
            existing.setUpdatedBy(businessServiceRequestVO.getUpdatedBy());
            BusinessService saved = businessServiceRepository.save(existing);

            businessServiceRequestVO.getServiceOfferings().stream().forEach(so -> {
                ServiceOffering serviceOffering = mapper.toServiceOfferingEntity(so, saved);
                serviceOfferingRepository.save(serviceOffering);
            });

            return mapper.toBusinessServiceVO(saved);

        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in updateBusinessService method in SettingsService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<BusinessServiceVO> listBusinessServices() {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            return businessServiceRepository.findAll().stream().map(mapper::toBusinessServiceVO).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in listBusinessServices method in SettingsService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public BusinessServiceVO getBusinessServiceInfo(String serviceId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            BusinessService existing = businessServiceRepository.findByServiceId(Long.valueOf(serviceId))
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Business Service")));
            return mapper.toBusinessServiceVO(existing);
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getBusinessServiceInfo method in SettingsService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public void deleteBusinessService(String serviceId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            BusinessService existing = businessServiceRepository.findByServiceId(Long.valueOf(serviceId))
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Business Service")));
            existing.setIsActive(DEACTIVATE);
            businessServiceRepository.save(existing);
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in deleteBusinessService method in SettingsService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    @Transactional
    public WorkItemVO createWorkItem(WorkItemRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (request == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Work item"));
            }
            if (request.getCode() == null || request.getCode().isBlank()
                    || request.getCode().trim().length() > 20) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Work item code"));
            }
            if (request.getDescription() == null || request.getDescription().isBlank()
                    || request.getDescription().trim().length() > 100) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Work item description"));
            }
            if (request.getCompanyId() == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), COMPANY));
            }
            if (request.getCreatedBy() == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Created By"));
            }

            CompanyMaster company = companyMasterRepository.findByCompanyIdAndIsActiveTrue(request.getCompanyId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), COMPANY)));

            String code = request.getCode().trim();
            workItemRepository.findByCode(code).ifPresent(existing -> {
                throw new FlickzzDeskException(ALREADY_EXISTS,
                        getDescription(ALREADY_EXISTS.getDescription(), "Work item code"));
            });

            WorkItem workItem = WorkItem.builder()
                    .code(code)
                    .description(request.getDescription().trim())
                    .company(company)
                    .createdBy(request.getCreatedBy())
                    .isCreatorAdmin(Boolean.TRUE.equals(request.getIsCreatedByAdmin()))
                    .build();

            WorkItem saved = workItemRepository.save(workItem);
            recordWorkItemAudit(saved, "CREATE", null, "[\"code\",\"description\",\"companyId\"]",
                    saved.getCreatedBy());
            return mapper.toWorkItemVO(saved);
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in createWorkItem method in SettingsService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    private void recordWorkItemAudit(WorkItem workItem, String action, String oldValue,
                                     String changedFields, Long userId) {
        auditService.recordAudit(mapper.toSystemAuditRequest(
                "Settings",
                "Work Item",
                "WorkItem",
                workItem.getItemId(),
                action,
                serializeWorkItemSnapshot(workItem),
                oldValue,
                changedFields,
                userId,
                "SYSTEM",
                workItem.getCompany().getCompanyId(),
                "SUCCESS",
                null));
    }

    private String serializeWorkItemSnapshot(WorkItem workItem) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("itemId", workItem.getItemId());
        snapshot.put("code", workItem.getCode());
        snapshot.put("description", workItem.getDescription());
        snapshot.put("companyId", workItem.getCompany().getCompanyId());
        snapshot.put("isActive", workItem.getIsActive());
        snapshot.put("createdBy", workItem.getCreatedBy());
        snapshot.put("isCreatorAdmin", workItem.getIsCreatorAdmin());
        snapshot.put("updatedBy", workItem.getUpdatedBy());
        snapshot.put("isUpdaterAdmin", workItem.getIsUpdaterAdmin());
        try {
            return objectMapper.writeValueAsString(snapshot);
        } catch (Exception e) {
            log.error("Unable to serialize work item audit snapshot", e);
            throw new IllegalStateException("Unable to serialize work item audit snapshot", e);
        }
    }

    @Transactional
    public void deleteWorkItem(WorkItemDeleteRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (request == null || request.getItemId() == null || request.getItemId() <= 0
                    || request.getOrgId() == null || request.getOrgId() <= 0
                    || request.getDeletedBy() == null || request.getDeletedBy() <= 0
                    || request.getIsDeletedByAdmin() == null) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Work item delete data is invalid"));
            }

            WorkItem workItem = workItemRepository
                    .findByItemIdAndCompany_CompanyId(request.getItemId(), request.getOrgId())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(),
                                    "Work item with ID " + request.getItemId())));

            if (!Boolean.TRUE.equals(workItem.getIsActive())) {
                throw new FlickzzDeskException(INACTIVE_ERROR,
                        getDescription(INACTIVE_ERROR.getDescription(), "Work item"));
            }

            String oldValue = serializeWorkItemSnapshot(workItem);
            workItem.setIsActive(false);
            workItem.setUpdatedBy(request.getDeletedBy());
            workItem.setIsUpdaterAdmin(request.getIsDeletedByAdmin());
            recordWorkItemAudit(workItem, "DELETE", oldValue,
                    "[\"isActive\",\"updatedBy\",\"isUpdaterAdmin\"]", request.getDeletedBy());
            workItemRepository.delete(workItem);
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in deleteWorkItem method in SettingsService", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<WorkItemVO> listWorkItems(String orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            return workItemRepository.findByIsActive(ACTIVE).stream().map(mapper::toWorkItemVO).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in listWorkItems method in SettingsService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<FieldTypeVO> listFieldTypes(String orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            return fieldTypeRepository.findByIsActive(ACTIVE).stream().map(mapper::toFieldTypeVO).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in listFieldTypes method in SettingsService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }
}
