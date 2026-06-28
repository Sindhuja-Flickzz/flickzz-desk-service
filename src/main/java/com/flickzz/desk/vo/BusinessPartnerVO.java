package com.flickzz.desk.vo;

import java.io.*;
import java.util.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessPartnerVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long roleId;
	private CompanyMasterVO company;
	private CompanyMasterVO mappedCompany;
	private Boolean isServiceProvider;
	private Boolean isRequestor;
	private Boolean isBoth;
	private int callHorizon;
	private Date validFrom;
	private Date validTo;
	private String refNo;
	private Date refDate;
	private Boolean isActive;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
}
