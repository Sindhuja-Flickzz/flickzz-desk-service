package com.flickzz.desk.model;

import java.time.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_BP_PRIORITY", uniqueConstraints = {
		@UniqueConstraint(name = "UK_PRIORITY_LEVEL_VERSION", columnNames = { "CONFIGURATION_ID", "TICKET_TYPE_ID", "LEVEL", "VERSION" }),
		@UniqueConstraint(name = "UK_PRIORITY_CODE_VERSION", columnNames = { "CONFIGURATION_ID", "TICKET_TYPE_ID", "CODE", "VERSION" }) })
public class BPPriority {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bpPrioritySeq")
	@SequenceGenerator(name = "bpPrioritySeq", sequenceName = "BP_PRIORITY_SEQ", allocationSize = 1)
	@Column(name = "PRIORITY_ID")
	private Long priorityId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CONFIGURATION_ID", nullable = false)
	private BPConfiguration configuration;

	@Column(name = "LEVEL", nullable = false)
	private Integer level;

	@Column(name = "CODE", nullable = false)
	private String code;

	@Column(name = "DESCRIPTION", nullable = false)
	private String description;

	@Column(name = "VERSION", nullable = false)
	private Integer version;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TICKET_TYPE_ID", nullable = false)
	private TicketTypeMaster ticketType;

	@OneToOne(mappedBy = "priority", cascade = CascadeType.ALL, orphanRemoval = true)
	private BPSla sla;

	@Builder.Default
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = false;

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