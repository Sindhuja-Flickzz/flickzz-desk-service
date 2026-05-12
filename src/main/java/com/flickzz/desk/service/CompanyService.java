package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.ACTIVE;
import static com.flickzz.desk.config.FlickzzDeskConstants.CITY;
import static com.flickzz.desk.config.FlickzzDeskConstants.COMPANY;
import static com.flickzz.desk.config.FlickzzDeskConstants.COMPANY_NAME;
import static com.flickzz.desk.config.FlickzzDeskConstants.COUNTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.CURRENCY;
import static com.flickzz.desk.config.FlickzzDeskConstants.REGISTERED_NUMBER;
import static com.flickzz.desk.config.FlickzzDeskConstants.STATE;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.ALREADY_EXISTS;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DEFAULT_ERROR_CODE;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DOES_NOT_EXIST;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.INVALID_FIELD;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.CityMaster;
import com.flickzz.desk.model.CompanyMaster;
import com.flickzz.desk.model.CountryMaster;
import com.flickzz.desk.model.StateMaster;
import com.flickzz.desk.repo.CityMasterRepository;
import com.flickzz.desk.repo.CompanyMasterRepository;
import com.flickzz.desk.repo.CountryMasterRepository;
import com.flickzz.desk.repo.StateMasterRepository;
import com.flickzz.desk.vo.CompanyMasterRequestVO;
import com.flickzz.desk.vo.CompanyMasterVO;

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
	private CommonMapper mapper;

	public CompanyMasterVO createCompany(CompanyMasterRequestVO request) {
		log.debug(generateLog("createCompany", this.getClass().getName()));
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
		log.debug(generateLog("getCompanyInfo", this.getClass().getName()));
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
		log.debug(generateLog("updateCompany", this.getClass().getName()));
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
		log.debug(generateLog("deleteCompany", this.getClass().getName()));
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
		log.debug(generateLog("listCompanies", this.getClass().getName()));
		try {
			return companyMasterRepository.findByIsActive(ACTIVE).stream().map(mapper::toCompanyMasterVO).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in listCompanies method in CompanyService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}
}
