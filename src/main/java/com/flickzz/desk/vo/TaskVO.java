package com.flickzz.desk.vo;

import java.io.*;
import java.util.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long taskId;
	private UserStoryVO storyId;
	private AgentMasterVO agentId;
	private ProgressStatusVO progress;
	private Integer maxProgress;
	private String title;
	private String description;
	private List<SubTaskVO> subTasks;
	private Integer taskSequence;
	private Date plannedStartDate;
	private Date plannedEndDate;
	private Date actualStartDate;
	private Date actualEndDate;
	private Boolean isActive;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
}
