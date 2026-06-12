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
@Table(name = "FD_PROJECT")
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_seq")
	@SequenceGenerator(name = "project_seq", sequenceName = "PROJECT_SEQ", allocationSize = 1)
	@Column(name = "PROJECT_ID")
	private Long projectId;

	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", referencedColumnName = "COMPANY_ID")
	private CompanyMaster company; // maps to FD_COMPANY_MASTER

	@Column(name = "PROJECT_CD", length = 100)
	private String projectCode;

	@Column(name = "PROJECT_NAME", nullable = false)
	private String projectName;

	@Column(name = "PROJECT_DESC")
	private String projectDesc;

	@Column(name = "PLANNED_START_DATE")
	private Date plannedStartDate;

	@Column(name = "PLANNED_END_DATE")
	private Date plannedEndDate;

	@Column(name = "ACTUAL_START_DATE")
	private Date actualStartDate;

	@Column(name = "ACTUAL_END_DATE")
	private Date actualEndDate;

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<Epic> epics;

	@Builder.Default
	@Column(name = "IS_SAVED")
	private Boolean isSaved = false;

	@Builder.Default
	@Column(name = "IS_SUBMITED")
	private Boolean isSubmited = false;

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
