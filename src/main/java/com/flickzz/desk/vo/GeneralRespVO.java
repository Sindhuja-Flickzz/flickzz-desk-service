package com.flickzz.desk.vo;

import java.io.Serializable;

import com.flickzz.desk.config.FlickzzDeskSuccessResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeneralRespVO implements Serializable {
    private static final long serialVersionUID = 1L;
	private String successCode;
	private FlickzzDeskSuccessResponse response;
    private Object attributes;

	public GeneralRespVO(String successCode, FlickzzDeskSuccessResponse response) {
		this.successCode = successCode;
		this.response = response;
	}
}
