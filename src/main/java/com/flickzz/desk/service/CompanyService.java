package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.flickzz.desk.vo.request.CompanyMasterRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
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
	BusinessPartnerRepository businessPartnerRepository;

	@Autowired
	CompanyApproverRepository companyApproverRepository;

	@Autowired
	AgentMasterRepository agentMasterRepository;

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

			companyMasterRepository.findTopByCompanyNameAndIsActiveOrderByVersionDesc(request.getCompanyName(), ACTIVE).ifPresent(c -> {
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

			if (request.getApproverIds() == null || request.getApproverIds().isEmpty()) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), APPROVERS));
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

			// Validate and load approvers
			List<CompanyApprover> approvers = new java.util.ArrayList<>();
			Integer level = 1;
			for (Long approverId : request.getApproverIds()) {
				AgentMaster agent = agentMasterRepository.findById(approverId)
						.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
								getDescription(DOES_NOT_EXIST.getDescription(), "Approver")));

				// Validate that agent belongs to the same organization
				if (!agent.getOrganization().getCompanyId().equals(existing.getCompanyId())) {
					throw new FlickzzDeskException(INVALID_FIELD,
							getDescription(INVALID_FIELD.getDescription(), "Approver belongs to different organization"));
				}

				CompanyApprover approver = mapper.toCompanyApproverEntity(agent, existing, level,
						request.getUpdatedBy(), request.getIsUpdatedByAdmin());
				approvers.add(approver);
				level++;
			}

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
			existing.getApprovers().clear();
			existing.getApprovers().addAll(approvers);
			
			companyMasterRepository.save(existing);

			return mapper.toCompanyMasterVO(existing);
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

	public List<BusinessPartnerVO> listServiceProviderList(Long orgId) {
		log.info(generateLog("listServiceProviderList", this.getClass().getName()));
		try {

			List<BusinessPartner> businessPartners = businessPartnerRepository.findActivePartnersByCompany(orgId,
					ACTIVE);

			businessPartners = businessPartners.stream().map(bp -> {

				if (bp.getMappedCompany() != null && orgId == bp.getMappedCompany().getCompanyId()) {

					CompanyMaster temp = bp.getCompany();

					bp.setCompany(bp.getMappedCompany());
					bp.setMappedCompany(temp);
				}

				return bp;
			}).toList();

			return businessPartners.stream().filter(role -> role.getIsBoth())
					.map(role -> mapper.toBusinessPartnerVO(role)).collect(Collectors.toList());
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
}
