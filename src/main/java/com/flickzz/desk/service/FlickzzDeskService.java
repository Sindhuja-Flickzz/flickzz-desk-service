package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.*;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

import java.util.*;

import com.flickzz.desk.vo.request.CommonRequestVO;
import com.flickzz.desk.vo.request.RegisterLoginRequestVO;
import com.flickzz.desk.vo.request.VerificationRequestVO;
import com.flickzz.desk.vo.response.LoginResponseVO;
import com.flickzz.desk.vo.response.RegisterLoginResponseVO;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;

import com.flickzz.desk.exception.*;
import com.flickzz.desk.mapper.*;
import com.flickzz.desk.model.*;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.security.*;
import com.flickzz.desk.vo.*;
import com.warrenstrange.googleauth.*;
import tools.jackson.databind.ObjectMapper;

@Service
public class FlickzzDeskService {

	private static final Logger log = LoggerFactory.getLogger(FlickzzDeskService.class);

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RefreshTokenService refreshTokenService;

	@Autowired
	private AgentMasterRepository agentMasterRepository;

	@Autowired
	private TwoFactorAuthenticationService tfaService;

	@Autowired
	private EnquiryRegistrationRepository enquiryRegistrationRepository;

	@Autowired
	private CommonMapper mapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuditService auditService;

	@Autowired
	private ObjectMapper objectMapper;

