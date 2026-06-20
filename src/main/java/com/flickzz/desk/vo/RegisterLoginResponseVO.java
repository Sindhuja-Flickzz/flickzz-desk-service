package com.flickzz.desk.vo;

import java.io.*;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RegisterLoginResponseVO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String accessToken;
	private String refreshToken;
	private boolean mfaEnabled;
	private String userRole;
	private boolean isEnquiryUser;
	private Long userOrgId;
	private Long userId;
	private String userOrgName;
	private String secretImageUri;
}
