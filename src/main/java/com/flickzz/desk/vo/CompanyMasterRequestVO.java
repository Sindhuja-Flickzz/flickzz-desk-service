package com.flickzz.desk.vo;

import java.io.*;
import java.util.*;

import lombok.*;

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
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
	private String bpUid;
	private int callHorizonDays;
	private Date validFrom;
	private Date validTo;
	private String refNumber;
	private Date refDate;
}
