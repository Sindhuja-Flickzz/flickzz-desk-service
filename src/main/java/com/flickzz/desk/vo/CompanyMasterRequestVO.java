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
public class CompanyMasterRequestVO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long companyId;
	private String companyName;
	private String phoneCode;
	private String registeredNumber;
	private String uid;
	private Long countryId; // reference to CountryMaster;
	private Long stateId;
	private Long cityId;
	private String pinCode;
	private String addressLine1;
	private String addressLine2;
	private Integer employeeSize;
	private String mail;
	private String createdBy;
	private String updatedBy;
}
