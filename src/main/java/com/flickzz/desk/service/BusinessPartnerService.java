package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.*;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

import java.util.*;
import java.util.stream.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.flickzz.desk.exception.*;
import com.flickzz.desk.mapper.*;
import com.flickzz.desk.model.*;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.vo.*;

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
	private CommonMapper mapper;

	public BusinessPartnerVO createBusinessPartner(CompanyMasterRequestVO request) {
		log.info(generateLog("createBusinessPartner", this.getClass().getName()));
		try {
			if (request == null || request.getCompanyId() == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), COMPANY));
			}

			if (request == null || request.getBpUid() == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), "Business Partner"));
			}

			if (request.getCompanyId().equals(request.getBpUid())) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), "Business Partner"));
			}

			CompanyMaster bpCompany = companyMasterRepository.findByUidAndIsActive(request.getBpUid(), ACTIVE)
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), COMPANY)));

			if (businessPartnerRepository.existsBusinessPartnerMapping(request.getCompanyId(),
					bpCompany.getCompanyId()) > 0) {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						getDescription(ALREADY_EXISTS.getDescription(), "Business Partner mapping"));
			}

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
			businessPartnerRepository.save(entity);
			return mapper.toBusinessPartnerVO(entity);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createBusinessPartner method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BPConfigurationVO getBusinessPartnerConfigurationList(String businessPartnerId) {
		log.info(generateLog("getBusinessPartnerConfigurationList", this.getClass().getName()));
		try {
			Optional<BPConfiguration> configurations = bPConfigurationRepository
					.findByBusinessPartnerBusinessPartnerIdAndIsActive(Long.valueOf(businessPartnerId), ACTIVE);
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
					.findByBusinessPartnerBusinessPartnerIdAndIsActive(request.getBusinessPartnerId(), ACTIVE);

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
			bPPriorityRepository.save(bpPriority);
			return mapper.toBPPriorityVo(bpPriority);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createBusinessPartnerPriorityConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BPPriorityVO updateBusinessPartnerPriorityConfiguration(BpConfigRequestVO request) {
		log.info(generateLog("updateBusinessPartnerPriorityConfiguration", this.getClass().getName()));
		try {
			if (request == null || request.getPriorityId() == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), PRIORITY));
			}

			Optional<BPPriority> existingPriority = bPPriorityRepository
					.findByPriorityIdAndIsActive(request.getPriorityId(), ACTIVE);

			if (existingPriority.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), PRIORITY));
			}

			BPPriority bpPriority = existingPriority.get();
			bpPriority.setCode(request.getCode());
			bpPriority.setDescription(request.getDescription());
			bpPriority.setUpdatedBy(request.getUpdatedBy());
			bPPriorityRepository.save(bpPriority);
			return mapper.toBPPriorityVo(bpPriority);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in updateBusinessPartnerPriorityConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deleteBusinessPartnerPriorityConfiguration(String priorityId) {

		log.info(generateLog("deleteBusinessPartnerPriorityConfiguration", this.getClass().getName()));
		try {
			Optional<BPPriority> existingPriority = bPPriorityRepository
					.findByPriorityIdAndIsActive(Long.valueOf(priorityId), ACTIVE);

			if (existingPriority.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), PRIORITY));
			}

			BPPriority bpPriority = existingPriority.get();
			bpPriority.setIsActive(Boolean.FALSE);
			bPPriorityRepository.save(bpPriority);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in deleteBusinessPartnerPriorityConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BPPriorityVO getBusinessPartnerPriorityConfigurationById(Long valueOf) {

		log.info(generateLog("getBusinessPartnerPriorityConfigurationById", this.getClass().getName()));
		try {
			Optional<BPPriority> existingPriority = bPPriorityRepository.findByPriorityIdAndIsActive(valueOf, ACTIVE);

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

	public List<BPPriorityVO> getBusinessPartnerPriorityConfiguration(Long businessPartnerId) {
		log.info(generateLog("getBusinessPartnerPriorityConfiguration", this.getClass().getName()));
		try {

			Optional<BusinessPartner> businessPartner = businessPartnerRepository
					.findByBusinessPartnerIdAndIsActive(businessPartnerId, ACTIVE);
			if (!businessPartner.isPresent()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
			}

			Optional<BPConfiguration> existingConfig = bPConfigurationRepository
					.findByBusinessPartnerBusinessPartnerIdAndIsActive(businessPartnerId, ACTIVE);

			if (!existingConfig.isPresent()) {
				return Collections.emptyList();
			}

			List<BPPriority> priorities = bPPriorityRepository
					.findByConfigurationConfigurationIdAndIsActive(existingConfig.get().getConfigurationId(), ACTIVE);
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
					.findByBusinessPartnerBusinessPartnerIdAndIsActive(request.getBusinessPartnerId(), ACTIVE);

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
			bpSlaRepository.save(bpSla);
			return mapper.toBPSlaVo(bpSla);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createBusinessPartnerSLAConfiguration method in BusinessPartnerService");
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
					.findByBusinessPartnerBusinessPartnerIdAndIsActive(valueOf, ACTIVE);

			if (!existingConfig.isPresent()) {
				return Collections.emptyList();
			}

			List<BPSla> slas = bpSlaRepository
					.findByConfigurationConfigurationIdAndIsActive(existingConfig.get().getConfigurationId(), ACTIVE);
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
		try {
			if (request == null || request.getSlaId() == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), SLA));
			}

			Optional<BPSla> existingSla = bpSlaRepository.findBySlaIdAndIsActive(request.getSlaId(), ACTIVE);

			if (existingSla.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), SLA));
			}

			BPSla bpSla = existingSla.get();
			bpSla.setFirstResponseTime(request.getFirstResponseTime());
			bpSla.setFirstResponseTerm(request.getFirstResponseTerm());
			bpSla.setResolutionTime(request.getResolutionTime());
			bpSla.setResolutionTerm(request.getResolutionTerm());
			bpSla.setUpdateFrequency(request.getUpdateFrequency());
			bpSla.setUpdateFrequencyTerm(request.getUpdateFrequencyTerm());
			bpSla.setUpdatedBy(request.getUpdatedBy());
			bpSlaRepository.save(bpSla);
			return mapper.toBPSlaVo(bpSla);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in updateBusinessPartnerSLAConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deleteBusinessPartnerSLAConfiguration(String slaId) {

		log.info(generateLog("deleteBusinessPartnerSLAConfiguration", this.getClass().getName()));
		try {
			Optional<BPSla> existingSla = bpSlaRepository.findBySlaIdAndIsActive(Long.valueOf(slaId), ACTIVE);

			if (existingSla.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), SLA));
			}

			BPSla bpSla = existingSla.get();
			bpSla.setIsActive(Boolean.FALSE);
			bpSlaRepository.save(bpSla);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in deleteBusinessPartnerSLAConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BPSlaVO getBusinessPartnerSLAConfigurationById(Long valueOf) {

		log.info(generateLog("getBusinessPartnerSLAConfigurationById", this.getClass().getName()));
		try {
			Optional<BPSla> existingSla = bpSlaRepository.findBySlaIdAndIsActive(valueOf, ACTIVE);

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
					.findByBusinessPartnerBusinessPartnerIdAndIsActive(request.getBusinessPartnerId(), ACTIVE);

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
			bpCategoryRepository.save(bpCategory);

			if (request.getSubCategories() != null && !request.getSubCategories().isEmpty()) {
				for (String subCategoryName : request.getSubCategories()) {
					if (subCategoryName == null || subCategoryName.trim().isEmpty()) {
						continue;
					}
					if (bpSubCategoryRepository.existsByCategoryCategoryIdAndSubCategoryNameAndIsActive(
							bpCategory.getCategoryId(), subCategoryName.trim(), ACTIVE)) {
						continue;
					}
					BPSubCategory subCategory = BPSubCategory.builder().category(bpCategory)
							.subCategoryName(subCategoryName.trim()).createdBy(request.getCreatedBy())
							.updatedBy(request.getUpdatedBy()).build();
					bpSubCategoryRepository.save(subCategory);
				}
			}
			return mapper.toBPCategoryVo(bpCategory);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createBusinessPartnerCategoryConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BPCategoryVO updateBusinessPartnerCategoryConfiguration(BpConfigRequestVO request) {
		log.info(generateLog("updateBusinessPartnerCategoryConfiguration", this.getClass().getName()));
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
			}

			BPCategory bpCategory = existingCategory.get();
			bpCategory.setCategoryName(request.getCategoryName());
			bpCategory.setUpdatedBy(request.getUpdatedBy());
			bpCategoryRepository.save(bpCategory);

			if (request.getSubCategories() != null) {
				Set<String> incomingSubCategories = new LinkedHashSet<>();
				for (String subCategoryName : request.getSubCategories()) {
					if (subCategoryName == null || subCategoryName.trim().isEmpty()) {
						continue;
					}
					incomingSubCategories.add(subCategoryName.trim());
				}

				List<BPSubCategory> existingSubCategories = bpSubCategoryRepository
						.findByCategoryCategoryIdAndIsActive(bpCategory.getCategoryId(), ACTIVE);
				Set<String> existingNames = existingSubCategories.stream().map(BPSubCategory::getSubCategoryName)
						.collect(Collectors.toSet());

				for (String subCategoryName : incomingSubCategories) {
					if (existingNames.contains(subCategoryName)) {
						continue;
					}
					BPSubCategory subCategory = BPSubCategory.builder().category(bpCategory)
							.subCategoryName(subCategoryName).createdBy(request.getCreatedBy())
							.updatedBy(request.getUpdatedBy()).build();
					bpSubCategoryRepository.save(subCategory);
				}

				for (BPSubCategory subCategory : existingSubCategories) {
					if (!incomingSubCategories.contains(subCategory.getSubCategoryName())) {
						subCategory.setIsActive(Boolean.FALSE);
						subCategory.setUpdatedBy(request.getUpdatedBy());
						bpSubCategoryRepository.save(subCategory);
					}
				}
			}
			return mapper.toBPCategoryVo(bpCategory);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in updateBusinessPartnerCategoryConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deleteBusinessPartnerCategoryConfiguration(String categoryId) {
		log.info(generateLog("deleteBusinessPartnerCategoryConfiguration", this.getClass().getName()));
		try {
			Optional<BPCategory> existingCategory = bpCategoryRepository
					.findByCategoryIdAndIsActive(Long.valueOf(categoryId), ACTIVE);
			if (existingCategory.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), CATEGORY));
			}

			BPCategory bpCategory = existingCategory.get();
			bpCategory.setIsActive(Boolean.FALSE);
			bpCategoryRepository.save(bpCategory);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in deleteBusinessPartnerCategoryConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BPCategoryVO getBusinessPartnerCategoryConfigurationById(Long categoryId) {
		log.info(generateLog("getBusinessPartnerCategoryConfigurationById", this.getClass().getName()));
		try {
			Optional<BPCategory> existingCategory = bpCategoryRepository.findByCategoryIdAndIsActive(categoryId,
					ACTIVE);
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

	public List<BPCategoryVO> getBusinessPartnerCategoryConfiguration(Long businessPartnerId) {
		log.info(generateLog("getBusinessPartnerCategoryConfiguration", this.getClass().getName()));
		try {
			Optional<BusinessPartner> businessPartner = businessPartnerRepository
					.findByBusinessPartnerIdAndIsActive(businessPartnerId, ACTIVE);
			if (!businessPartner.isPresent()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), BUSINESS_PARTNER));
			}

			Optional<BPConfiguration> existingConfig = bPConfigurationRepository
					.findByBusinessPartnerBusinessPartnerIdAndIsActive(businessPartnerId, ACTIVE);
			if (!existingConfig.isPresent()) {
				return Collections.emptyList();
			}

			List<BPCategory> categories = bpCategoryRepository
					.findByConfigurationConfigurationIdAndIsActive(existingConfig.get().getConfigurationId(), ACTIVE);
			return categories.stream().map(mapper::toBPCategoryVo).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getBusinessPartnerCategoryConfiguration method in BusinessPartnerService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

}
