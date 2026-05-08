package com.flickzz.desk.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubTaskVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long subTaskId;
	private TaskVO taskId;
	private ProgressStatusVO progressId;
	private AgentMasterVO agentId;
	private String title;
	private String description;
	private Date tentativeStartDate;
	private Date tentativeEndDate;
	private Date actualStartDate;
	private Date actualEndDate;
	private Boolean isActive;
	private String createdBy;
	private String updatedBy;
}
