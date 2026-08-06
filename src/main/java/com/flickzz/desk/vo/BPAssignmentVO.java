package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BPAssignmentVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long assignmentId;

	private BPConfigurationVO configuration;

	private BPSubCategoryVO subCategory;

	private BPSupportGroupVO supportGroup;

	private Boolean isActive;

	private Boolean isUnderApproval;

	private Long createdBy;

	private Long updatedBy;
}