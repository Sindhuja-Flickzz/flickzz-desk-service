package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.ACTIVE;
import static com.flickzz.desk.config.FlickzzDeskConstants.COMPANY_NAME;
import static com.flickzz.desk.config.FlickzzDeskConstants.COUNTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.ROLE_ADMIN;
import static com.flickzz.desk.config.FlickzzDeskConstants.USERNAME_OR_EMAIL;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateUniversalId;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.ALREADY_EXISTS;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DEFAULT_ERROR_CODE;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DOES_NOT_EXIST;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.EXPIRED_LINK;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.INVALID_PASSWORD;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.INVALID_TOKEN;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.CityMaster;
import com.flickzz.desk.model.CompanyMaster;
import com.flickzz.desk.model.CountryMaster;
import com.flickzz.desk.model.EnquiryInfo;
import com.flickzz.desk.model.EnquiryRegistration;
import com.flickzz.desk.model.StateMaster;
import com.flickzz.desk.repo.CityMasterRepository;
import com.flickzz.desk.repo.CompanyMasterRepository;
import com.flickzz.desk.repo.CountryMasterRepository;
import com.flickzz.desk.repo.EnquiryInfoRepository;
import com.flickzz.desk.repo.EnquiryRegistrationRepository;
import com.flickzz.desk.repo.StateMasterRepository;
import com.flickzz.desk.vo.EnquiryInfoVO;
import com.flickzz.desk.vo.EnquiryRegisterRequestVO;
import com.flickzz.desk.vo.EnquiryRegistrationVO;
import com.flickzz.desk.vo.EnquiryRequestVO;

@Service
public class EnquiryService {

	private static final Logger log = LoggerFactory.getLogger(EnquiryService.class);

	@Autowired
	private EnquiryRegistrationRepository enquiryRegistrationRepository;

	@Autowired
	private EnquiryInfoRepository enquiryInfoRepository;

	@Autowired
	private CountryMasterRepository countryMasterRepository;

	@Autowired
	private CompanyMasterRepository companyMasterRepository;

	@Autowired
	private StateMasterRepository stateMasterRepository;

	@Autowired
	private CityMasterRepository cityMasterRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CommonMapper mapper;

	@Autowired
	private MailService mailService;

	@Value("${uid.prefix}")
	private String uidPrefix;

	public void enquiryRegister(EnquiryRegisterRequestVO request) {
		log.debug(generateLog("enquiryRegister", this.getClass().getName()));
		try {
			enquiryRegistrationRepository.findByEmailAndIsActive(request.getEmail(), ACTIVE).ifPresent(er -> {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						getDescription(ALREADY_EXISTS.getDescription(), USERNAME_OR_EMAIL));
			});

			companyMasterRepository.findByCompanyNameAndIsActive(request.getOrgName(), ACTIVE).ifPresent(er -> {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						getDescription(ALREADY_EXISTS.getDescription(), COMPANY_NAME));
			});

