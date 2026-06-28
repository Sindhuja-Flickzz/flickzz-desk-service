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
public class CompanyService {

	private static final Logger log = LoggerFactory.getLogger(CompanyService.class);

	@Autowired
	private CountryMasterRepository countryMasterRepository;

	@Autowired
	CompanyMasterRepository companyMasterRepository;

	@Autowired
	StateMasterRepository stateMasterRepository;

	@Autowired
	CityMasterRepository cityMasterRepository;

	@Autowired
	CompanyRoleRepository companyRoleRepository;

	@Autowired
	private CommonMapper mapper;

	public CompanyMasterVO createCompany(CompanyMasterRequestVO request) {
		log.info(generateLog("createCompany", this.getClass().getName()));
		try {
			if (request == null || request.getCompanyName() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), COMPANY_NAME));
			}

			if (request == null || request.getRegisteredNumber() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), REGISTERED_NUMBER));
			}

			if (request == null || request.getCountryId() == null) {
				throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), CURRENCY));
			}

			Optional<CountryMaster> country = countryMasterRepository.findById(request.getCountryId());
			if (country.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), COUNTRY));
			}

			Optional<StateMaster> state = stateMasterRepository.findById(request.getStateId());
			if (state.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), STATE));
			}

			Optional<CityMaster> city = cityMasterRepository.findById(request.getCityId());
			if (city.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), CITY));
			}

			companyMasterRepository.findByCompanyNameAndIsActive(request.getCompanyName(), ACTIVE).ifPresent(c -> {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						getDescription(ALREADY_EXISTS.getDescription(), COMPANY_NAME));
			});

			CompanyMaster entity = mapper.toCompanyMasterEntity(request, country.get(), state.get(), city.get());
			return mapper.toCompanyMasterVO(companyMasterRepository.save(entity));
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createCompany method in CompanyService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public CompanyMasterVO getCompanyInfo(String companyId) {
		log.info(generateLog("getCompanyInfo", this.getClass().getName()));
		try {
			CompanyMaster entity = companyMasterRepository.findById(Long.valueOf(companyId))
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), COMPANY)));
			return mapper.toCompanyMasterVO(entity);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getCompanyInfo method in CompanyService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public CompanyMasterVO updateCompany(CompanyMasterRequestVO request) {
		log.info(generateLog("updateCompany", this.getClass().getName()));
		try {
			if (request == null || request.getCompanyId() == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), COMPANY));
			}

			Optional<CountryMaster> country = countryMasterRepository.findById(request.getCountryId());
			if (country.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), CURRENCY));
			}

			Optional<StateMaster> state = stateMasterRepository.findById(request.getStateId());
			if (state.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), STATE));
			}

			Optional<CityMaster> city = cityMasterRepository.findById(request.getCityId());
			if (city.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), CITY));
			}

			CompanyMaster existing = companyMasterRepository.findByCompanyId(request.getCompanyId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), COMPANY)));

			existing.setIsActive(ACTIVE);
			existing.setCompanyName(request.getCompanyName());
			existing.setCountry(country.get());
			existing.setState(state.get());
			existing.setCity(city.get());
			existing.setPinCode(request.getPinCode());
			existing.setAddressLine1(request.getAddressLine1());
			existing.setAddressLine2(request.getAddressLine2());
			existing.setEmployeeSize(request.getEmployeeSize());
			existing.setUpdatedBy(request.getUpdatedBy());
			return mapper.toCompanyMasterVO(companyMasterRepository.save(existing));
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in updateCompany method in CompanyService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deleteCompany(String companyId) {
		log.info(generateLog("deleteCompany", this.getClass().getName()));
		try {
			CompanyMaster existing = companyMasterRepository
					.findById(companyId != null ? Long.valueOf(companyId) : null)
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), COMPANY)));
			existing.setIsActive(!ACTIVE);
			companyMasterRepository.save(existing);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in deleteCompany method in CompanyService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<CompanyMasterVO> listCompanies() {
		log.info(generateLog("listCompanies", this.getClass().getName()));
		try {
			return companyMasterRepository.findByIsActive(ACTIVE).stream().map(mapper::toCompanyMasterVO).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in listCompanies method in CompanyService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<BusinessPartnerVO> listServiceProviderList(String orgId) {
		log.info(generateLog("listServiceProviderList", this.getClass().getName()));
		try {
			return companyRoleRepository.findByCompany_CompanyIdAndIsActive(Long.valueOf(orgId), ACTIVE).stream()
					.filter(role -> role.getIsBoth()).map(role -> mapper.toCompanyRoleVO(role))
					.collect(Collectors.toList());
		} catch (Exception e) {
			log.error("Exception in listServiceProviderList method in CompanyService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public CompanyMasterVO getCompanyInfoByUid(String uid) {
		log.info(generateLog("getCompanyInfoByUid", this.getClass().getName()));
		try {
			CompanyMaster entity = companyMasterRepository.findByUidAndIsActive(uid, ACTIVE)
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), COMPANY)));
			return mapper.toCompanyMasterVO(entity);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getCompanyInfoByUid method in CompanyService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BusinessPartnerVO createCompanyBusinessPartner(CompanyMasterRequestVO request) {
		log.info(generateLog("createCompanyBusinessPartner", this.getClass().getName()));
		try {
			if (request == null || request.getCompanyId() == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), COMPANY));
			}

			CompanyMaster bpCompany = companyMasterRepository.findByUidAndIsActive(request.getBpUid(), ACTIVE)
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), COMPANY)));
			Optional<BusinessPartner> businessPartner = companyRoleRepository
					.findByCompany_CompanyIdAndMappedCompany_CompanyIdAndIsActive(request.getCompanyId(),
							bpCompany.getCompanyId(), ACTIVE);
			businessPartner.ifPresent(role -> {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						getDescription(ALREADY_EXISTS.getDescription(), "Mapping "));
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
			companyRoleRepository.save(entity);
			return mapper.toCompanyRoleVO(entity);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createCompanyBusinessPartner method in CompanyService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}
}
