package com.flickzz.desk.model;

import java.time.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_BP_ASSIGNMENT", uniqueConstraints = {
		@UniqueConstraint(name = "UK_ASSIGNMENT", columnNames = { "CONFIGURATION_ID", "SUB_CATEGORY_ID" }) })
@ToString(exclude = {"configuration","subCategory","supportGroup"})
public class BPAssignment {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bpAssignmentSeq")
	@SequenceGenerator(name = "bpAssignmentSeq", sequenceName = "BP_ASSIGNMENT_SEQ", allocationSize = 1)
	@Column(name = "ASSIGNMENT_ID")
	private Long assignmentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CONFIGURATION_ID", nullable = false)
	private BPConfiguration configuration;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUB_CATEGORY_ID", nullable = false)
	private BPSubCategory subCategory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUPPORT_GROUP_ID", nullable = false)
	private BPSupportGroup supportGroup;

	@Builder.Default
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;

	@Column(name = "CREATED_BY")
	private Long createdBy;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

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