package com.flickzz.desk.exception;

public class FlickzzDeskException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final FlickzzDeskErrorCodes errorCode;
	private final String description;
	
	public FlickzzDeskException(FlickzzDeskErrorCodes errorCode) {
		super(errorCode.getTitle());
		this.errorCode = errorCode;
		this.description = "";
	}
	
	public FlickzzDeskException(FlickzzDeskErrorCodes errorCode, String description) {
		super(errorCode.getTitle());
		this.errorCode = errorCode;
		this.description = description;
	}
	
	public FlickzzDeskErrorCodes getErrorCode() { return errorCode; } 
	public String getDescription() { return description; }
}
