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
@Table(name = "FD_USER_STORY")
public class UserStory {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_story_seq")
	@SequenceGenerator(name = "user_story_seq", sequenceName = "USER_STORY_SEQ", allocationSize = 1)
	@Column(name = "STORY_ID")
	private Long storyId;

	@ManyToOne
	@JoinColumn(name = "EPIC_ID", referencedColumnName = "EPIC_ID", nullable = false)
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

	@ManyToOne
	@JoinColumn(name = "AGENT_ID", referencedColumnName = "AGENT_ID")
	private AgentMaster agent; // maps to FD_AGENT_MASTER

	@Column(name = "TENTATIVE_START_DATE")
	private Date tentativeStartDate;

	@Column(name = "TENTATIVE_END_DATE")
	private Date tentativeEndDate;

	@Column(name = "ACTUAL_START_DATE")
	private Date actualStartDate;

	@Column(name = "ACTUAL_END_DATE")
	private Date actualEndDate;

	@ManyToOne
	@JoinColumn(name = "PREDECESSOR_ID", referencedColumnName = "STORY_ID")
	private UserStory predecessor; // self-reference

	@ManyToOne
	@JoinColumn(name = "PRIORITY_ID", referencedColumnName = "PRIORITY_ID")
	private PriorityMaster priority; // maps to FD_PRIORITY_MASTER

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
