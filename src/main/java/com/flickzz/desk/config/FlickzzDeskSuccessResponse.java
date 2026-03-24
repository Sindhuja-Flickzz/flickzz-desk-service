package com.flickzz.desk.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data 
@AllArgsConstructor
public class FlickzzDeskSuccessResponse {

	private String code; 
	private String title; 
	private String description;
}
