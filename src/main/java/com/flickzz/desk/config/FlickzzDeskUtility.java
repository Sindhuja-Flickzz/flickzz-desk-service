package com.flickzz.desk.config;

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
}
