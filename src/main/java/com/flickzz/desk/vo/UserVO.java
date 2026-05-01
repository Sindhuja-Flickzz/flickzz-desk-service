package com.flickzz.desk.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long userId;
	private String userName;
	private String firstName;
	private String lastName;
	private String registerId;
	private String email;
	private String role;
	private String phone;
	private CountryMasterVO country;
	private CityMasterVO city;
	private LanguageMasterVO language;
	private boolean mfaEnabled;
	private Boolean isActive;
	private String createdBy;
	private String updatedBy;
}
