package com.flickzz.desk.service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.*;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.vo.TemplateFieldDetailVO;
import com.flickzz.desk.vo.TemplateFieldVO;
import com.flickzz.desk.vo.TemplateVO;
import com.flickzz.desk.vo.request.TemplateDetailsRequestVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.flickzz.desk.config.FlickzzDeskConstants.ACTIVE;
import static com.flickzz.desk.config.FlickzzDeskConstants.CREATE;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

@Service
@Transactional
public class TemplateDetailsService {

    private static final Logger log = LoggerFactory.getLogger(TemplateDetailsService.class);

    @Autowired
    private TemplateDetailsRepository templateDetailsRepository;

    @Autowired
    private TemplateDetailFieldRepository templateDetailFieldRepository;

    @Autowired
    private TemplateFieldOptionRepository templateFieldOptionRepository;

    @Autowired
    private CompanyMasterRepository companyMasterRepository;

    @Autowired
    private WorkItemRepository workItemRepository;

    @Autowired
    private FieldTypeRepository fieldTypeRepository;

    @Autowired
    private CommonMapper mapper;

    @Autowired
    private AuditService auditService;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public TemplateFieldVO createTemplateFieldDetails(TemplateDetailsRequestVO request) {
        return saveTemplateFieldDetails(request, "CREATE");
    }

