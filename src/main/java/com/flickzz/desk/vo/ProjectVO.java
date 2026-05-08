package com.flickzz.desk.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long projectId;
	private CompanyMasterVO companyId;
	private String projectCode;
	private String projectName;
	private String projectDesc;
	private List<EpicVO> epics;
	private Date startDate;
	private Date endDate;
	private Boolean isActive;
	private String createdBy;
	private String updatedBy;
}
