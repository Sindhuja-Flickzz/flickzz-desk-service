package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.ACTIVE;
import static com.flickzz.desk.config.FlickzzDeskConstants.EMAIL;
import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.FD_USER;
import static com.flickzz.desk.config.FlickzzDeskConstants.PASSWORD;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DEFAULT_ERROR_CODE;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DOES_NOT_EXIST;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.INCORRECT_PASSWORD;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.INVALID_TEXT;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.TFA_ERROR;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.AgentMaster;
import com.flickzz.desk.model.Auth;
import com.flickzz.desk.model.EnquiryRegistration;
import com.flickzz.desk.model.User;
import com.flickzz.desk.repo.AgentMasterRepository;
import com.flickzz.desk.repo.CityMasterRepository;
import com.flickzz.desk.repo.CountryMasterRepository;
import com.flickzz.desk.repo.EnquiryRegistrationRepository;
import com.flickzz.desk.repo.LanguageMasterRepository;
import com.flickzz.desk.repo.LoginMasterRepository;
import com.flickzz.desk.repo.UserRepository;
import com.flickzz.desk.security.JwtUtil;
import com.flickzz.desk.security.TwoFactorAuthenticationService;
import com.flickzz.desk.vo.CommonRequestVO;
import com.flickzz.desk.vo.LoginResponseVO;
import com.flickzz.desk.vo.RegisterLoginRequestVO;
import com.flickzz.desk.vo.RegisterLoginResponseVO;
import com.flickzz.desk.vo.UserVO;
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
	private AgentMasterRepository agentMasterRepository;

	@Autowired
	private TwoFactorAuthenticationService tfaService;

	@Autowired
	private CountryMasterRepository countryMasterRepository;

	@Autowired
	private CityMasterRepository cityMasterRepository;

	@Autowired
	private LanguageMasterRepository languageMasterRepository;

	@Autowired
	private EnquiryRegistrationRepository enquiryRegistrationRepository;

	@Autowired
	private CommonMapper mapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

