package com.flickzz.desk.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_BP_SUPPORT_GROUP", uniqueConstraints = {
		@UniqueConstraint(name = "UK_SUPPORT_GROUP_NAME_VERSION", columnNames = { "CONFIGURATION_ID", "GROUP_NAME", "VERSION" }) })
@ToString(exclude = {"configuration", "members", "managers"})
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

	@OneToMany(mappedBy = "supportGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<BPSupportGroupManager> managers;

	@Column(name = "VERSION", nullable = false)
	@Builder.Default
	private Integer version = 1;

	@Builder.Default
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;

	@Builder.Default
	@Column(name = "IS_UNDER_APPROVAL")
	private Boolean isUnderApproval = false;

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