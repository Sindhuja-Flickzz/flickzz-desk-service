package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectLeadAssignmentVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long assignmentId;
	private CompanyMasterVO company;
	private UserStoryVO story;
	private Boolean isActive;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
	// For request purpose
	private Long companyId;
}
