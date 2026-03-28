package com.flickzz.desk.vo;

import java.io.Serializable;

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
public class RegisterLoginResponseVO implements Serializable {
      private static final long serialVersionUID = 1L;
	  private String accessToken;
	  private String refreshToken;
	  private boolean mfaEnabled;
	  private String userRole;
	  private String secretImageUri;
}
