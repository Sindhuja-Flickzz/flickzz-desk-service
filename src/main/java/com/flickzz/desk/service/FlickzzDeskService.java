package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.FD_USER;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DEFAULT_ERROR_CODE;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DOES_NOT_EXIST;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.INCORRECT_CODE;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.INVALID_CREDENTIALS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.Auth;
import com.flickzz.desk.model.LoginMaster;
import com.flickzz.desk.model.User;
import com.flickzz.desk.repo.LoginMasterRepository;
import com.flickzz.desk.repo.UserRepository;
import com.flickzz.desk.security.JwtUtil;
import com.flickzz.desk.security.TwoFactorAuthenticationService;
import com.flickzz.desk.vo.CommonRequestVO;
import com.flickzz.desk.vo.LoginResponseVO;
import com.flickzz.desk.vo.RegisterLoginRequestVO;
import com.flickzz.desk.vo.RegisterLoginResponseVO;
import com.flickzz.desk.vo.VerificationRequestVO;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

@Service
public class FlickzzDeskService {

	private static final Logger log = LoggerFactory.getLogger(FlickzzDeskService.class);

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LoginMasterRepository loginMasterRepository;
		
	@Autowired
	private RefreshTokenService refreshTokenService;
	
	@Autowired
	private TwoFactorAuthenticationService tfaService;
	
	@Autowired
	private CommonMapper mapper;
	
	public RegisterLoginResponseVO register(RegisterLoginRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			var user = mapper.registerRequesttoUser(request);

			 GoogleAuthenticatorKey key = tfaService.generateNewSecret();
		        // if MFA enabled --> Generate Secret
		        if (request.getMfaEnabled()) {
		            user.setSecret(key.getKey());
		        }
		        userRepository.save(user);
		        
		        LoginMaster loginMaster = mapper.userToLoginMaster(user);
		        loginMasterRepository.save(loginMaster);
		        
		        var jwtToken = jwtUtil.generateToken(user.getUserName());
		        var refreshToken = refreshTokenService.createRefreshToken(user, false).getToken(); // keepMeLoggedIn is false to have short duration for token
		        return RegisterLoginResponseVO.builder()
		                .secretImageUri(tfaService.generateQrCodeImageUri(key, user.getUserName()))
		                .accessToken(jwtToken)
		                .refreshToken(refreshToken)
		                .mfaEnabled(user.isMfaEnabled())
						.userRole(user.getRole())
		                .build();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in register method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}	
		 
	}

    public RegisterLoginResponseVO verifyCode(VerificationRequestVO verificationRequestVO) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			User user = userRepository.findByUserName(verificationRequestVO.getEmail())
	        		.orElseThrow(()-> new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), FD_USER)));
	        
	        if (!tfaService.verifyCode(user.getSecret(), verificationRequestVO.getCode())) {
	        	throw new FlickzzDeskException(INVALID_CREDENTIALS, INVALID_CREDENTIALS.getDescription());
	        }
	        var jwtToken = jwtUtil.generateToken(user.getUserName());
	        var refreshToken = refreshTokenService.createRefreshToken(user, false).getToken();
	        return RegisterLoginResponseVO.builder()
	                .accessToken(jwtToken)
	                .refreshToken(refreshToken)
	                .mfaEnabled(user.isMfaEnabled())
					.userRole(user.getRole())
	                .build();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in register method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
    }

    public RegisterLoginResponseVO userLogin(RegisterLoginRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			 
	        var user = userRepository.findByUserName(request.getEmail())
	                .orElseThrow(()-> new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), FD_USER)));
	        
	        if(!user.getPassword().equals(user.getPassword())) {
	        	throw new FlickzzDeskException(INCORRECT_CODE, INCORRECT_CODE.getDescription());
	        }
	        
	        if (user.isMfaEnabled()) {
	            return RegisterLoginResponseVO.builder()
	                    .accessToken("")
	                    .refreshToken("")
	                    .mfaEnabled(true)
						.userRole(user.getRole())
	                    .build();
	        }
	        var jwtToken = jwtUtil.generateToken(user.getUserName());
	        var refreshToken = refreshTokenService.createRefreshToken(user, false).getToken();
	        return RegisterLoginResponseVO.builder()
	                .accessToken(jwtToken)
	                .refreshToken(refreshToken)
	                .mfaEnabled(false)
	                .build();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (AuthenticationException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in register method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
    }
    
	public LoginResponseVO authRefresh(CommonRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		LoginResponseVO responseVO = new LoginResponseVO();
		try {
		    Auth auth = refreshTokenService.validateRefreshToken(request.getRefreshToken());
	
		    String newAccessToken = jwtUtil.generateToken(auth.getUser().getUserName());
	
		    responseVO.setAccessToken(newAccessToken);
	        responseVO.setRefreshToken(auth.getToken());
	        return responseVO;
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in authRefresh method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}	
	}
	
	public void logoutUser(CommonRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
	        refreshTokenService.revokeRefreshToken(request.getRefreshToken());
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in logoutUser method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}		
	}
	
	public void logoutAllUsers(CommonRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			String username = request.getUsername();
	        User user = userRepository.findByUserName(username)
	                .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), FD_USER)));
	        refreshTokenService.revokeAllTokensForUser(user);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in logoutAllUsers method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}		
	}
}
