package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.ACTIVE;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.flickzz.desk.model.Auth;
import com.flickzz.desk.model.EnquiryRegistration;
import com.flickzz.desk.model.User;
import com.flickzz.desk.repo.AuthRepository;
import com.flickzz.desk.repo.EnquiryRegistrationRepository;
import com.flickzz.desk.repo.UserRepository;

@Service
public class RefreshTokenService {

	@Value("${jwt.refresh.expiration}")
	private Long refreshTokenDuration;

	@Value("${jwt.refresh.expiration.extended}")
	private Long refreshTokenExtendedDuration;

	@Autowired
	private AuthRepository authRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EnquiryRegistrationRepository enquiryRegistrationRepository;

	public Auth createRefreshToken(User user, boolean keepMeLoggedIn) {
		User fDUser = userRepository.findByUserName(user.getUserName())
				.orElseThrow(() -> new RuntimeException("User not found"));

		// Refresh token lifetime depends on "keep me logged in"
		long duration = keepMeLoggedIn ? refreshTokenExtendedDuration : refreshTokenDuration; // 30 days vs 1 day

		Auth fDAuth = new Auth();
		fDAuth.setUser(fDUser);
		fDAuth.setExpiresAt(new Date(System.currentTimeMillis() + duration * 1000));
		fDAuth.setToken(UUID.randomUUID().toString());
		fDAuth.setCreatedBy(fDUser.getRole());

		return authRepository.save(fDAuth);
	}

	public Auth createRefreshToken(EnquiryRegistration enquiryRegistration, boolean keepMeLoggedIn) {
		EnquiryRegistration registration = enquiryRegistrationRepository
				.findByUserNameAndIsActive(enquiryRegistration.getUserName(), ACTIVE)
				.orElseThrow(() -> new RuntimeException("User not found"));

		// Refresh token lifetime depends on "keep me logged in"
		long duration = keepMeLoggedIn ? refreshTokenExtendedDuration : refreshTokenDuration; // 30 days vs 1 day

		Auth fDAuth = new Auth();
		fDAuth.setEnquiryRegistration(enquiryRegistration);
		fDAuth.setExpiresAt(new Date(System.currentTimeMillis() + duration * 1000));
		fDAuth.setToken(UUID.randomUUID().toString());
		fDAuth.setCreatedBy(enquiryRegistration.getUserRole());

		return authRepository.save(fDAuth);
	}

	public Auth validateRefreshToken(String token) {
		Auth refreshToken = authRepository.findByToken(token)
				.orElseThrow(() -> new RuntimeException("Invalid refresh token"));

		if (refreshToken.getExpiresAt().before(new Date())) {
			throw new RuntimeException("Refresh token expired");
		}
		return refreshToken;
	}

	public void revokeRefreshToken(String token) {
		authRepository.findByToken(token).ifPresent(authRepository::delete);
	}

	public void revokeAllTokensForUser(User user) {
		authRepository.deleteAll(authRepository.findAll().stream()
				.filter(rt -> rt.getUser().getUserId().equals(user.getUserId())).toList());
	}
}
