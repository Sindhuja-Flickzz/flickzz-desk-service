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
@Table(name = "FD_BP_SUPPORT_GROUP", uniqueConstraints = {
		@UniqueConstraint(name = "UK_SUPPORT_GROUP", columnNames = { "CONFIGURATION_ID", "GROUP_NAME" }) })
public class BPSupportGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bpSupportGroupSeq")
	@SequenceGenerator(name = "bpSupportGroupSeq", sequenceName = "BP_SUPPORT_GROUP_SEQ", allocationSize = 1)
	@Column(name = "SUPPORT_GROUP_ID")
	private Long supportGroupId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CONFIGURATION_ID", nullable = false)
	private BPConfiguration configuration;

	@Column(name = "GROUP_NAME", nullable = false, length = 100)
	private String groupName;

	@OneToMany(mappedBy = "supportGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<BPSupportGroupMember> members;

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