			CountryMaster country = countryMasterRepository.findById(request.getCountryId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), COUNTRY)));

			String currentUID = companyMasterRepository.findMaxUid();

			String universalId = generateUniversalId(uidPrefix, currentUID);

			CompanyMaster company = CompanyMaster.builder().companyName(request.getOrgName()).uid(universalId)
					.employeeSize(request.getEmployeeSize()).registeredNumber(request.getPhoneNumber())
					.phoneCode(request.getPhoneCode()).mail(request.getEmail()).country(country).isActive(true).build();
			companyMasterRepository.save(company);

			EnquiryRegistration enquiry = mapper.enquiryRegisterRequestToEnquiryRegistration(request, country,
					ROLE_ADMIN, company);
			enquiryRegistrationRepository.save(enquiry);

			handleEnquiry(enquiry);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in enquiryRegister method in EnquiryService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void updateEnquiry(EnquiryRegisterRequestVO request) {
		log.debug(generateLog("updateEnquiry", this.getClass().getName()));
		try {
			EnquiryRegistration exitingRegistration = enquiryRegistrationRepository
					.findByEnquiryIdAndIsActive(request.getEnquiryId(), ACTIVE)
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), USERNAME_OR_EMAIL)));

			CountryMaster country = countryMasterRepository.findById(request.getCountryId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), COUNTRY)));

			StateMaster state = stateMasterRepository.findById(request.getStateId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), "State")));

			CityMaster city = cityMasterRepository.findById(request.getCityId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), "City")));

			exitingRegistration.setFirstName(request.getFirstName());
			exitingRegistration.setMiddleName(request.getMiddleName());
			exitingRegistration.setLastName(request.getLastName());
			exitingRegistration.setPhoneNumber(request.getPhoneNumber());
			exitingRegistration.setPhoneCode(request.getPhoneCode());
			exitingRegistration.setCountry(country);
			exitingRegistration.setState(state);
			exitingRegistration.setCity(city);
			exitingRegistration.setUpdatedAt(LocalDateTime.now());
			enquiryRegistrationRepository.save(exitingRegistration);
		} catch (

		FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in enquiryRegister method in EnquiryService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	private void handleEnquiry(EnquiryRegistration enquiry) {
		log.debug(generateLog("handleEnquiry", this.getClass().getName()));
		try {
			// Generate token
			String token = UUID.randomUUID().toString();
			EnquiryInfo entity = new EnquiryInfo();
			entity.setToken(token);
			entity.setEnquiryRegistration(enquiry);
			entity.setExpiryTime(LocalDateTime.now().plusHours(3));
			enquiryInfoRepository.save(entity);

			// Send email
			mailService.sendEnquiryLink(enquiry.getEmail(), enquiry.getUserName(), token);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in handleEnquiry method in EnquiryService");
		}

	}

	public EnquiryInfoVO verifyEnquiry(String token) {
		log.debug(generateLog("verifyEnquiry", this.getClass().getName()));
		try {
			EnquiryInfo enquiryInfo = enquiryInfoRepository.findByToken(token).orElseThrow(
					() -> new FlickzzDeskException(INVALID_TOKEN, getDescription(INVALID_TOKEN.getDescription())));

			if (enquiryInfo.getUsed() || LocalDateTime.now().isAfter(enquiryInfo.getExpiryTime())) {
				throw new FlickzzDeskException(EXPIRED_LINK, getDescription(EXPIRED_LINK.getDescription()));
			}
			return mapper.toEnquiryInfoVo(enquiryInfo);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in verifyEnquiry method in EnquiryService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void submitEnquiry(EnquiryRequestVO request) {
		log.debug(generateLog("submitEnquiry", this.getClass().getName()));
		try {
			EnquiryInfo enquiryInfo = enquiryInfoRepository.findByToken(request.getToken()).orElseThrow(
					() -> new FlickzzDeskException(INVALID_TOKEN, getDescription(INVALID_TOKEN.getDescription())));
			if (enquiryInfo.getUsed() || LocalDateTime.now().isAfter(enquiryInfo.getExpiryTime())) {
				new FlickzzDeskException(EXPIRED_LINK, getDescription(EXPIRED_LINK.getDescription()));
			}

			if (request.getPassword() == null || request.getPassword().isEmpty()) {
				throw new FlickzzDeskException(INVALID_PASSWORD, getDescription(INVALID_PASSWORD.getDescription()));
			}

			EnquiryRegistration enquiryRegistration = enquiryInfo.getEnquiryRegistration();
			enquiryRegistration.setPassword(passwordEncoder.encode(request.getPassword()));
			enquiryRegistrationRepository.save(enquiryRegistration);

			enquiryInfo.setUsed(true);
			enquiryInfoRepository.save(enquiryInfo);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in submitEnquiry method in EnquiryService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public EnquiryRegistrationVO getEnquiriesByUserEmail(String userEmail) {
		log.debug(generateLog("getEnquiriesByUserEmail", this.getClass().getName()));
		try {
			EnquiryRegistration enquiryRegistration = enquiryRegistrationRepository
					.findByEmailAndIsActive(userEmail, ACTIVE)
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), USERNAME_OR_EMAIL)));

			return mapper.toEnquiryRegistrationVO(enquiryRegistration);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getEnquiriesByUserEmail method in EnquiryService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public EnquiryRegistrationVO getCompanyInfoByUserEmail(String userEmail) {
		log.debug(generateLog("getEnquiriesByUserEmail", this.getClass().getName()));
		try {
			EnquiryRegistration enquiryRegistration = enquiryRegistrationRepository.findByEmail(userEmail)
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), USERNAME_OR_EMAIL)));

			return mapper.toEnquiryRegistrationVO(enquiryRegistration);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getEnquiriesByUserEmail method in EnquiryService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}
}
