package com.flickzz.desk.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data 
@AllArgsConstructor
public class FlickzzDeskResponse {

	private String code; 
	private String title; 
	private String description;
	private Object attributes;
}
