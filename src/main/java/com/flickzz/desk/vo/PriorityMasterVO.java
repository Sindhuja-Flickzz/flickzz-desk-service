package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriorityMasterVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long priorityId;
	private String priorityName;
	private CompanyMasterVO organization;
	private Integer rank;
	private String colorCode;
	private Integer responseSla;
	private Integer resolutionSla;
	private Boolean isActive;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
}
