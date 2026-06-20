package com.flickzz.desk.vo;

import java.io.*;
import java.util.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStoryVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long storyId;
	private EpicVO epicId;
	private ProgressStatusVO progress;
	private Integer maxProgress;
	private String storyCode;
	private String title;
	private String description;
	private Integer storySequence;
	private AgentMasterVO agentId;
	private Date plannedStartDate;
	private Date plannedEndDate;
	private Date actualStartDate;
	private Date actualEndDate;
	private Long predecessorId;
	private List<ProjectLeadAssignmentVO> leads;
	private List<TaskVO> tasks;
	private PriorityMasterVO priorityId;
	private Integer storyPoints;
	private Boolean isActive;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;

	// For request purpose
	private String mappingStoryId;
	private String mappingPredecessorId;
}
