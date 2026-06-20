package com.flickzz.desk.vo;

import java.io.*;
import java.util.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubTaskVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long subTaskId;
	private TaskVO taskId;
	private ProgressStatusVO progress;
	private Integer maxProgress;
	private AgentMasterVO agentId;
	private String title;
	private String description;
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
