package com.flickzz.desk.security;

import org.springframework.stereotype.Service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TwoFactorAuthenticationService {
	
	private final GoogleAuthenticator googleAuthenticator;
	
	public TwoFactorAuthenticationService() {
		GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
				.setTimeStepSizeInMillis(30000) //30 seconds
				.setWindowSize(3) //Allowable time window
				.build();
		this.googleAuthenticator = new GoogleAuthenticator(config);
	}

	public GoogleAuthenticatorKey generateNewSecret() {
		return googleAuthenticator.createCredentials();
//		GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
//		return key.getKey();
	}

	public String generateQrCodeImageUri(GoogleAuthenticatorKey key, String username) {
		return GoogleAuthenticatorQRGenerator.getOtpAuthURL("FlickzzDesk", username, key);
	}

	public boolean verifyCode(String secret, int verificationCode) {
		return googleAuthenticator.authorize(secret, verificationCode);
	}
}
