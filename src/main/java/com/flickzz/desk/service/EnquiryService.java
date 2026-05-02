package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.ACTIVE;
import static com.flickzz.desk.config.FlickzzDeskConstants.COUNTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.ROLE_ADMIN;
import static com.flickzz.desk.config.FlickzzDeskConstants.USERNAME_OR_EMAIL;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.CountryMaster;
import com.flickzz.desk.model.EnquiryInfo;
import com.flickzz.desk.model.EnquiryRegistration;
import com.flickzz.desk.repo.CountryMasterRepository;
import com.flickzz.desk.repo.EnquiryInfoRepository;
import com.flickzz.desk.repo.EnquiryRegistrationRepository;
import com.flickzz.desk.vo.EnquiryInfoVO;
import com.flickzz.desk.vo.EnquiryRegisterRequestVO;
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
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CommonMapper mapper;

	@Autowired
	private MailService mailService;

	public void enquiryRegister(EnquiryRegisterRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			enquiryRegistrationRepository.findByEmailAndIsActive(request.getEmail(), ACTIVE).ifPresent(er -> {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						getDescription(ALREADY_EXISTS.getDescription(), USERNAME_OR_EMAIL));
			});

			CountryMaster country = countryMasterRepository.findById(request.getCountryId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), COUNTRY)));

			EnquiryRegistration enquiry = mapper.enquiryRegisterRequestToEnquiryRegistration(request, country,
					ROLE_ADMIN);
			enquiryRegistrationRepository.save(enquiry);

			handleEnquiry(enquiry);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in enquiryRegister method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	private void handleEnquiry(EnquiryRegistration enquiry) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
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
			log.error("Exception in handleEnquiry method in FlickzzDeskService");
		}

	}

	public EnquiryInfoVO verifyEnquiry(String token) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
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

	}
}
