package com.flickzz.desk.model;

import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	private Task task; // maps to FD_TASK

	@ManyToOne
	@JoinColumn(name = "PROGRESS_ID", referencedColumnName = "PROGRESS_ID", nullable = false)
	private ProgressStatus progressStatus; // maps to FD_PROGRESS_STATUS

	@ManyToOne
	@JoinColumn(name = "AGENT_ID", referencedColumnName = "AGENT_ID", nullable = false)
	private AgentMaster agent; // maps to FD_AGENT_MASTER

	@Column(name = "TITLE", length = 255)
	private String title;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "TENTATIVE_START_DATE")
	private Date tentativeStartDate;

	@Column(name = "TENTATIVE_END_DATE")
	private Date tentativeEndDate;

	@Column(name = "ACTUAL_START_DATE")
	private Date actualStartDate;

	@Column(name = "ACTUAL_END_DATE")
	private Date actualEndDate;

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
