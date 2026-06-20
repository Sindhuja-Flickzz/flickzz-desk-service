package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterLoginRequestVO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String firstname;
	private String middlename;
	private String lastname;
	private String email;
	private String password;
	private String registerId;
	private String role;
	private String phoneCode;
	private String phoneNumber;
	private Long countryId;
	private Long cityId;
	private Long languageId;
	private Boolean mfaEnabled;
	private Long createdBy;
	private Boolean isCreatedByAdmin;
	private String oldPassword;
}
