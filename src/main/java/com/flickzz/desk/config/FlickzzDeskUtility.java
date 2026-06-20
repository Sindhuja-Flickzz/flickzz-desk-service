package com.flickzz.desk.config;

import java.security.*;

import org.springframework.security.core.*;
import org.springframework.security.core.context.*;

import com.flickzz.desk.model.*;
import com.flickzz.desk.security.*;
import com.flickzz.desk.vo.*;

public class FlickzzDeskUtility {

	public static String generateLog(String methodName, String className) {
		if (methodName.equalsIgnoreCase(FlickzzDeskConstants.ENTRY))
			return "Entered " + methodName + " method in " + className;
		else
			return "Exit " + methodName + " method in " + className;
	}

	public static String getDescription(String template, Object... params) {
		return String.format(template, params);
	}

	public static CustomUserDetails getUserDetails() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
		return user;
	}

	public static String generateTemporaryPassword() {
		SecureRandom secureRandom = new SecureRandom();
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
		StringBuilder password = new StringBuilder(12);
		for (int i = 0; i < 12; i++) {
			password.append(characters.charAt(secureRandom.nextInt(characters.length())));
		}
		return password.toString();
	}

	public static String generateUniversalId(String uidPrefix, String currentUID) {
		String yearSuffix = String.valueOf(java.time.Year.now().getValue()).substring(2);
		int sequence = 0;

		if (currentUID != null && currentUID.length() > 1) {
			String sequenceStr = currentUID.substring(uidPrefix.length() + yearSuffix.length());
			sequence = Integer.parseInt(sequenceStr);
		}
		int nextSequence = sequence + 1;
		return uidPrefix + yearSuffix + nextSequence;
	}

	public static RegisterLoginResponseVO generateLoginResponse(String jwtToken, String refreshToken,
			Boolean isEnquiryUser, Boolean mfaEnabled, CompanyMaster company, String userRole, String qrCodeImageUri,
			Long userId) {
		return RegisterLoginResponseVO.builder().accessToken(jwtToken != null ? jwtToken : "")
				.refreshToken(refreshToken != null ? refreshToken : "").isEnquiryUser(isEnquiryUser)
				.mfaEnabled(mfaEnabled).userOrgId(company != null ? company.getCompanyId() : 0)
				.userOrgName(company != null ? company.getCompanyName() : "").userRole(userRole != null ? userRole : "")
				.secretImageUri(qrCodeImageUri).userId(userId).build();
	}

}
