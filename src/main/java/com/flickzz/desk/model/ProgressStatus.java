package com.flickzz.desk.model;

import java.time.LocalDateTime;

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
@Table(name = "FD_PROGRESS_STATUS")
public class ProgressStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "progress_status_seq")
	@SequenceGenerator(name = "progress_status_seq", sequenceName = "PROGRESS_STATUS_SEQ", allocationSize = 1)
	@Column(name = "PROGRESS_ID")
	private Long progressId;

	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", referencedColumnName = "COMPANY_ID")
	private CompanyMaster company; // maps to FD_COMPANY_MASTER

	@Column(name = "PROGRESS_NAME", length = 50)
	private String progressName; // e.g. NOT STARTED, IN PROGRESS, DONE

	@Column(name = "PROGRESS_SEQUENCE", nullable = false)
	private Integer progressSequence; // ordering of statuses

	@Column(name = "COLOR_CODE", length = 20)
	private String colorCode; // optional hex or predefined color

	@Builder.Default
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;

	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt;

	@Column(name = "CREATED_BY", length = 50)
	private String createdBy;

	@Column(name = "UPDATED_AT")
	private LocalDateTime updatedAt;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

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
