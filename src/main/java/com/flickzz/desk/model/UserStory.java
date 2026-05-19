package com.flickzz.desk.model;

import java.time.*;
import java.util.*;

import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_USER_STORY")
public class UserStory {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_story_seq")
	@SequenceGenerator(name = "user_story_seq", sequenceName = "USER_STORY_SEQ", allocationSize = 1)
	@Column(name = "STORY_ID")
	private Long storyId;

	@ManyToOne
	@JoinColumn(name = "EPIC_ID", referencedColumnName = "EPIC_ID", nullable = false)
	@JsonBackReference
	private Epic epic; // maps to FD_EPIC

	@ManyToOne
	@JoinColumn(name = "PROGRESS_ID", referencedColumnName = "PROGRESS_ID", nullable = false)
	private ProgressStatus progressStatus; // maps to FD_PROGRESS_STATUS

	@Column(name = "STORY_CODE", length = 50)
	private String storyCode;

	@Column(name = "TITLE", length = 255)
	private String title;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "STORY_SEQUENCE", nullable = false)
	private Integer storySequence;

	@OneToMany(mappedBy = "userStory", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<ProjectLeadAssignment> projectLeadAssignments;

	@ManyToOne
	@JoinColumn(name = "AGENT_ID", referencedColumnName = "AGENT_ID")
	private AgentMaster agent; // maps to FD_AGENT_MASTER

	@Column(name = "PLANNED_START_DATE")
	private Date plannedStartDate;

	@Column(name = "PLANNED_END_DATE")
	private Date plannedEndDate;

	@Column(name = "ACTUAL_START_DATE")
	private Date actualStartDate;

	@Column(name = "ACTUAL_END_DATE")
	private Date actualEndDate;

	@Column(name = "MAX_PROGRESS_NUMBER", nullable = false)
	private Integer maxProgressStatus;

	@ManyToOne
	@JoinColumn(name = "PREDECESSOR_ID", referencedColumnName = "STORY_ID")
	private UserStory predecessor; // self-reference

	@ManyToOne
	@JoinColumn(name = "PRIORITY_ID", referencedColumnName = "PRIORITY_ID")
	private PriorityMaster priority; // maps to FD_PRIORITY_MASTER

	@OneToMany(mappedBy = "userStory", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<Task> tasks;

	@Column(name = "STORY_POINTS")
	private Integer storyPoints;

	@Builder.Default
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt;

	@Column(name = "UPDATED_AT")
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
