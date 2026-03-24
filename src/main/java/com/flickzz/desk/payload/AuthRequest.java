package com.flickzz.desk.payload;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String username;
    private String password;
}
