package com.flickzz.desk.model;

import java.time.*;

import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_PROJECT_LEAD_ASSIGNMENT")
public class ProjectLeadAssignment {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_lead_seq")
	@SequenceGenerator(name = "project_lead_seq", sequenceName = "PROJECT_LEAD_SEQ", allocationSize = 1)
	@Column(name = "ASSIGNMENT_ID")
	private Long assignmentId;

	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", referencedColumnName = "COMPANY_ID", nullable = false)
	private CompanyMaster company; // maps to FD_COMPANY_MASTER

	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "STORY_ID", referencedColumnName = "STORY_ID", nullable = false)
	private UserStory userStory; // maps to FD_USER_STORY

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
