package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRoleVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long roleId;
	private CompanyMasterVO company;
	private CompanyMasterVO mappedCompany;
	private Boolean isServiceProvider;
	private Boolean isRequestor;
	private Boolean isBoth;
	private Boolean isActive;
	private String createdBy;
	private String updatedBy;
}
