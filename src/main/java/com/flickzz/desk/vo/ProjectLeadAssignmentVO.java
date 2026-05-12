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
public class ProjectLeadAssignmentVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long assignmentId;
	private CompanyMasterVO companyId;
	private UserStoryVO storyId;
	private Boolean isActive;
	private String createdBy;
	private String updatedBy;
}
