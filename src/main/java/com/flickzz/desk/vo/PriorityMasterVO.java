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
	private String createdBy;
	private String updatedBy;
}