		public RegisterLoginResponseVO verifyCode(VerificationRequestVO verificationRequestVO) {
		log.info(generateLog("verifyCode", this.getClass().getName()));
		try {
			User user = userRepository.findByUserNameAndIsActiveTrue(verificationRequestVO.getEmail())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), FD_USER)));

			if (!tfaService.verifyCode(user.getSecret(), verificationRequestVO.getCode())) {
				throw new FlickzzDeskException(TFA_ERROR, TFA_ERROR.getDescription());
			}
			var jwtToken = jwtUtil.generateToken(user.getUserName());
			var refreshToken = refreshTokenService.createRefreshToken(user, false).getToken();
			user.setMfaEnabled(true);

			AgentMaster agent = agentMasterRepository.findByUserAndIsActiveTrue(user);
			userRepository.save(user);

				// Audit: successful TFA verification / login
				Map<String, Object> changed = new HashMap<>();
				changed.put("mfaEnabled", true);
				String changedStr = null;
				try {
					changedStr = objectMapper.writeValueAsString(changed);
				} catch (Exception ignore) {
				}
				auditService.recordAudit(mapper.toSystemAuditRequest("Auth", "TFA", "User",
						user.getUserId(),
						"VERIFY_CODE",
						null,
						null,
						changedStr,
						user.getUserId(),
						user.getUserName(),
						null,
						"SUCCESS",
						null));

				return generateLoginResponse(jwtToken, refreshToken, Boolean.FALSE, user.isMfaEnabled(),
						agent != null ? agent.getOrganization() : null, user.getRole(), null, user.getUserId());
			} catch (FlickzzDeskException e) {
				auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Auth", "TFA", "User",
						null, "VERIFY_CODE", null, null, null, null, null, null, "FAILED", e.getMessage()), e);
				throw e;
			} catch (Exception e) {
				auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Auth", "TFA", "User",
						null, "VERIFY_CODE", null, null, null, null, null, null, "FAILED", e.getMessage()), e);
				log.error("Exception in register method in FlickzzDeskService");
				throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
			}
		}

	public RegisterLoginResponseVO userLogin(RegisterLoginRequestVO request) {
		log.info(generateLog("userLogin", this.getClass().getName()));
		try {

			if (request.getEmail() == null || request.getEmail().isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), FD_USER));
			}

			if (request.getPassword() == null || request.getPassword().isEmpty()) {
				throw new FlickzzDeskException(INCORRECT_PASSWORD);
			}

			EnquiryRegistration enquiryRegistration = enquiryRegistrationRepository
					.findTopByEmailAndIsActiveTrueOrderByVersionDesc(request.getEmail()).orElse(null);

			if (enquiryRegistration != null) {
				if (!passwordEncoder.matches(request.getPassword(), enquiryRegistration.getPassword())) {
					throw new FlickzzDeskException(INVALID_TEXT,
							getDescription(INVALID_TEXT.getDescription(), PASSWORD));
				}

				var jwtToken = jwtUtil.generateToken(enquiryRegistration.getUserName());
				var refreshToken = refreshTokenService.createRefreshToken(enquiryRegistration, false).getToken();
				RegisterLoginResponseVO resp = generateLoginResponse(jwtToken, refreshToken, Boolean.TRUE, Boolean.TRUE,
						enquiryRegistration.getCompany(), enquiryRegistration.getUserRole(), null,
						enquiryRegistration.getEnquiryId());

				// Audit: enquiry login success
				Map<String, Object> changed = new HashMap<>();
				changed.put("loginType", "ENQUIRY");
				changed.put("userName", enquiryRegistration.getUserName());
				String changedStr = null;
				try {
					changedStr = objectMapper.writeValueAsString(changed);
				} catch (Exception ignore) {
				}
				auditService.recordAudit(mapper.toSystemAuditRequest("Auth", "Login", "EnquiryRegistration",
							enquiryRegistration.getEnquiryId(),
							"LOGIN",
							null,
							null,
							changedStr,
							enquiryRegistration.getEnquiryId(),
							enquiryRegistration.getUserName(),
							enquiryRegistration.getCompany() != null ? enquiryRegistration.getCompany().getCompanyId() : null,
							"SUCCESS",
							null));

				return resp;
			}

			var user = userRepository.findByUserNameAndIsActiveTrue(request.getEmail()).orElseThrow(
					() -> new FlickzzDeskException(INVALID_TEXT, getDescription(INVALID_TEXT.getDescription(), EMAIL)));

			if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
				throw new FlickzzDeskException(INVALID_TEXT, getDescription(INVALID_TEXT.getDescription(), PASSWORD));
			}

			if (!user.isMfaEnabled()) {
				GoogleAuthenticatorKey key = tfaService.generateNewSecret();
				user.setSecret(key.getKey());
				userRepository.save(user);
				RegisterLoginResponseVO resp = generateLoginResponse(null, null, Boolean.FALSE, user.isMfaEnabled(), null, user.getRole(),
						tfaService.generateQrCodeImageUri(key, user.getUserName()), user.getUserId());

				// Audit: MFA setup required
				Map<String, Object> changed = new HashMap<>();
				changed.put("mfaSetup", true);
				changed.put("userName", user.getUserName());
				String changedStr = null;
				try {
					changedStr = objectMapper.writeValueAsString(changed);
				} catch (Exception ignore) {
				}
				auditService.recordAudit(mapper.toSystemAuditRequest("Auth", "Login", "User",
							user.getUserId(),
							"LOGIN_MFA_SETUP",
							null,
							null,
							changedStr,
							user.getUserId(),
							user.getUserName(),
							null,
							"SUCCESS",
							null));

				return resp;
			}

			AgentMaster agent = agentMasterRepository.findByUserAndIsActiveTrue(user);

			var jwtToken = jwtUtil.generateToken(user.getUserName());
			var refreshToken = refreshTokenService.createRefreshToken(user, false).getToken();
			RegisterLoginResponseVO resp = generateLoginResponse(jwtToken, refreshToken, Boolean.FALSE, user.isMfaEnabled(),
								agent != null ? agent.getOrganization() : null, user.getRole(), null, user.getUserId());

			// Audit: user login success
			Map<String, Object> changed = new HashMap<>();
			changed.put("loginType", "USER");
			changed.put("userName", user.getUserName());
			String changedStr = null;
			try {
				changedStr = objectMapper.writeValueAsString(changed);
			} catch (Exception ignore) {
			}
			auditService.recordAudit(mapper.toSystemAuditRequest("Auth", "Login", "User",
					user.getUserId(),
					"LOGIN",
					null,
					null,
					changedStr,
					user.getUserId(),
					user.getUserName(),
					agent != null && agent.getOrganization() != null ? Long.valueOf(agent.getOrganization().getCompanyId()) : null,
					"SUCCESS",
					null));

			return resp;
		} catch (FlickzzDeskException | AuthenticationException e) {
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Auth", "Login", "User",
					null, "LOGIN", null, null, null, null, null, null, "FAILED", e.getMessage()), e);
			throw e;
		} catch (Exception e) {
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Auth", "Login", "User",
					null, "LOGIN", null, null, null, null, null, null, "FAILED", e.getMessage()), e);
			log.error("Exception in register method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public LoginResponseVO authRefresh(CommonRequestVO request) {
		log.info(generateLog("authRefresh", this.getClass().getName()));
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
		log.info(generateLog("logoutUser", this.getClass().getName()));
		try {
			refreshTokenService.revokeRefreshToken(request.getRefreshToken());
			// Audit logout
			Map<String, Object> changed = new HashMap<>();
			changed.put("refreshTokenRevoked", true);
			String changedStr = null;
			try {
				changedStr = objectMapper.writeValueAsString(changed);
			} catch (Exception ignore) {
			}
			auditService.recordAudit(mapper.toSystemAuditRequest("Auth", "Logout", "Auth",
					null,
					"LOGOUT",
					null,
					null,
					changedStr,
					0L,
					request.getUsername(),
					null,
					"SUCCESS",
					null));
		} catch (FlickzzDeskException e) {
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Auth", "Logout", "Auth",
					null, "LOGOUT", null, null, null, 0L, request != null ? request.getUsername() : null, null, "FAILED", e.getMessage()), e);
			throw e;
		} catch (Exception e) {
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Auth", "Logout", "Auth",
					null, "LOGOUT", null, null, null, 0L, request != null ? request.getUsername() : null, null, "FAILED", e.getMessage()), e);
			log.error("Exception in logoutUser method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void logoutAllUsers(CommonRequestVO request) {
		log.info(generateLog("logoutAllUsers", this.getClass().getName()));
		try {
			String username = request.getUsername();
			User user = userRepository.findByUserNameAndIsActiveTrue(username)
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), FD_USER)));
			refreshTokenService.revokeAllTokensForUser(user);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in logoutAllUsers method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void resetPassword(RegisterLoginRequestVO request) {
		log.info(generateLog("resetPassword", this.getClass().getName()));
		try {
			User user = userRepository.findByUserNameAndIsActiveTrue(request.getEmail())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), FD_USER)));
			user.setPassword(passwordEncoder.encode(request.getPassword()));
			userRepository.save(user);

			// Audit password reset
			Map<String, Object> changed = new HashMap<>();
			changed.put("passwordReset", true);
			changed.put("userName", user.getUserName());
			String changedStr = null;
			try {
				changedStr = objectMapper.writeValueAsString(changed);
			} catch (Exception ignore) {
			}
			auditService.recordAudit(mapper.toSystemAuditRequest("Auth", "Password", "User",
					user.getUserId(),
					"RESET_PASSWORD",
					null,
					null,
					changedStr,
					user.getUserId(),
					user.getUserName(),
					null,
					"SUCCESS",
					null));
		} catch (FlickzzDeskException e) {
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Auth", "Password", "User",
					null, "RESET_PASSWORD", null, null, null, 0L, null, null, "FAILED", e.getMessage()), e);
			throw e;
		} catch (Exception e) {
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Auth", "Password", "User",
					null, "RESET_PASSWORD", null, null, null, 0L, null, null, "FAILED", e.getMessage()), e);
			log.error("Exception in resetPassword method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<UserVO> getUserList() {
		log.info(generateLog("getUserList", this.getClass().getName()));
		try {
			var users = userRepository.findAll();
			return mapper.usersToUserVO(users);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getUserList method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public UserVO getUserInfo(String userEmail) {
		log.info(generateLog("getUserInfo", this.getClass().getName()));
		try {
			User user = userRepository.findByUserNameAndIsActiveTrue(userEmail)
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), FD_USER)));
			return mapper.userToUserVO(user);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getUserInfo method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

    public UserVO getUserInfoById(Long userId) {
			log.info(generateLog("getUserInfoById", this.getClass().getName()));
			try {
				User user = userRepository.findById(userId)
						.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
								getDescription(DOES_NOT_EXIST.getDescription(), FD_USER)));
				return mapper.userToUserVO(user);
			} catch (FlickzzDeskException e) {
				throw e;
			} catch (Exception e) {
				log.error("Exception in getUserInfoById method in FlickzzDeskService");
				throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
			}
		}
	}
