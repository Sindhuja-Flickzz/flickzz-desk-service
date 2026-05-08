package com.flickzz.desk.model;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

	@Column(name = "START_DATE")
	private Date startDate;

	@Column(name = "END_DATE")
	private Date endDate;

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<Epic> epics;

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
