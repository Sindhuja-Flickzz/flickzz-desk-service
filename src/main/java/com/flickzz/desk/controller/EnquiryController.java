package com.flickzz.desk.controller;

import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.EXIT;
import static com.flickzz.desk.config.FlickzzDeskConstants.LOGIN;
import static com.flickzz.desk.config.FlickzzDeskResponseHandler.handleSuccessResponse;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.CREATE_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.ENQUIRY_VERIFICATION_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.REGISTRATION_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flickzz.desk.config.FlickzzDeskResponse;
import com.flickzz.desk.service.EnquiryService;
import com.flickzz.desk.vo.EnquiryInfoVO;
import com.flickzz.desk.vo.EnquiryRegisterRequestVO;
import com.flickzz.desk.vo.EnquiryRegistrationVO;
import com.flickzz.desk.vo.EnquiryRequestVO;

@CrossOrigin
@RestController
@RequestMapping("/enquiry")
public class EnquiryController {

	private static final Logger log = LoggerFactory.getLogger(EnquiryController.class);

	@Autowired
	private EnquiryService enquiryService;

	@PostMapping("/register")
	public ResponseEntity<FlickzzDeskResponse> enquiryRegister(@RequestBody EnquiryRegisterRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		enquiryService.enquiryRegister(request);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(REGISTRATION_SUCCESS,
				getDescription(REGISTRATION_SUCCESS.getDescription(), LOGIN));
	}

	@PostMapping("/update")
	public ResponseEntity<FlickzzDeskResponse> updateEnquiry(@RequestBody EnquiryRegisterRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		enquiryService.updateEnquiry(request);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(REGISTRATION_SUCCESS,
				getDescription(REGISTRATION_SUCCESS.getDescription(), LOGIN));
	}

	@GetMapping("/verify")
	public ResponseEntity<FlickzzDeskResponse> verifyEnquiry(@RequestParam String token) throws Exception {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		EnquiryInfoVO respVO = enquiryService.verifyEnquiry(token);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(ENQUIRY_VERIFICATION_SUCCESS, ENQUIRY_VERIFICATION_SUCCESS.getDescription(),
				respVO);
	}

	@PostMapping("/submit-secure")
	public ResponseEntity<FlickzzDeskResponse> submitEnquiry(@RequestBody EnquiryRequestVO enquiryInfoVO)
			throws Exception {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		enquiryService.submitEnquiry(enquiryInfoVO);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), ENTRY));
	}

	@GetMapping("/company/{userEmail}")
	public ResponseEntity<FlickzzDeskResponse> getCompanyInfoByUserEmail(@PathVariable String userEmail)
			throws Exception {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		EnquiryRegistrationVO respVO = enquiryService.getCompanyInfoByUserEmail(userEmail);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), ENTRY), respVO);
	}

	@GetMapping("/{userEmail}")
	public ResponseEntity<FlickzzDeskResponse> getEnquiriesByUserEmail(@PathVariable String userEmail)
			throws Exception {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		EnquiryRegistrationVO respVO = enquiryService.getEnquiriesByUserEmail(userEmail);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), ENTRY), respVO);
	}
}
