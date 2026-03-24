package com.flickzz.desk.vo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonRequestVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String username;

	private String refreshToken;
}
