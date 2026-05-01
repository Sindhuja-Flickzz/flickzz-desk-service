package com.flickzz.desk.config;

import java.security.SecureRandom;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.flickzz.desk.security.CustomUserDetails;

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
}
