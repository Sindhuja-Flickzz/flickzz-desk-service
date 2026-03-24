package com.flickzz.desk.config;

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
}
