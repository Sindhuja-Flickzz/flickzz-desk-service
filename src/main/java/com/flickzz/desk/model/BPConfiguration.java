package com.flickzz.desk.model;

import java.time.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_BP_CONFIGURATION")
public class BPConfiguration {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bpConfigurationSeq")
	@SequenceGenerator(name = "bpConfigurationSeq", sequenceName = "BP_CONFIGURATION_SEQ", allocationSize = 1)
	@Column(name = "CONFIGURATION_ID")
	private Long configurationId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BUSINESS_PARTNER_ID", nullable = false)
	private BusinessPartner businessPartner;

//	@Builder.Default
//	@Column(name = "PRIORITY_APPROVAL_STATUS")
//	private Boolean priorityApprovalStatus = false;
//
//	@Builder.Default
//	@Column(name = "SLA_APPROVAL_STATUS")
//	private Boolean slaApprovalStatus = false;
//
//	@Builder.Default
//	@Column(name = "CATEGORY_APPROVAL_STATUS")
//	private Boolean categoryApprovalStatus = false;
//
//	@Builder.Default
//	@Column(name = "SUPPORT_GROUP_APPROVAL_STATUS")
//	private Boolean supportGroupApprovalStatus = false;
//
//	@Builder.Default
//	@Column(name = "ASSIGNMENT_APPROVAL_STATUS")
//	private Boolean assignmentApprovalStatus = false;

	@Builder.Default
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;

	@Column(name = "CREATED_BY")
	private Long createdBy;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	@Column(name = "IS_CREATOR_ADMIN")
	private Boolean isCreatorAdmin;

	@Column(name = "IS_UPDATER_ADMIN")
	private Boolean isUpdaterAdmin;

	@Column(name = "CREATED_AT", updatable = false)
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
