package com.flickzz.desk.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnquiryRegisterRequestVO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String enquiryId;
	private String firstName;
	private String middleName;
	private String lastName;
	private String email;
	private String orgName;
	private String phoneCode;
	private String phoneNumber;
	private Long countryId;
	private Long stateId;
	private Long cityId;
	private Integer employeeSize;
}
