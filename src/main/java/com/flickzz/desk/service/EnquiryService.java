package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.*;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.flickzz.desk.vo.request.EnquiryRegisterRequestVO;
import com.flickzz.desk.vo.request.EnquiryRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.*;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.vo.*;
import tools.jackson.databind.ObjectMapper;

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
	private BusinessPartnerRepository businessPartnerRepository;

	@Autowired
	private AuditService auditService;

	@Autowired
	private ObjectMapper objectMapper;

	@Value("${uid.prefix}")
	private String uidPrefix;

	public void enquiryRegister(EnquiryRegisterRequestVO request) {
		log.info(generateLog("enquiryRegister", this.getClass().getName()));
		try {
			var existingEnquiry = enquiryRegistrationRepository.findTopByEmailAndIsActiveTrueOrderByVersionDesc(request.getEmail());

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
							e.getCompany().setMail(e.getCompany().getMail());
							companyMasterRepository.save(e.getCompany());
						}
					});
				}
			}

			companyMasterRepository.findTopByCompanyNameAndIsActiveOrderByVersionDesc(request.getOrgName(), ACTIVE).ifPresent(companyMaster -> {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						getDescription(ALREADY_EXISTS.getDescription(), COMPANY_NAME));
			});

			CountryMaster country = countryMasterRepository.findByCountryIdAndIsActiveTrue(request.getCountryId())
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

			// Record CREATE audit
			Map<String, Object> attemptedMap = new HashMap<>();
			attemptedMap.put("email", enquiry.getEmail());
			attemptedMap.put("firstName", enquiry.getFirstName());
			attemptedMap.put("lastName", enquiry.getLastName());
			attemptedMap.put("version", enquiry.getVersion());
			String attempted = null;
			try {
				attempted = objectMapper.writeValueAsString(attemptedMap);
			} catch (Exception ignore) {
			}

			auditService.recordAudit(mapper.toSystemAuditRequest("Enquiry", "Registration", "EnquiryRegistration",
					enquiry.getEnquiryId(),
					"CREATE",
					attempted,
					null,
					null,
					0L,
					"SYSTEM",
					company.getCompanyId(),
					"SUCCESS",
					null));
		} catch (DataIntegrityViolationException e) {
			log.info("DataIntegrityViolationException in enquiryRegister method in EnquiryService: {}", e.getMessage());
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Enquiry", "Registration", "EnquiryRegistration",
					null, "CREATE", null, null, null, 0L, "SYSTEM", null, "FAILED", "Email or Company Name exist"), e);
			throw new FlickzzDeskException(DB_SAVE_ERROR, "Email or Company Name");
		} catch (FlickzzDeskException e) {
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Enquiry", "Registration", "EnquiryRegistration",
					null, "CREATE", null, null, null, 0L, "SYSTEM", null, "FAILED", e.getMessage()), e);
			throw e;
		} catch (Exception e) {
			log.error("Exception in enquiryRegister method in EnquiryService");
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Enquiry", "Registration", "EnquiryRegistration",
					null, "CREATE", null, null, null, 0L, "SYSTEM", null, "FAILED", e.getMessage()), e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void updateEnquiry(EnquiryRegisterRequestVO request) {
		log.info(generateLog("updateEnquiry", this.getClass().getName()));
		try {
			EnquiryRegistration existingRegistration = enquiryRegistrationRepository
					.findByEnquiryIdAndIsActiveTrue(request.getEnquiryId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), USERNAME_OR_EMAIL)));

			// Capture old values for audit
			String oldValue = null;
			try {
				Map<String, Object> oldData = new HashMap<>();
				oldData.put("firstName", existingRegistration.getFirstName());
				oldData.put("lastName", existingRegistration.getLastName());
				oldData.put("phoneNumber", existingRegistration.getPhoneNumber());
				oldData.put("version", existingRegistration.getVersion());
				oldValue = objectMapper.writeValueAsString(oldData);
			} catch (Exception ignore) {
			}

			CountryMaster country = countryMasterRepository.findByCountryIdAndIsActiveTrue(request.getCountryId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), COUNTRY)));

			StateMaster state = stateMasterRepository.findById(request.getStateId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), "State")));

			CityMaster city = cityMasterRepository.findByCityIdAndIsActiveTrue(request.getCityId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), "City")));

			existingRegistration.setFirstName(request.getFirstName());
			existingRegistration.setMiddleName(request.getMiddleName());
			existingRegistration.setLastName(request.getLastName());
			existingRegistration.setPhoneNumber(request.getPhoneNumber());
			existingRegistration.setPhoneCode(request.getPhoneCode());
			existingRegistration.setCountry(country);
			existingRegistration.setState(state);
			existingRegistration.setCity(city);
			existingRegistration.setUpdatedAt(LocalDateTime.now());
			// Increment version for update
			existingRegistration.setVersion(existingRegistration.getVersion() + 1);
			enquiryRegistrationRepository.save(existingRegistration);

			// Capture new values and changed fields for audit
			String newValue = null;
			String changedFieldsStr = null;
			try {
				Map<String, Object> newData = new HashMap<>();
				newData.put("firstName", existingRegistration.getFirstName());
				newData.put("lastName", existingRegistration.getLastName());
				newData.put("phoneNumber", existingRegistration.getPhoneNumber());
				newData.put("version", existingRegistration.getVersion());
				newValue = objectMapper.writeValueAsString(newData);

				Map<String, Object> changedFields = new HashMap<>();
				changedFields.put("firstName", request.getFirstName());
				changedFields.put("lastName", request.getLastName());
				changedFields.put("phoneNumber", request.getPhoneNumber());
				changedFields.put("version", existingRegistration.getVersion());
				changedFieldsStr = objectMapper.writeValueAsString(changedFields);
			} catch (Exception ignore) {
			}

			auditService.recordAudit(mapper.toSystemAuditRequest("Enquiry", "Registration", "EnquiryRegistration",
					existingRegistration.getEnquiryId(),
					"UPDATE",
					oldValue,
					newValue,
					changedFieldsStr,
					0L,
					"SYSTEM",
					existingRegistration.getCompany() != null ? existingRegistration.getCompany().getCompanyId() : null,
					"SUCCESS",
					null));
		} catch (DataIntegrityViolationException e) {
			log.info("DataIntegrityViolationException in updateEnquiry method in EnquiryService: {}", e.getMessage());
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Enquiry", "Registration", "EnquiryRegistration",
					Long.valueOf(request.getEnquiryId()), "UPDATE", null, null, null, 0L, "SYSTEM", null, "FAILED", "Update failed"), e);
			throw new FlickzzDeskException(DB_SAVE_ERROR, "Email or Company Name");
		} catch (FlickzzDeskException e) {
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Enquiry", "Registration", "EnquiryRegistration",
					Long.valueOf(request.getEnquiryId()), "UPDATE", null, null, null, 0L, "SYSTEM", null, "FAILED", e.getMessage()), e);
			throw e;
		} catch (Exception e) {
			log.error("Exception in updateEnquiry method in EnquiryService");
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Enquiry", "Registration", "EnquiryRegistration",
					Long.valueOf(request.getEnquiryId()), "UPDATE", null, null, null, 0L, "SYSTEM", null, "FAILED", e.getMessage()), e);
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

			// Record audit for enquiry info creation
			Map<String, Object> changedFields = new HashMap<>();
			changedFields.put("token", token);
			changedFields.put("expiryTime", entity.getExpiryTime());
			String changedFieldsStr = null;
			try {
				changedFieldsStr = objectMapper.writeValueAsString(changedFields);
			} catch (Exception ignore) {
			}

			auditService.recordAudit(mapper.toSystemAuditRequest("Enquiry", "Verification", "EnquiryInfo",
					entity.getId(),
					"CREATE",
					null,
					null,
					changedFieldsStr,
					0L,
					"SYSTEM",
					enquiry.getCompany() != null ? enquiry.getCompany().getCompanyId() : null,
					"SUCCESS",
					null));
		} catch (FlickzzDeskException e) {
			log.error("FlickzzDeskException in handleEnquiry method in EnquiryService: {}", e.getMessage());
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Enquiry", "Verification", "EnquiryInfo",
					null, "CREATE", null, null, null, 0L, "SYSTEM", null, "FAILED", e.getMessage()), e);
			throw e;
		} catch (Exception e) {
			log.error("Exception in handleEnquiry method in EnquiryService: {}", e.getMessage());
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Enquiry", "Verification", "EnquiryInfo",
					null, "CREATE", null, null, null, 0L, "SYSTEM", null, "FAILED", e.getMessage()), e);
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

			// Record audit for verification
			auditService.recordAudit(mapper.toSystemAuditRequest("Enquiry", "Verification", "EnquiryInfo",
					enquiryInfo.getId(),
					"VERIFY",
					null,
					null,
					null,
					0L,
					"SYSTEM",
					enquiryInfo.getEnquiryRegistration() != null && enquiryInfo.getEnquiryRegistration().getCompany() != null ? enquiryInfo.getEnquiryRegistration().getCompany().getCompanyId() : null,
					"SUCCESS",
					null));

			return mapper.toEnquiryInfoVo(enquiryInfo);
		} catch (FlickzzDeskException e) {
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Enquiry", "Verification", "EnquiryInfo",
					null, "VERIFY", null, null, null, 0L, "SYSTEM", null, "FAILED", e.getMessage()), e);
			throw e;
		} catch (Exception e) {
			log.error("Exception in verifyEnquiry method in EnquiryService");
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Enquiry", "Verification", "EnquiryInfo",
					null, "VERIFY", null, null, null, 0L, "SYSTEM", null, "FAILED", e.getMessage()), e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void submitEnquiry(EnquiryRequestVO request) {
		log.info(generateLog("submitEnquiry", this.getClass().getName()));
		try {
			EnquiryInfo enquiryInfo = enquiryInfoRepository.findByToken(request.getToken()).orElseThrow(
					() -> new FlickzzDeskException(INVALID_TOKEN, getDescription(INVALID_TOKEN.getDescription())));
			if (enquiryInfo.getUsed() || LocalDateTime.now().isAfter(enquiryInfo.getExpiryTime())) {
				throw new FlickzzDeskException(EXPIRED_LINK, getDescription(EXPIRED_LINK.getDescription()));
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

			// Record audit for enquiry submission
			Map<String, Object> changedFields = new HashMap<>();
			changedFields.put("passwordSet", true);
			changedFields.put("enquiryUsed", true);
			changedFields.put("businessPartnerCreated", true);
			String changedFieldsStr = null;
			try {
				changedFieldsStr = objectMapper.writeValueAsString(changedFields);
			} catch (Exception ignore) {
			}

			auditService.recordAudit(mapper.toSystemAuditRequest("Enquiry", "Submission", "EnquiryRegistration",
					enquiryRegistration.getEnquiryId(),
					"SUBMIT",
					null,
					null,
					changedFieldsStr,
					0L,
					"SYSTEM",
					enquiryRegistration.getCompany() != null ? enquiryRegistration.getCompany().getCompanyId() : null,
					"SUCCESS",
					null));
		} catch (FlickzzDeskException e) {
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Enquiry", "Submission", "EnquiryRegistration",
					null, "SUBMIT", null, null, null, 0L, "SYSTEM", null, "FAILED", e.getMessage()), e);
			throw e;
		} catch (Exception e) {
			log.error("Exception in submitEnquiry method in EnquiryService");
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Enquiry", "Submission", "EnquiryRegistration",
					null, "SUBMIT", null, null, null, 0L, "SYSTEM", null, "FAILED", e.getMessage()), e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public EnquiryRegistrationVO getEnquiriesByUserEmail(String userEmail) {
		log.info(generateLog("getEnquiriesByUserEmail", this.getClass().getName()));
		try {
			EnquiryRegistration enquiryRegistration = enquiryRegistrationRepository
					.findTopByEmailAndIsActiveTrueOrderByVersionDesc(userEmail)
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
			EnquiryRegistration enquiryRegistration = enquiryRegistrationRepository.findByEmailAndIsActiveTrue(userEmail)
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
