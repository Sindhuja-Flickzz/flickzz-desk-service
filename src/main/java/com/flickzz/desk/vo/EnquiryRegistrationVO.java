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
public class EnquiryRegistrationVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long enquiryId;
	private String firstName;
	private String middleName;
	private String lastName;
	private String userName;
	private String password;
	private String userRole;
	private String email;
	private String orgName;
	private String phone;
	private CountryMasterVO country;
	private Integer employeeSize;
	private Boolean isActive;
}
