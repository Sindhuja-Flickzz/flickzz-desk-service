package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long userId;
	private String userName;
	private String firstName;
	private String middleName;
	private String lastName;
	private String registerId;
	private String email;
	private String role;
	private String phoneCode;
	private String phoneNumber;
	private CountryMasterVO country;
	private CityMasterVO city;
	private AgentMasterVO agent;
	private LanguageMasterVO language;
	private boolean mfaEnabled;
	private Boolean isActive;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
}
