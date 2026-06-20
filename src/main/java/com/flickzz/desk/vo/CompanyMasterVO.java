package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyMasterVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long companyId;
	private String companyName;
	private String phoneCode;
	private String registeredNumber;
	private String uid;
	private CountryMasterVO country;
	private StateMasterVO state;
	private CityMasterVO city;
	private String pinCode;
	private String addressLine1;
	private String addressLine2;
	private Integer employeeSize;
	private String mail;
	private Boolean isActive;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
}
