package com.flickzz.desk.controller;

import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.EXIT;
import static com.flickzz.desk.config.FlickzzDeskConstants.LOGIN;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.LOGOUT_ALL_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.REGISTRATION_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.TOKEN_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskSuccessResponseHandler.handleSuccessResponse;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flickzz.desk.service.FlickzzDeskService;
import com.flickzz.desk.vo.CommonRequestVO;
import com.flickzz.desk.vo.GeneralRespVO;
import com.flickzz.desk.vo.LoginResponseVO;
import com.flickzz.desk.vo.RegisterLoginRequestVO;
import com.flickzz.desk.vo.RegisterLoginResponseVO;
import com.flickzz.desk.vo.VerificationRequestVO;

@CrossOrigin
@RestController
@RequestMapping("/")
public class FlickzzDeskController {
	
	private static final Logger log = LoggerFactory.getLogger(FlickzzDeskController.class);
	
	@Autowired
	private FlickzzDeskService flickzzDeskService;
	

	@PostMapping("register")
	public ResponseEntity<GeneralRespVO> register(@RequestBody RegisterLoginRequestVO request) throws Exception {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		
		RegisterLoginResponseVO respVO = flickzzDeskService.register(request);
        
        log.debug(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(REGISTRATION_SUCCESS, getDescription(REGISTRATION_SUCCESS.getDescription(), LOGIN), respVO);
	}
	
	@PostMapping("verify")
	public ResponseEntity<GeneralRespVO> verifyCode(@RequestBody VerificationRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		
		RegisterLoginResponseVO respVO = flickzzDeskService.verifyCode(request);
        
        log.debug(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(REGISTRATION_SUCCESS, getDescription(REGISTRATION_SUCCESS.getDescription(), LOGIN), respVO);
	}
	
	@PostMapping("login")
	public ResponseEntity<GeneralRespVO> userLogin(@RequestBody RegisterLoginRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		
		RegisterLoginResponseVO respVO = flickzzDeskService.userLogin(request);
        
        log.debug(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(REGISTRATION_SUCCESS, getDescription(REGISTRATION_SUCCESS.getDescription(), LOGIN), respVO);
	}
	
	@PostMapping("refresh")
	public ResponseEntity<GeneralRespVO> authRefresh(@RequestBody CommonRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
	    
		LoginResponseVO respVO = flickzzDeskService.authRefresh(request);
		
	    log.debug(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(TOKEN_SUCCESS, TOKEN_SUCCESS.getDescription(), respVO);
	}
	
    @PostMapping("/logoutAll")
    public ResponseEntity<GeneralRespVO> logoutAll(@RequestBody CommonRequestVO request) {
    	log.debug(generateLog(ENTRY, this.getClass().getName()));
	    
    	flickzzDeskService.logoutAllUsers(request);
        
        log.debug(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(LOGOUT_ALL_SUCCESS, getDescription(LOGOUT_ALL_SUCCESS.getDescription(), request.getUsername()));
    }
}
