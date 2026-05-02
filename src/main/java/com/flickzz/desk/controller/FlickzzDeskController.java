package com.flickzz.desk.controller;

import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.EXIT;
import static com.flickzz.desk.config.FlickzzDeskConstants.LOGIN;
import static com.flickzz.desk.config.FlickzzDeskConstants.USER_LIST;
import static com.flickzz.desk.config.FlickzzDeskResponseHandler.handleSuccessResponse;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.FETCH_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.LOGOUT_ALL_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.PASSWORD_RESET_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.REGISTRATION_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.TOKEN_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flickzz.desk.config.FlickzzDeskResponse;
import com.flickzz.desk.service.FlickzzDeskService;
import com.flickzz.desk.vo.CommonRequestVO;
import com.flickzz.desk.vo.LoginResponseVO;
import com.flickzz.desk.vo.RegisterLoginRequestVO;
import com.flickzz.desk.vo.RegisterLoginResponseVO;
import com.flickzz.desk.vo.UserVO;
import com.flickzz.desk.vo.VerificationRequestVO;

@CrossOrigin
@RestController
@RequestMapping("/")
public class FlickzzDeskController {

	private static final Logger log = LoggerFactory.getLogger(FlickzzDeskController.class);

	@Autowired
	private FlickzzDeskService flickzzDeskService;

//	@PostMapping("register")
//	public ResponseEntity<FlickzzDeskResponse> register(@RequestBody RegisterLoginRequestVO request) throws Exception {
//		log.debug(generateLog(ENTRY, this.getClass().getName()));
//
//		RegisterLoginResponseVO respVO = flickzzDeskService.register(request);
//
//		log.debug(generateLog(EXIT, this.getClass().getName()));
//		return handleSuccessResponse(REGISTRATION_SUCCESS, getDescription(REGISTRATION_SUCCESS.getDescription(), LOGIN),
//				respVO);
//	}

	@PostMapping("verify")
	public ResponseEntity<FlickzzDeskResponse> verifyCode(@RequestBody VerificationRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		RegisterLoginResponseVO respVO = flickzzDeskService.verifyCode(request);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(REGISTRATION_SUCCESS, getDescription(REGISTRATION_SUCCESS.getDescription(), LOGIN),
				respVO);
	}

	@PostMapping("login")
	public ResponseEntity<FlickzzDeskResponse> userLogin(@RequestBody RegisterLoginRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		RegisterLoginResponseVO respVO = flickzzDeskService.userLogin(request);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(REGISTRATION_SUCCESS, getDescription(REGISTRATION_SUCCESS.getDescription(), LOGIN),
				respVO);
	}

	@PostMapping("refresh")
	public ResponseEntity<FlickzzDeskResponse> authRefresh(@RequestBody CommonRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		LoginResponseVO respVO = flickzzDeskService.authRefresh(request);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(TOKEN_SUCCESS, TOKEN_SUCCESS.getDescription(), respVO);
	}

	@PostMapping("/logoutAll")
	public ResponseEntity<FlickzzDeskResponse> logoutAll(@RequestBody CommonRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		flickzzDeskService.logoutAllUsers(request);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(LOGOUT_ALL_SUCCESS,
				getDescription(LOGOUT_ALL_SUCCESS.getDescription(), request.getUsername()));
	}

	@PostMapping("reset/password")
	public ResponseEntity<FlickzzDeskResponse> resetPassword(@RequestBody RegisterLoginRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		flickzzDeskService.resetPassword(request);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(PASSWORD_RESET_SUCCESS, PASSWORD_RESET_SUCCESS.getDescription());
	}

	@GetMapping("user/list")
	public ResponseEntity<FlickzzDeskResponse> getUserList() {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		List<UserVO> respVO = flickzzDeskService.getUserList();

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), USER_LIST), respVO);
	}

	@GetMapping("user/info")
	public ResponseEntity<FlickzzDeskResponse> getUserInfo(@RequestBody CommonRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		UserVO respVO = flickzzDeskService.getUserInfo(request);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), LOGIN), respVO);
	}
}
