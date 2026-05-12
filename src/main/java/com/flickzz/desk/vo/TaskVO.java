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
public class TaskVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long taskId;
	private UserStoryVO storyId;
	private AgentMasterVO agentId;
	private ProgressStatusVO progressId;
	private String title;
	private String description;
	private Integer taskSequence;
	private Date tentativeStartDate;
	private Date tentativeEndDate;
	private Date actualStartDate;
	private Date actualEndDate;
	private Boolean isActive;
	private String createdBy;
	private String updatedBy;
}
