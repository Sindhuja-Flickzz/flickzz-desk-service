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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private TemplateRepository templateRepository;

    @Autowired
    private TemplateFieldRepository templateFieldRepository;

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
                field = templateFieldRepository.findByFieldIdAndDefaultValueIsNull(request.getFieldId())
                        .orElseThrow(() -> new FlickzzDeskException(VERIFY_LIST_PAGE,
                                getDescription(VERIFY_LIST_PAGE.getDescription(), "Default Value for this template")));
            } else {
                field = templateFieldRepository.findById(request.getFieldId())
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

            TemplateField savedField = templateFieldRepository.save(field);
            templateFieldRepository.flush();
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
        Long userId = request != null ? request.getUpdatedBy() : null;
        if (userId == null && request != null) {
            userId = request.getCreatedBy();
        }
        if (userId == null && field != null) {
            userId = field.getUpdatedBy() != null ? field.getUpdatedBy() : field.getCreatedBy();
        }
        auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Template", "Template Field", "TemplateField",
                field != null ? field.getFieldId() : request != null ? request.getFieldId() : null, action, null, null,
                null, userId, "SYSTEM",
                request != null ? request.getCompanyId() : null, "FAILED", exception.getMessage()), exception);
    }

    @Transactional
    public TemplateFieldVO updateTemplateFieldDetails(TemplateDetailsRequestVO request) {
        return saveTemplateFieldDetails(request, "UPDATE");
    }

    @Transactional
    public TemplateVO createTemplateDetails(TemplateDetailsRequestVO request) {
        log.info(generateLog("createTemplateDetails", this.getClass().getName()));
        Template savedEntity = null;
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
            Optional<Template> existingTemplate = templateRepository
                    .findByTemplateNameAndWorkItem_ItemIdAndCompany_CompanyId(request.getTemplateName(), request.getWorkItemId(), request.getCompanyId());
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

            savedEntity = templateRepository.save(entity);

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
                            .showInInfoPage(Boolean.TRUE.equals(fieldDetail.getShowInInfoPage()))
                            .isActive(true).createdBy(request.getCreatedBy())
                            .isCreatorAdmin(
                                    request.getIsCreatedByAdmin() != null ? request.getIsCreatedByAdmin() : false)
                            .updatedBy(request.getCreatedBy())
                            .isUpdaterAdmin(
                                    request.getIsUpdatedByAdmin() != null ? request.getIsUpdatedByAdmin() : false)
                            .build();

                    TemplateField savedField = templateFieldRepository.save(field);

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

            templateRepository.flush();
            TemplateVO result = mapper.toTemplateDetailsVO(savedEntity);
            auditService.recordAudit(mapper.toSystemAuditRequest("Template", "Template", "Template",
                    savedEntity.getTemplateId(), "CREATE", templateSnapshot(savedEntity), null, null,
                    request.getCreatedBy() != null ? request.getCreatedBy() : savedEntity.getCreatedBy(),
                    "SYSTEM", request.getCompanyId(), "SUCCESS", null));
            return result;

        } catch (FlickzzDeskException e) {
            recordTemplateExceptionAudit(request, savedEntity, "CREATE", e);
            throw e;
        } catch (Exception e) {
            log.error("Exception in createTemplateDetails method in TemplateDetailsService: {}", e.getMessage());
            recordTemplateExceptionAudit(request, savedEntity, "CREATE", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    private String templateSnapshot(Template template) throws Exception {
        return objectMapper.writeValueAsString(mapper.toTemplateDetailsVO(template));
    }

    private void recordTemplateExceptionAudit(TemplateDetailsRequestVO request, Template template,
                                              String action, Exception exception) {
        Long userId = null;
        if (request != null) {
            userId = request.getUpdatedBy() != null ? request.getUpdatedBy() : request.getCreatedBy();
        }
        if (userId == null && template != null) {
            userId = template.getCreatedBy();
        }
        recordTemplateExceptionAudit(request != null ? request.getTemplateId() : null,
                request != null ? request.getCompanyId() : null, userId, template, action, exception);
    }

    private void recordTemplateExceptionAudit(Long templateId, Long companyId, Long userId, Template template,
                                              String action, Exception exception) {
        if (userId == null && template != null) {
            userId = template.getUpdatedBy() != null ? template.getUpdatedBy() : template.getCreatedBy();
        }
        auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Template", "Template", "Template",
                template != null ? template.getTemplateId() : templateId, action, null, null, null, userId,
                "SYSTEM", companyId, "FAILED", exception.getMessage()), exception);
    }

    @Transactional
    public TemplateVO updateTemplateDetails(TemplateDetailsRequestVO request) {
        log.info(generateLog("updateTemplateDetails", this.getClass().getName()));
        Template entity = null;
        String oldValue = null;
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
            Optional<Template> existingTemplate = templateRepository
                    .findByTemplateIdAndCompany_CompanyId(request.getTemplateId(), request.getCompanyId());
            if (existingTemplate.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Template"));
            }

            entity = existingTemplate.get();
            oldValue = templateSnapshot(entity);

            // Update fields
            if (request.getTemplateName() != null) {
                entity.setTemplateName(request.getTemplateName());
            }
            if (request.getWorkItemId() != null) {
                WorkItem workItem = workItemRepository.findById(request.getWorkItemId())
                        .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), "Work Item")));
                entity.setWorkItem(workItem);
            }
            entity.setUpdatedBy(request.getUpdatedBy());
            entity.setIsUpdaterAdmin(request.getIsUpdatedByAdmin() != null ? request.getIsUpdatedByAdmin() : false);

            Template updatedEntity = templateRepository.save(entity);

            // Update template fields and options if provided
            if (request.getTemplateDetails() != null && !request.getTemplateDetails().isEmpty()) {
                List<TemplateField> activeFields = updatedEntity.getFields() == null ? List.of()
                        : updatedEntity.getFields().stream()
                        .filter(field -> Boolean.TRUE.equals(field.getIsActive()))
                        .sorted(java.util.Comparator.comparing(TemplateField::getFieldSequence))
                        .toList();
                if (request.getTemplateDetails().size() > activeFields.size()) {
                    throw new FlickzzDeskException(INVALID_FIELD,
                            getDescription(INVALID_FIELD.getDescription(),
                                    "Template Details cannot contain more fields than the existing template"));
                }

                Set<Long> updatedFieldIds = new HashSet<>();
                int fieldSequence = 0;
                for (int fieldIndex = 0; fieldIndex < request.getTemplateDetails().size(); fieldIndex++) {
                    TemplateFieldDetailVO fieldDetail = request.getTemplateDetails().get(fieldIndex);
                    if (fieldDetail == null) {
                        throw new FlickzzDeskException(INVALID_FIELD,
                                getDescription(INVALID_FIELD.getDescription(), "Template Field Details"));
                    }
                    TemplateField field = fieldDetail.getFieldId() == null ? activeFields.get(fieldIndex)
                            : activeFields.stream()
                            .filter(existingField -> fieldDetail.getFieldId().equals(existingField.getFieldId()))
                            .findFirst().orElse(null);
                    if (field == null || (fieldDetail.getFieldId() != null
                            && !updatedFieldIds.add(fieldDetail.getFieldId()))) {
                        throw new FlickzzDeskException(DOES_NOT_EXIST,
                                getDescription(DOES_NOT_EXIST.getDescription(), "Template Field"));
                    }

                    if (fieldDetail.getFieldName() != null) {
                        field.setFieldName(fieldDetail.getFieldName());
                    }
                    if (fieldDetail.getFieldTypeId() != null) {
                        FieldType fieldType = fieldTypeRepository
                                .findByTypeIdAndIsActive(fieldDetail.getFieldTypeId(), ACTIVE)
                                .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                                        getDescription(DOES_NOT_EXIST.getDescription(), "Field Type")));
                        field.setFieldType(fieldType);
                    }
                    if (fieldDetail.getMandatory() != null) {
                        field.setMandatory(fieldDetail.getMandatory());
                    }
                    if (fieldDetail.getShowInInfoPage() != null) {
                        field.setShowInInfoPage(fieldDetail.getShowInInfoPage());
                    }
                    field.setFieldSequence(fieldSequence++);
                    field.setUpdatedBy(request.getUpdatedBy());
                    field.setIsUpdaterAdmin(
                            request.getIsUpdatedByAdmin() != null ? request.getIsUpdatedByAdmin() : false);

                    if (fieldDetail.getOptions() != null) {
                        List<com.flickzz.desk.model.TemplateFieldOption> activeOptions = field.getOptions() == null
                                ? List.of()
                                : field.getOptions().stream()
                                .filter(option -> Boolean.TRUE.equals(option.getIsActive()))
                                .sorted(java.util.Comparator.comparing(
                                        com.flickzz.desk.model.TemplateFieldOption::getOptionSequence))
                                .toList();
                        if (fieldDetail.getOptions().size() > activeOptions.size()) {
                            throw new FlickzzDeskException(INVALID_FIELD,
                                    getDescription(INVALID_FIELD.getDescription(),
                                            "Template Field Options cannot contain more options than the existing field"));
                        }

                        Set<Long> updatedOptionIds = new HashSet<>();
                        int optionSequence = 0;
                        for (int optionIndex = 0; optionIndex < fieldDetail.getOptions().size(); optionIndex++) {
                            com.flickzz.desk.vo.TemplateFieldOptionVO optionDetail =
                                    fieldDetail.getOptions().get(optionIndex);
                            if (optionDetail == null) {
                                throw new FlickzzDeskException(INVALID_FIELD,
                                        getDescription(INVALID_FIELD.getDescription(), "Template Field Options"));
                            }
                            com.flickzz.desk.model.TemplateFieldOption option =
                                    optionDetail.getOptionId() == null ? activeOptions.get(optionIndex)
                                            : activeOptions.stream()
                                            .filter(existingOption -> optionDetail.getOptionId()
                                                    .equals(existingOption.getOptionId()))
                                            .findFirst().orElse(null);
                            if (option == null || (optionDetail.getOptionId() != null
                                    && !updatedOptionIds.add(optionDetail.getOptionId()))) {
                                throw new FlickzzDeskException(DOES_NOT_EXIST,
                                        getDescription(DOES_NOT_EXIST.getDescription(), "Template Field Option"));
                            }

                            if (optionDetail.getLabel() != null) {
                                option.setLabel(optionDetail.getLabel());
                            }
                            if (optionDetail.getValue() != null) {
                                option.setValue(optionDetail.getValue());
                            }
                            if (optionDetail.getDefaultSelected() != null) {
                                option.setDefaultSelected(optionDetail.getDefaultSelected());
                            }
                            option.setOptionSequence(optionSequence++);
                            option.setUpdatedBy(request.getUpdatedBy());
                            option.setIsUpdaterAdmin(
                                    request.getIsUpdatedByAdmin() != null ? request.getIsUpdatedByAdmin() : false);
                        }
                    }
                }

                templateFieldRepository.saveAll(updatedEntity.getFields());
                templateFieldRepository.flush();
            }

            TemplateVO result = mapper.toTemplateDetailsVO(updatedEntity);
            auditService.recordAudit(mapper.toSystemAuditRequest("Template", "Template", "Template",
                    updatedEntity.getTemplateId(), "UPDATE", templateSnapshot(updatedEntity), oldValue, null,
                    request.getUpdatedBy() != null ? request.getUpdatedBy() : updatedEntity.getCreatedBy(),
                    "SYSTEM", request.getCompanyId(), "SUCCESS", null));
            return result;

        } catch (FlickzzDeskException e) {
            recordTemplateExceptionAudit(request, entity, "UPDATE", e);
            throw e;
        } catch (Exception e) {
            log.error("Exception in updateTemplateDetails method in TemplateDetailsService: {}", e.getMessage());
            recordTemplateExceptionAudit(request, entity, "UPDATE", e);
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

            List<Template> templates = templateRepository.findByCompany_CompanyIdAndIsActive(companyId,
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

            Optional<Template> template = templateRepository.findByTemplateId(templateId);
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

            return templateFieldRepository
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
        TemplateField field = null;
        try {
            if (fieldId == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Field ID"));
            }
            if (companyId == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Company ID"));
            }

            field = templateFieldRepository.findById(fieldId)
                    .filter(templateField -> templateField.getTemplate().getCompany().getCompanyId().equals(companyId))
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Template Field")));

            String oldValue = fieldSnapshot(field);
            field.setDefaultValue(null);
            field.setEditable(true);
            field.setUpdatedBy(updatedBy);
            TemplateField savedField = templateFieldRepository.save(field);
            templateFieldRepository.flush();

            auditService.recordAudit(mapper.toSystemAuditRequest("Template", "Template Field", "TemplateField",
                    savedField.getFieldId(), "DELETE_DEFAULT_VALUE", fieldSnapshot(savedField), oldValue, null,
                    updatedBy != null ? updatedBy : savedField.getCreatedBy(), "SYSTEM", companyId, "SUCCESS", null));
        } catch (FlickzzDeskException e) {
            recordTemplateFieldExceptionAudit(
                    TemplateDetailsRequestVO.builder().fieldId(fieldId).companyId(companyId).updatedBy(updatedBy).build(),
                    field, "DELETE_DEFAULT_VALUE", e);
            throw e;
        } catch (Exception e) {
            log.error("Exception in deleteTemplateFieldDefaultValue method in TemplateDetailsService: {}",
                    e.getMessage());
            recordTemplateFieldExceptionAudit(
                    TemplateDetailsRequestVO.builder().fieldId(fieldId).companyId(companyId).updatedBy(updatedBy).build(),
                    field, "DELETE_DEFAULT_VALUE", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    @Transactional
    public void deleteTemplateDetails(Long templateId, Long companyId) {
        log.info(generateLog("deleteTemplateDetails", this.getClass().getName()));
        Template entity = null;
        try {
            if (templateId == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Template ID"));
            }

            if (companyId == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Company ID"));
            }

            Optional<Template> template = templateRepository
                    .findByTemplateIdAndCompany_CompanyId(templateId, companyId);
            if (template.isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Template"));
            }

            entity = template.get();
            String oldValue = templateSnapshot(entity);
            templateRepository.delete(entity);
            templateRepository.flush();
            auditService.recordAudit(mapper.toSystemAuditRequest("Template", "Template", "Template",
                    entity.getTemplateId(), "DELETE", null, oldValue, null,
                    entity.getUpdatedBy() != null ? entity.getUpdatedBy() : entity.getCreatedBy(), "SYSTEM",
                    companyId, "SUCCESS", null));
        } catch (DataIntegrityViolationException e) {
            log.error("DataIntegrityViolationException in deleteTemplateDetails method in TemplateDetailsService");
            recordTemplateExceptionAudit(templateId, companyId, entity != null ? entity.getUpdatedBy() : null,
                    entity, "DELETE", e);
            throw new FlickzzDeskException(DB_SAVE_ERROR, "Cannot delete template details as it is associated with existing records.");
        } catch (FlickzzDeskException e) {
            recordTemplateExceptionAudit(templateId, companyId, entity != null ? entity.getUpdatedBy() : null,
                    entity, "DELETE", e);
            throw e;
        } catch (Exception e) {
            log.error("Exception in deleteTemplateDetails method in TemplateDetailsService: {}", e.getMessage());
            recordTemplateExceptionAudit(templateId, companyId, entity != null ? entity.getUpdatedBy() : null,
                    entity, "DELETE", e);
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<TemplateVO> listRITMDefaultTemplates(String item, Long orgId) {
        log.info(generateLog("listRITMDefaultTemplates", this.getClass().getName()));
        try {
            if (orgId == null) {
                throw new FlickzzDeskException(INVALID_FIELD,
                        getDescription(INVALID_FIELD.getDescription(), "Organization ID"));
            }

            if (companyMasterRepository.findById(orgId).isEmpty()) {
                throw new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Organization"));
            }

            WorkItem workItem = workItemRepository.findByCodeAndIsActiveTrue(item.toUpperCase())
                    .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                            getDescription(DOES_NOT_EXIST.getDescription(), "Work Item")));

            return templateRepository.findByCompany_CompanyIdAndWorkItem_ItemIdAndIsActiveTrue(orgId, workItem.getItemId()).stream()
                    .map(mapper::toTemplateDetailsVO).collect(Collectors.toList());
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in listRITMDefaultTemplates method in TemplateDetailsService: {}",
                    e.getMessage());
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }
}
