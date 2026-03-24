package com.flickzz.desk.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RegisterLoginResponseVO {

	  private String accessToken;
	  private String refreshToken;
	  private boolean mfaEnabled;
	  private String secretImageUri;
}
