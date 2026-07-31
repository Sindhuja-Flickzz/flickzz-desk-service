package com.flickzz.desk.model;

import java.time.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_BP_SLA")
public class BPSla {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bpSlaSeq")
	@SequenceGenerator(name = "bpSlaSeq", sequenceName = "BP_SLA_SEQ", allocationSize = 1)
	@Column(name = "SLA_ID")
	private Long slaId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CONFIGURATION_ID", nullable = false)
	private BPConfiguration configuration;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRIORITY_ID", nullable = false)
	private BPPriority priority;

	@Column(name = "FIRST_RESPONSE_TIME")
	private Integer firstResponseTime;

	@Column(name = "FIRST_RESPONSE_TERM")
	private Character firstResponseTerm;

	@Column(name = "RESOLUTION_TIME")
	private Integer resolutionTime;

	@Column(name = "RESOLUTION_TERM")
	private Character resolutionTerm;

	@Column(name = "UPDATE_FREQUENCY")
	private Integer updateFrequency;

	@Column(name = "UPDATE_FREQUENCY_TERM")
	private Character updateFrequencyTerm;

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