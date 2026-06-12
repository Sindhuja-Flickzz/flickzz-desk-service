package com.flickzz.desk.exception;

import lombok.Getter;

@Getter
public enum FlickzzDeskErrorCodes implements ErrorCode {

	DEFAULT_ERROR_CODE("FD-100", "Error Occured", "An error occured. Please contact support with the error ID"),
	DOES_NOT_EXIST("FD-101", "Not found", "%s doesn't exist. Please verify!"),
	INACTIVE_ERROR("FD-102", "Not active", "%s is not active"),
	INVALID_CREDENTIALS("FD-103", "Invalid credentials",
			"Sorry, you have entered an invalid Username or password. Please try again."),
	ALREADY_EXISTS("FD-104", "Already exists", "%s alreary exists. Please try again!"),
	TFA_ERROR("FD-105", "Code Error", "Code is not valid. Please verify!"),
	INCORRECT_CODE("FD-106", "Incorrect Code", "Code is not correct. Please retry!"),
	INVALID_FIELD("FD-107", "Not a valid value", "Value for %s is not valid. Please verify!"),
	NO_DATA("FD-108", "No data found", "No data found. Please verify!"),
	INCORRECT_PASSWORD("FD-109", "Incorrect Password", "Current password is incorrect. Please verify!"),
	INVALID_TOKEN("FD-110", "Token Invalid", "Token Invalid or Expired!"),
	EXPIRED_LINK("FD-110", "Link Expired", "Link Expired! Please raise enquiry again."),
	INVALID_PASSWORD("FD-111", "Invalid Password",
			"Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character."),
	INVALID_TEXT("FD-112", "Invalid Text", "Invalid %s. Please verify!"),
	SET_TEXT("FD-113", "Set Text", "Set %s to proceed further!");

	private String code;
	private String title;
	private String description;

	FlickzzDeskErrorCodes(String code, String title, String description) {
		this.code = code;
		this.title = title;
		this.description = description;
	}
}
