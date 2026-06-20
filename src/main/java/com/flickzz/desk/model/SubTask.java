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
@Table(name = "FD_SUB_TASK")
public class SubTask {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sub_task_seq")
	@SequenceGenerator(name = "sub_task_seq", sequenceName = "SUB_TASK_SEQ", allocationSize = 1)
	@Column(name = "SUB_TASK_ID")
	private Long subTaskId;

	@ManyToOne
	@JoinColumn(name = "TASK_ID", referencedColumnName = "TASK_ID", nullable = false)
	@JsonBackReference
	private Task task; // maps to FD_TASK

	@ManyToOne
	@JoinColumn(name = "PROGRESS_ID", referencedColumnName = "PROGRESS_ID", nullable = false)
	private ProgressStatus progressStatus; // maps to FD_PROGRESS_STATUS

	@Column(name = "MAX_PROGRESS_NUMBER", nullable = false)
	private Integer maxProgressStatus;

	@ManyToOne
	@JoinColumn(name = "AGENT_ID", referencedColumnName = "AGENT_ID", nullable = false)
	private AgentMaster agent; // maps to FD_AGENT_MASTER

	@Column(name = "TITLE", length = 255)
	private String title;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "PLANNED_START_DATE")
	private Date plannedStartDate;

	@Column(name = "PLANNED_END_DATE")
	private Date plannedEndDate;

	@Column(name = "ACTUAL_START_DATE")
	private Date actualStartDate;

	@Column(name = "ACTUAL_END_DATE")
	private Date actualEndDate;

	@Builder.Default
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;

	@Column(name = "CREATED_BY", nullable = false)
	private Long createdBy;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	@Column(name = "IS_CREATOR_ADMIN", nullable = false)
	private Boolean isCreatorAdmin;

	@Builder.Default
	@Column(name = "IS_UPDATER_ADMIN")
	private Boolean isUpdaterAdmin = false;

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