//	public RegisterLoginResponseVO register(RegisterLoginRequestVO request) {
//		log.debug(generateLog(ENTRY, this.getClass().getName()));
//		try {
//			Optional<User> userCheck = userRepository.findByUserName(request.getEmail());
//			if (userCheck.isPresent()) {
//				throw new FlickzzDeskException(ALREADY_EXISTS,
//						getDescription(ALREADY_EXISTS.getDescription(), USERNAME_OR_EMAIL));
//			}
//
//			CountryMaster country = countryMasterRepository.findById(request.getCountryId())
//					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
//							getDescription(DOES_NOT_EXIST.getDescription(), COUNTRY)));
//
//			CityMaster city = cityMasterRepository.findById(request.getCityId())
//					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
//							getDescription(DOES_NOT_EXIST.getDescription(), CITY)));
//
//			LanguageMaster language = languageMasterRepository.findById(request.getLanguageId())
//					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
//							getDescription(DOES_NOT_EXIST.getDescription(), LANGUAGE)));
//
//			userRepository.findByPhone(request.getPhone()).ifPresent(u -> {
//				throw new FlickzzDeskException(ALREADY_EXISTS,
//						getDescription(ALREADY_EXISTS.getDescription(), PHONE_NUMBER));
//			});
//
//			String rawPassword = request.getPassword();
//			var user = mapper.registerRequesttoUser(request, rawPassword, country, city, language);
//
//			GoogleAuthenticatorKey key = tfaService.generateNewSecret();
//			user.setSecret(key.getKey());
//			userRepository.save(user);
//
//			LoginMaster loginMaster = mapper.userToLoginMaster(user);
//			loginMasterRepository.save(loginMaster);
//
//			var jwtToken = jwtUtil.generateToken(user.getUserName());
//			var refreshToken = refreshTokenService.createRefreshToken(user, false).getToken();
//			return RegisterLoginResponseVO.builder()
//					.secretImageUri(tfaService.generateQrCodeImageUri(key, user.getUserName())).accessToken(jwtToken)
//					.refreshToken(refreshToken).mfaEnabled(user.isMfaEnabled()).userRole(user.getRole()).build();
//		} catch (FlickzzDeskException e) {
//			throw e;
//		} catch (Exception e) {
//			log.error("Exception in register method in FlickzzDeskService");
//			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
//		}
//
//	}

	public RegisterLoginResponseVO verifyCode(VerificationRequestVO verificationRequestVO) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			User user = userRepository.findByUserName(verificationRequestVO.getEmail())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), FD_USER)));

			if (!tfaService.verifyCode(user.getSecret(), verificationRequestVO.getCode())) {
				throw new FlickzzDeskException(TFA_ERROR, TFA_ERROR.getDescription());
			}
			var jwtToken = jwtUtil.generateToken(user.getUserName());
			var refreshToken = refreshTokenService.createRefreshToken(user, false).getToken();
			user.setMfaEnabled(true);

			AgentMaster agent = agentMasterRepository.findByUser(user);
			userRepository.save(user);
			return RegisterLoginResponseVO.builder().accessToken(jwtToken).refreshToken(refreshToken)
					.mfaEnabled(user.isMfaEnabled()).userRole(user.getRole())
					.userOrganization(agent != null ? agent.getOrganization().getCompanyId() : null).build();
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

			if (request.getEmail() == null || request.getEmail().isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), FD_USER));
			}

			if (request.getPassword() == null || request.getPassword().isEmpty()) {
				throw new FlickzzDeskException(INCORRECT_PASSWORD);
			}

			EnquiryRegistration enquiryRegistration = enquiryRegistrationRepository
					.findByEmailAndIsActive(request.getEmail(), ACTIVE).orElse(null);

			if (enquiryRegistration != null) {
				if (!passwordEncoder.matches(request.getPassword(), enquiryRegistration.getPassword())) {
					throw new FlickzzDeskException(INVALID_TEXT,
							getDescription(INVALID_TEXT.getDescription(), PASSWORD));
				}

				var jwtToken = jwtUtil.generateToken(enquiryRegistration.getUserName());
				var refreshToken = refreshTokenService.createRefreshToken(enquiryRegistration, false).getToken();
				return RegisterLoginResponseVO.builder().accessToken(jwtToken).refreshToken(refreshToken)
						.isEnquiryUser(Boolean.TRUE).mfaEnabled(Boolean.TRUE)
						.userRole(enquiryRegistration.getUserRole()).build();
			}

			var user = userRepository.findByUserName(request.getEmail()).orElseThrow(
					() -> new FlickzzDeskException(INVALID_TEXT, getDescription(INVALID_TEXT.getDescription(), EMAIL)));

			if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
				throw new FlickzzDeskException(INVALID_TEXT, getDescription(INVALID_TEXT.getDescription(), PASSWORD));
			}

			if (!user.isMfaEnabled()) {
				GoogleAuthenticatorKey key = tfaService.generateNewSecret();
				user.setSecret(key.getKey());
				userRepository.save(user);
				return RegisterLoginResponseVO.builder()
						.secretImageUri(tfaService.generateQrCodeImageUri(key, user.getUserName()))
						.isEnquiryUser(Boolean.FALSE).mfaEnabled(user.isMfaEnabled()).userRole(user.getRole()).build();
			}
			var jwtToken = jwtUtil.generateToken(user.getUserName());
			var refreshToken = refreshTokenService.createRefreshToken(user, false).getToken();
			return RegisterLoginResponseVO.builder().accessToken(jwtToken).refreshToken(refreshToken)
					.isEnquiryUser(Boolean.FALSE).mfaEnabled(user.isMfaEnabled()).userRole(user.getRole()).build();
		} catch (FlickzzDeskException | AuthenticationException e) {
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
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			User user = userRepository.findByUserName(request.getEmail())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), FD_USER)));

			if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
				throw new FlickzzDeskException(INCORRECT_PASSWORD);
			}

			user.setPassword(passwordEncoder.encode(request.getPassword()));
			userRepository.save(user);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in resetPassword method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<UserVO> getUserList() {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			var users = userRepository.findAllByIsActive(ACTIVE);
			return mapper.usersToUserVO(users);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getUserList method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public UserVO getUserInfo(CommonRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			User user = userRepository.findByUserName(request.getUsername())
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
}
