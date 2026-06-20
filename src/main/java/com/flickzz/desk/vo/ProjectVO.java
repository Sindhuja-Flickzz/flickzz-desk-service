package com.flickzz.desk.vo;

import java.io.*;
import java.util.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long projectId;
	private CompanyMasterVO company;
	private String projectCode;
	private String projectName;
	private String projectDesc;
	private List<EpicVO> epics;
	private Date plannedStartDate;
	private Date plannedEndDate;
	private Date actualStartDate;
	private Date actualEndDate;
	private Boolean isActive;
	private Boolean isSaved;
	private Boolean isSubmitted;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
}
