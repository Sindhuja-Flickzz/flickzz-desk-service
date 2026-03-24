package com.flickzz.desk.vo;

import com.flickzz.desk.config.FlickzzDeskSuccessResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeneralRespVO {
	
	private String successCode;
	private FlickzzDeskSuccessResponse response;
    private Object object;

	public GeneralRespVO(String successCode, FlickzzDeskSuccessResponse response) {
		this.successCode = successCode;
		this.response = response;
	}
}
