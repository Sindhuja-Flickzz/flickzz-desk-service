package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.*;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

import java.util.*;
import java.util.stream.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import com.flickzz.desk.exception.*;
import com.flickzz.desk.mapper.*;
import com.flickzz.desk.model.*;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.vo.*;

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

	@Transactional
	public TemplateDetailsVO createTemplateDetails(TemplateDetailsRequestVO request) {
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
			Optional<TemplateDetails> existingTemplate = templateDetailsRepository
					.findByTemplateNameAndCompany_CompanyId(request.getTemplateName(), request.getCompanyId());
			if (existingTemplate.isPresent()) {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						getDescription(ALREADY_EXISTS.getDescription(), "Template Name"));
			}

			// Create template entity
			TemplateDetails entity = TemplateDetails.builder().templateName(request.getTemplateName())
					.workItem(workItemOpt.get()).company(companyOpt.get()).isActive(true)
					.createdBy(request.getCreatedBy()).updatedBy(request.getCreatedBy()).build();

			TemplateDetails savedEntity = templateDetailsRepository.save(entity);

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

					TemplateDetailField field = TemplateDetailField.builder().template(savedEntity)
							.fieldName(fieldDetail.getFieldName()).fieldType(fieldTypeOpt.get())
							.mandatory(Boolean.TRUE.equals(fieldDetail.getMandatory())).fieldSequence(fieldSequence++)
							.isActive(true).createdBy(request.getCreatedBy()).updatedBy(request.getCreatedBy()).build();

					TemplateDetailField savedField = templateDetailFieldRepository.save(field);

					// Save field options
					if (fieldDetail.getOptions() != null) {
						int optionSequence = 0;
						for (com.flickzz.desk.vo.TemplateFieldOptionVO option : fieldDetail.getOptions()) {
							com.flickzz.desk.model.TemplateFieldOption fieldOption = com.flickzz.desk.model.TemplateFieldOption
									.builder().field(savedField).label(option.getLabel()).value(option.getValue())
									.defaultSelected(Boolean.TRUE.equals(option.getDefaultSelected()))
									.optionSequence(optionSequence++).isActive(true).createdBy(request.getCreatedBy())
									.updatedBy(request.getCreatedBy()).build();

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
	public TemplateDetailsVO updateTemplateDetails(TemplateDetailsRequestVO request) {
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
			Optional<TemplateDetails> existingTemplate = templateDetailsRepository
					.findByTemplateIdAndCompany_CompanyId(request.getTemplateId(), request.getCompanyId());
			if (existingTemplate.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), "Template"));
			}

			TemplateDetails entity = existingTemplate.get();

			// Update fields
			if (request.getTemplateName() != null) {
				entity.setTemplateName(request.getTemplateName());
			}
			if (request.getUpdatedBy() != null) {
				entity.setUpdatedBy(request.getUpdatedBy());
			}

			TemplateDetails updatedEntity = templateDetailsRepository.save(entity);

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
					TemplateDetailField field = TemplateDetailField.builder().template(updatedEntity)
							.fieldName(fieldDetail.getFieldName()).fieldType(fieldTypeOpt.get())
							.mandatory(Boolean.TRUE.equals(fieldDetail.getMandatory())).fieldSequence(fieldSequence++)
							.isActive(true).createdBy(request.getUpdatedBy()).updatedBy(request.getUpdatedBy()).build();

					TemplateDetailField savedField = templateDetailFieldRepository.save(field);
					updatedEntity.getFields().add(savedField);
					// Save field options
					if (fieldDetail.getOptions() != null) {
						int optionSequence = 0;
						for (com.flickzz.desk.vo.TemplateFieldOptionVO option : fieldDetail.getOptions()) {
							com.flickzz.desk.model.TemplateFieldOption fieldOption = com.flickzz.desk.model.TemplateFieldOption
									.builder().field(savedField).label(option.getLabel()).value(option.getValue())
									.defaultSelected(Boolean.TRUE.equals(option.getDefaultSelected()))
									.optionSequence(optionSequence++).isActive(true).createdBy(request.getUpdatedBy())
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

	public List<TemplateDetailsVO> listTemplateDetails(Long companyId) {
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

			List<TemplateDetails> templates = templateDetailsRepository.findByCompany_CompanyIdAndIsActive(companyId,
					true);

			return templates.stream().map(mapper::toTemplateDetailsVO).collect(Collectors.toList());

		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in listTemplateDetails method in TemplateDetailsService: {}", e.getMessage());
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public TemplateDetailsVO getTemplateDetail(Long templateId) {
		log.info(generateLog("getTemplateDetail", this.getClass().getName()));
		try {
			if (templateId == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), "Template ID"));
			}

			Optional<TemplateDetails> template = templateDetailsRepository.findByTemplateId(templateId);
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

			Optional<TemplateDetails> template = templateDetailsRepository
					.findByTemplateIdAndCompany_CompanyId(templateId, companyId);
			if (template.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), "Template"));
			}

			templateDetailsRepository.delete(template.get());

		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in deleteTemplateDetails method in TemplateDetailsService: {}", e.getMessage());
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}
}
