package com.flickzz.desk.exception;

import lombok.Getter;

@Getter
public enum FlickzzDeskErrorCodes implements ErrorCode {

	DEFAULT_ERROR_CODE("FD-100", "Error Occured", "An error occured. Please contact support with the error ID"),
	DOES_NOT_EXIST("FD-101", "Not found", "%s doesn't exist. Please verify!"),
	INACTIVE_ERROR("FD-102", "Not active","%s is not active"),
	INVALID_CREDENTIALS("FD-103", "Invalid credentials", "Sorry, you have entered an invalid Username or password. Please try again."),
	ALREADY_EXISTS("FD-104", "Already exists", "%s alreary exists. Please try again!"),
//	NOT_UNIQUE("FD-105", "Not Unique", "Not unique field. Please verify!"),
	INCORRECT_CODE("FD-106", "Incorrect Code", "Code is not correct. Please retry!"),
	INVALID_FIELD("FD-107", "Not a valid value", "Value for %s is not valid. Please verify!");

	private String code;
	private String title;
	private String description;

	FlickzzDeskErrorCodes(String code, String title, String description) {
		this.code = code;
		this.title = title;
		this.description = description;
	}
}
