package com.flickzz.desk.controller;

import com.flickzz.desk.config.FlickzzDeskResponse;
import com.flickzz.desk.service.EnquiryService;
import com.flickzz.desk.vo.EnquiryInfoVO;
import com.flickzz.desk.vo.EnquiryRegistrationVO;
import com.flickzz.desk.vo.request.EnquiryRegisterRequestVO;
import com.flickzz.desk.vo.request.EnquiryRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskResponseHandler.handleSuccessResponse;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;

@CrossOrigin
@RestController
@RequestMapping("/enquiry")
public class EnquiryController {

    private static final Logger log = LoggerFactory.getLogger(EnquiryController.class);

    @Autowired
    private EnquiryService enquiryService;

    @PostMapping("/register")
    public ResponseEntity<FlickzzDeskResponse> enquiryRegister(@RequestBody EnquiryRegisterRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        enquiryService.enquiryRegister(request);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(REGISTRATION_SUCCESS,
                getDescription(REGISTRATION_SUCCESS.getDescription(), LOGIN));
    }

    @PostMapping("/update")
    public ResponseEntity<FlickzzDeskResponse> updateEnquiry(@RequestBody EnquiryRegisterRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        enquiryService.updateEnquiry(request);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(REGISTRATION_SUCCESS,
                getDescription(REGISTRATION_SUCCESS.getDescription(), LOGIN));
    }

    @GetMapping("/verify")
    public ResponseEntity<FlickzzDeskResponse> verifyEnquiry(@RequestParam String token) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        EnquiryInfoVO respVO = enquiryService.verifyEnquiry(token);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(ENQUIRY_VERIFICATION_SUCCESS, ENQUIRY_VERIFICATION_SUCCESS.getDescription(),
                respVO);
    }

    @PostMapping("/submit-secure")
    public ResponseEntity<FlickzzDeskResponse> submitEnquiry(@RequestBody EnquiryRequestVO enquiryInfoVO) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        enquiryService.submitEnquiry(enquiryInfoVO);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), ENTRY));
    }

    @GetMapping("/company/{userEmail}")
    public ResponseEntity<FlickzzDeskResponse> getCompanyInfoByUserEmail(@PathVariable String userEmail) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        EnquiryRegistrationVO respVO = enquiryService.getCompanyInfoByUserEmail(userEmail);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), ENTRY), respVO);
    }

    @GetMapping("/{userEmail}/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> getEnquiriesByUserEmail(@PathVariable String userEmail, @PathVariable String orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        EnquiryRegistrationVO respVO = enquiryService.getEnquiriesByUserEmail(userEmail, orgId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), ENTRY), respVO);
    }
}