    private TemplateFieldVO saveTemplateFieldDetails(TemplateDetailsRequestVO request, String action) {
        log.info(generateLog(action.toLowerCase() + "TemplateFieldDetails", this.getClass().getName()));
        TemplateField field = null;
        try {
            validateTemplateFieldDetailsRequest(request);

            if (StringUtils.equalsIgnoreCase(action, CREATE)) {
                field = templateDetailFieldRepository.findByFieldIdAndDefaultValueIsNull(request.getFieldId())
                        .orElseThrow(() -> new FlickzzDeskException(VERIFY_LIST_PAGE,
                                getDescription(VERIFY_LIST_PAGE.getDescription(), "Default Value for this template")));
            } else {
                field = templateDetailFieldRepository.findById(request.getFieldId())
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), "Template Field")));
            }

            String oldValue = fieldSnapshot(field);
            field.setDefaultValue(request.getDefaultValue());
            if (request.getIsEditable() != null) {
                field.setEditable(request.getIsEditable());
            }
            field.setUpdatedBy(request.getUpdatedBy() != null ? request.getUpdatedBy() : request.getCreatedBy());
            field.setIsUpdaterAdmin(Boolean.TRUE.equals(request.getIsUpdatedByAdmin()));

            TemplateField savedField = templateDetailFieldRepository.save(field);
            auditService.recordAudit(mapper.toSystemAuditRequest("Template", "Template Field", "TemplateField",
                    savedField.getFieldId(), action, fieldSnapshot(savedField), oldValue, null,
                    request.getUpdatedBy() != null ? request.getUpdatedBy() : request.getCreatedBy(), "SYSTEM",
                    request.getCompanyId(), "SUCCESS", null));
            return mapper.toTemplateDetailFieldVO(savedField);
        } catch (FlickzzDeskException e) {
            recordTemplateFieldExceptionAudit(request, field, action, e);
            throw e;
        } catch (Exception e) {
            log.error("Exception in {}TemplateFieldDetails method in TemplateDetailsService: {}", action.toLowerCase(),
                    e.getMessage());
            recordTemplateFieldExceptionAudit(request, field, action, e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    private void validateTemplateFieldDetailsRequest(TemplateDetailsRequestVO request) {
        if (request == null) {
            throw new FlickzzDeskException(INVALID_FIELD,
                    getDescription(INVALID_FIELD.getDescription(), "Template Field Details"));
        }
        if (request.getCompanyId() == null) {
            throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), "Organization ID"));
        }
        if (request.getFieldId() == null) {
            throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), "Field ID"));
        }
        if (request.getCreatedBy() == null && request.getUpdatedBy() == null) {
            throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), "User ID"));
        }
    }

    private String fieldSnapshot(TemplateField field) throws Exception {
        java.util.Map<String, Object> snapshot = new java.util.LinkedHashMap<>();
        snapshot.put("fieldId", field.getFieldId());
        snapshot.put("templateId", field.getTemplate().getTemplateId());
        snapshot.put("defaultValue", field.getDefaultValue());
        snapshot.put("isEditable", field.isEditable());
        snapshot.put("updatedBy", field.getUpdatedBy());
        return objectMapper.writeValueAsString(snapshot);
    }

    private void recordTemplateFieldExceptionAudit(TemplateDetailsRequestVO request, TemplateField field,
                                                   String action, Exception exception) {
        auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Template", "Template Field", "TemplateField",
                field != null ? field.getFieldId() : request != null ? request.getFieldId() : null, action, null, null,
                null, request != null ? request.getUpdatedBy() : null, "SYSTEM",
                request != null ? request.getCompanyId() : null, "FAILED", exception.getMessage()), exception);
    }

    @Transactional
    public TemplateFieldVO updateTemplateFieldDetails(TemplateDetailsRequestVO request) {
        return saveTemplateFieldDetails(request, "UPDATE");
    }

    @Transactional
    public TemplateVO createTemplateDetails(TemplateDetailsRequestVO request) {
        log.info(generateLog("createTemplateDetails", this.getClass().getName()));
        try {
            // Validate request
            if (request == null || request.getTemplateName() == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Template Name"));
            }

            if (request.getCompanyId() == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Company ID"));
            }

            if (request.getWorkItemId() == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Work Item ID"));
            }

            if (request.getTemplateDetails() == null || request.getTemplateDetails().isEmpty()) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Template Details"));
            }

            // Check if company exists
            Optional<CompanyMaster> companyOpt = companyMasterRepository.findById(request.getCompanyId());
            if (companyOpt.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Company"));
            }

            Optional<WorkItem> workItemOpt = workItemRepository.findById(request.getWorkItemId());
            if (workItemOpt.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Work Item"));
            }

            // Check if template with same name already exists for this company
            Optional<Template> existingTemplate = templateDetailsRepository
                    .findByTemplateNameAndCompany_CompanyId(request.getTemplateName(), request.getCompanyId());
            if (existingTemplate.isPresent()) {
                throw new FlickzzDeskException(ALREADY_EXISTS,
                        getDescription(ALREADY_EXISTS.getDescription(), "Template Name"));
            }

            // Create template entity
            Template entity = Template.builder().templateName(request.getTemplateName())
                    .workItem(workItemOpt.get()).company(companyOpt.get()).isActive(true)
                    .createdBy(request.getCreatedBy())
                    .isCreatorAdmin(request.getIsCreatedByAdmin() != null ? request.getIsCreatedByAdmin() : false)
                    .updatedBy(request.getCreatedBy())
                    .isUpdaterAdmin(request.getIsUpdatedByAdmin() != null ? request.getIsUpdatedByAdmin() : false)
                    .build();

            Template savedEntity = templateDetailsRepository.save(entity);

            // Save template fields and options
            if (request.getTemplateDetails() != null) {
                int fieldSequence = 0;
                for (TemplateFieldDetailVO fieldDetail : request.getTemplateDetails()) {
                    Optional<FieldType> fieldTypeOpt = fieldTypeRepository
                            .findByTypeIdAndIsActive(fieldDetail.getFieldTypeId(), ACTIVE);
                    if (fieldTypeOpt.isEmpty()) {
                        throw new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), "Field Type"));
                    }

                    TemplateField field = TemplateField.builder().template(savedEntity)
                            .fieldName(fieldDetail.getFieldName()).fieldType(fieldTypeOpt.get())
                            .mandatory(Boolean.TRUE.equals(fieldDetail.getMandatory())).fieldSequence(fieldSequence++)
                            .isActive(true).createdBy(request.getCreatedBy())
                            .isCreatorAdmin(
                                    request.getIsCreatedByAdmin() != null ? request.getIsCreatedByAdmin() : false)
                            .updatedBy(request.getCreatedBy())
                            .isUpdaterAdmin(
                                    request.getIsUpdatedByAdmin() != null ? request.getIsUpdatedByAdmin() : false)
                            .build();

                    TemplateField savedField = templateDetailFieldRepository.save(field);

                    // Save field options
                    if (fieldDetail.getOptions() != null) {
                        int optionSequence = 0;
                        for (com.flickzz.desk.vo.TemplateFieldOptionVO option : fieldDetail.getOptions()) {
                            com.flickzz.desk.model.TemplateFieldOption fieldOption = com.flickzz.desk.model.TemplateFieldOption
                                    .builder().field(savedField).label(option.getLabel()).value(option.getValue())
                                    .defaultSelected(Boolean.TRUE.equals(option.getDefaultSelected()))
                                    .optionSequence(optionSequence++).isActive(true).createdBy(request.getCreatedBy())
                                    .isCreatorAdmin(
                                            request.getIsCreatedByAdmin() != null ? request.getIsCreatedByAdmin()
                                                    : false)
                                    .updatedBy(request.getCreatedBy())
                                    .isUpdaterAdmin(
                                            request.getIsUpdatedByAdmin() != null ? request.getIsUpdatedByAdmin()
                                                    : false)
                                    .build();

                            templateFieldOptionRepository.save(fieldOption);
                        }
                    }
                }
            }

            return mapper.toTemplateDetailsVO(savedEntity);

        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in createTemplateDetails method in TemplateDetailsService: {}", e.getMessage());
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    @Transactional
    public TemplateVO updateTemplateDetails(TemplateDetailsRequestVO request) {
        log.info(generateLog("updateTemplateDetails", this.getClass().getName()));
        try {
            // Validate request
            if (request == null || request.getTemplateId() == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Template ID"));
            }

            if (request.getCompanyId() == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Company ID"));
            }

            // Check if template exists
            Optional<Template> existingTemplate = templateDetailsRepository
                    .findByTemplateIdAndCompany_CompanyId(request.getTemplateId(), request.getCompanyId());
            if (existingTemplate.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Template"));
            }

            Template entity = existingTemplate.get();

            // Update fields
            if (request.getTemplateName() != null) {
                entity.setTemplateName(request.getTemplateName());
            }
            entity.setUpdatedBy(request.getUpdatedBy());
            entity.setIsUpdaterAdmin(request.getIsUpdatedByAdmin() != null ? request.getIsUpdatedByAdmin() : false);

            Template updatedEntity = templateDetailsRepository.save(entity);

            // Update template fields and options if provided
            if (request.getTemplateDetails() != null && !request.getTemplateDetails().isEmpty()) {
                // Remove existing fields from DB first to avoid unique constraint violations
                templateDetailFieldRepository.deleteByTemplateTemplateId(updatedEntity.getTemplateId());
                templateDetailFieldRepository.flush();

                if (updatedEntity.getFields() == null) {
                    updatedEntity.setFields(new ArrayList<>());
                } else {
                    updatedEntity.getFields().clear();
                }

                // Add new fields
                int fieldSequence = 0;
                for (TemplateFieldDetailVO fieldDetail : request.getTemplateDetails()) {
                    Optional<FieldType> fieldTypeOpt = fieldTypeRepository
                            .findByTypeIdAndIsActive(fieldDetail.getFieldTypeId(), ACTIVE);
                    if (fieldTypeOpt.isEmpty()) {
                        throw new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), "Field Type"));
                    }
                    TemplateField field = TemplateField.builder().template(updatedEntity)
                            .fieldName(fieldDetail.getFieldName()).fieldType(fieldTypeOpt.get())
                            .mandatory(Boolean.TRUE.equals(fieldDetail.getMandatory())).fieldSequence(fieldSequence++)
                            .isActive(true).createdBy(request.getUpdatedBy())
                            .isCreatorAdmin(
                                    request.getIsCreatedByAdmin() != null ? request.getIsCreatedByAdmin() : false)
                            .updatedBy(request.getUpdatedBy())
                            .isUpdaterAdmin(
                                    request.getIsUpdatedByAdmin() != null ? request.getIsUpdatedByAdmin() : false)
                            .build();

                    TemplateField savedField = templateDetailFieldRepository.save(field);
                    updatedEntity.getFields().add(savedField);
                    // Save field options
                    if (fieldDetail.getOptions() != null) {
                        int optionSequence = 0;
                        for (com.flickzz.desk.vo.TemplateFieldOptionVO option : fieldDetail.getOptions()) {
                            com.flickzz.desk.model.TemplateFieldOption fieldOption = com.flickzz.desk.model.TemplateFieldOption
                                    .builder().field(savedField).label(option.getLabel()).value(option.getValue())
                                    .defaultSelected(Boolean.TRUE.equals(option.getDefaultSelected()))
                                    .optionSequence(optionSequence++).isActive(true).createdBy(request.getUpdatedBy())
                                    .isCreatorAdmin(
                                            request.getIsCreatedByAdmin() != null ? request.getIsCreatedByAdmin()
                                                    : false)
                                    .isUpdaterAdmin(
                                            request.getIsUpdatedByAdmin() != null ? request.getIsUpdatedByAdmin()
                                                    : false)
                                    .updatedBy(request.getUpdatedBy()).build();

                            templateFieldOptionRepository.save(fieldOption);
                        }
                    }
                }
            }

            return mapper.toTemplateDetailsVO(updatedEntity);

        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in updateTemplateDetails method in TemplateDetailsService: {}", e.getMessage());
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<TemplateVO> listTemplateDetails(Long companyId) {
        log.info(generateLog("listTemplateDetails", this.getClass().getName()));
        try {
            if (companyId == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Company ID"));
            }

            // Check if company exists
            Optional<CompanyMaster> companyOpt = companyMasterRepository.findById(companyId);
            if (companyOpt.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Company"));
            }

            List<Template> templates = templateDetailsRepository.findByCompany_CompanyIdAndIsActive(companyId,
                    true);

            return templates.stream().map(mapper::toTemplateDetailsVO).collect(Collectors.toList());

        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in listTemplateDetails method in TemplateDetailsService: {}", e.getMessage());
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public TemplateVO getTemplateDetail(Long templateId) {
        log.info(generateLog("getTemplateDetail", this.getClass().getName()));
        try {
            if (templateId == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Template ID"));
            }

            Optional<Template> template = templateDetailsRepository.findByTemplateId(templateId);
            if (template.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Template"));
            }

            return mapper.toTemplateDetailsVO(template.get());

        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getTemplateDetail method in TemplateDetailsService: {}", e.getMessage());
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<TemplateFieldVO> listTemplateFieldsWithDefaultValue(Long companyId) {
        log.info(generateLog("listTemplateFieldsWithDefaultValue", this.getClass().getName()));
        try {
            if (companyId == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Company ID"));
            }

            if (companyMasterRepository.findById(companyId).isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Company"));
            }

            return templateDetailFieldRepository
                    .findByTemplateCompanyCompanyIdAndDefaultValueIsNotNullAndIsActiveOrderByTemplateTemplateIdAscFieldSequenceAsc(
                            companyId, true)
                    .stream().map(mapper::toTemplateDetailFieldVO).collect(Collectors.toList());
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in listTemplateFieldsWithDefaultValue method in TemplateDetailsService: {}",
                    e.getMessage());
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    @Transactional
    public void deleteTemplateFieldDefaultValue(Long fieldId, Long companyId, Long updatedBy) {
        log.info(generateLog("deleteTemplateFieldDefaultValue", this.getClass().getName()));
        try {
            if (fieldId == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Field ID"));
            }
            if (companyId == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Company ID"));
            }

            TemplateField field = templateDetailFieldRepository.findById(fieldId)
                    .filter(templateField -> templateField.getTemplate().getCompany().getCompanyId().equals(companyId))
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Template Field")));

            String oldValue = fieldSnapshot(field);
            field.setDefaultValue(null);
            field.setEditable(true);
            field.setUpdatedBy(updatedBy);
            TemplateField savedField = templateDetailFieldRepository.save(field);

            auditService.recordAudit(mapper.toSystemAuditRequest("Template", "Template Field", "TemplateField",
                    savedField.getFieldId(), "DELETE_DEFAULT_VALUE", fieldSnapshot(savedField), oldValue, null,
                    updatedBy, "SYSTEM", companyId, "SUCCESS", null));
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in deleteTemplateFieldDefaultValue method in TemplateDetailsService: {}",
                    e.getMessage());
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    @Transactional
    public void deleteTemplateDetails(Long templateId, Long companyId) {
        log.info(generateLog("deleteTemplateDetails", this.getClass().getName()));
        try {
            if (templateId == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Template ID"));
            }

            if (companyId == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Company ID"));
            }

            Optional<Template> template = templateDetailsRepository
                    .findByTemplateIdAndCompany_CompanyId(templateId, companyId);
            if (template.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Template"));
            }

            templateDetailsRepository.delete(template.get());
        } catch (DataIntegrityViolationException e) {
            log.error("DataIntegrityViolationException in deleteTemplateDetails method in TemplateDetailsService");
            throw new FlickzzDeskException(DB_SAVE_ERROR, "Cannot delete template details as it is associated with existing records.");
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in deleteTemplateDetails method in TemplateDetailsService: {}", e.getMessage());
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }
}
