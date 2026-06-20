package com.flickzz.desk.model;

import java.time.*;

import jakarta.persistence.*;
import lombok.*;

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

	@Column(name = "CREATED_BY", nullable = false)
	private Long createdBy;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	@Column(name = "IS_CREATOR_ADMIN", nullable = false)
	private Boolean isCreatorAdmin;

	@Builder.Default
	@Column(name = "IS_UPDATER_ADMIN")
	private Boolean isUpdaterAdmin = false;

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
