package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.*;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

import java.time.LocalDateTime;
import java.util.UUID;

import com.flickzz.desk.vo.request.EnquiryRegisterRequestVO;
import com.flickzz.desk.vo.request.EnquiryRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.*;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.vo.*;

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
	
	@Autowired
	BusinessPartnerRepository businessPartnerRepository;

	@Value("${uid.prefix}")
	private String uidPrefix;

	public void enquiryRegister(EnquiryRegisterRequestVO request) {
		log.info(generateLog("enquiryRegister", this.getClass().getName()));
		try {
			var existingEnquiry = enquiryRegistrationRepository.findTopByEmailAndIsActiveOrderByVersionDesc(request.getEmail(), ACTIVE);
			
			int nextVersion = 1;
			if (existingEnquiry.isPresent()) {
				log.info("Existing enquiry found for email: {}", request.getEmail());
				EnquiryRegistration enquiryRegistration = existingEnquiry.get();
				
				if (enquiryRegistration.getEnquiryInfo() != null &&
					enquiryRegistration.getEnquiryInfo().getExpiryTime().isAfter(LocalDateTime.now())) {
					log.info("Existing enquiry is still valid for email: {}", request.getEmail());
					throw new FlickzzDeskException(ALREADY_EXISTS,
							"Registration already exists, please check your email to verify existing enquiry");
				} else {
					log.info("Existing enquiry expired for email: {}, proceeding with new registration", request.getEmail());
					var allExistingEnquiries = enquiryRegistrationRepository.findAll().stream()
							.filter(e -> e.getEmail().equals(request.getEmail()) && e.getIsActive())
							.toList();
					
					// Get max version from existing enquiries
					nextVersion = allExistingEnquiries.stream()
							.map(EnquiryRegistration::getVersion)
							.max(Integer::compareTo)
							.orElse(0) + 1;
					
					// Inactivate all existing enquiries and their associated companies
					allExistingEnquiries.forEach(e -> {
						log.info("Inactivating existing enquiry with ID: {} and version: {}", e.getEnquiryId(), e.getVersion());
						e.setIsActive(Boolean.FALSE);
						enquiryRegistrationRepository.save(e);
						
						// Inactivate and clear email from company master to avoid unique constraint error
						if (e.getCompany() != null) {
							log.info("Inactivating associated company with ID: {} for enquiry ID: {}", e.getCompany().getCompanyId(), e.getEnquiryId());
							e.getCompany().setIsActive(Boolean.FALSE);
							e.getCompany().setMail(e.getCompany().getMail() + "_inactive_" + System.currentTimeMillis());
							companyMasterRepository.save(e.getCompany());
						}
					});
				}
			}

			companyMasterRepository.findTopByCompanyNameAndIsActiveOrderByVersionDesc(request.getOrgName(), ACTIVE).ifPresent(companyMaster -> {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						getDescription(ALREADY_EXISTS.getDescription(), COMPANY_NAME));
			});

			CountryMaster country = countryMasterRepository.findById(request.getCountryId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), COUNTRY)));

			String currentUID = companyMasterRepository.findMaxUid();

			String universalId = generateUniversalId(uidPrefix, currentUID);

			CompanyMaster company = CompanyMaster.builder().companyName(request.getOrgName()).uid(universalId)
					.employeeSize(request.getEmployeeSize()).registeredNumber(request.getPhoneNumber()).version(nextVersion)
					.phoneCode(request.getPhoneCode()).mail(request.getEmail()).country(country).isActive(true)
					.isCreatorAdmin(true).createdBy(0L).build();
			companyMasterRepository.save(company);

			EnquiryRegistration enquiry = mapper.enquiryRegisterRequestToEnquiryRegistration(request, country,
					ROLE_ADMIN, company);
			// Set version to nextVersion (1 for new or oldVersion + 1 for reregistration)
			enquiry.setVersion(nextVersion);
			enquiryRegistrationRepository.save(enquiry);

			company.setCreatedBy(enquiry.getEnquiryId());
			companyMasterRepository.save(company);

			handleEnquiry(enquiry);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in enquiryRegister method in EnquiryService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void updateEnquiry(EnquiryRegisterRequestVO request) {
		log.info(generateLog("updateEnquiry", this.getClass().getName()));
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
			// Increment version for update
			exitingRegistration.setVersion(exitingRegistration.getVersion() + 1);
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
		log.info(generateLog("handleEnquiry", this.getClass().getName()));
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
			log.error("FlickzzDeskException in handleEnquiry method in EnquiryService: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Exception in handleEnquiry method in EnquiryService: {}", e.getMessage());
		}

	}

	public EnquiryInfoVO verifyEnquiry(String token) {
		log.info(generateLog("verifyEnquiry", this.getClass().getName()));
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
		log.info(generateLog("submitEnquiry", this.getClass().getName()));
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
			BusinessPartner entity = new BusinessPartner();
			entity.setCompany(enquiryRegistration.getCompany());
			entity.setMappedCompany(enquiryRegistration.getCompany());
			entity.setIsBoth(Boolean.TRUE);
			entity.setCreatedBy(enquiryRegistration.getEnquiryId());
			entity.setIsCreatorAdmin(Boolean.TRUE);
			businessPartnerRepository.save(entity);
			enquiryInfoRepository.save(enquiryInfo);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in submitEnquiry method in EnquiryService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public EnquiryRegistrationVO getEnquiriesByUserEmail(String userEmail) {
		log.info(generateLog("getEnquiriesByUserEmail", this.getClass().getName()));
		try {
			EnquiryRegistration enquiryRegistration = enquiryRegistrationRepository
					.findTopByEmailAndIsActiveOrderByVersionDesc(userEmail, ACTIVE)
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
		log.info(generateLog("getEnquiriesByUserEmail", this.getClass().getName()));
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
