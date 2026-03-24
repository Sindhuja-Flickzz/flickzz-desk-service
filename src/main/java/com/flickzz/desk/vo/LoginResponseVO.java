package com.flickzz.desk.vo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String loginResponse;
	
	private String accessToken;

	private String refreshToken;
}
