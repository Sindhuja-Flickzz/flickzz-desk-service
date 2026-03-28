package com.flickzz.desk.config;
import lombok.Getter;

@Getter
public enum FlickzzDeskSuccessCodes {

	REGISTRATION_SUCCESS("GM-200", "Registration Successful", "%s Successful!"),
	LOGIN_SUCCESS("GM-201", "Login Successful", "%s Successful!"),
	TOKEN_SUCCESS("GM-202", "Auth token created Successfully", "Auth token created successfully!"),
	LOGOUT_SUCCESS("GM-203", "Logout Successful", "Logged out successfully. Refresh token revoked."),
	LOGOUT_ALL_SUCCESS("GM-204", "Logout Successful", "All refresh tokens revoked for user %s"),
	FETCH_SUCCESS("GM-205", "Fetch Successful", "%s fetch successful!"),
	CREATE_SUCCESS("GM-206", "Create Successful", "%s created successfully!"),
	UPDATE_SUCCESS("GM-207", "Update Successful", "%s updated successfully!"),
	DELETE_SUCCESS("GM-208", "Delete Successful", "%s deleted successfully!");

	private String code;
	private String title;
	private String description;

	FlickzzDeskSuccessCodes(String code, String title, String description) {
		this.code = code;
		this.title = title;
		this.description = description;
	}
}
