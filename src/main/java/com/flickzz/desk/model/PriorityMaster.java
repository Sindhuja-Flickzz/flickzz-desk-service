package com.flickzz.desk.model;

import java.time.*;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FD_PRIORITY_MASTER")
public class PriorityMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "priorityGen")
	@SequenceGenerator(name = "priorityGen", sequenceName = "PRIORITY_SEQ", allocationSize = 1)
	@Column(name = "PRIORITY_ID")
	private Long priorityId;

	@Column(name = "PRIORITY_NAME", nullable = false, length = 100)
	private String priorityName;

	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", foreignKey = @ForeignKey(name = "FK_PRIORITY_COMPANY"), nullable = false)
	private CompanyMaster organization;

	@Column(name = "RANK", nullable = false)
	private Integer rank;

	@Column(name = "COLOR_CODE", length = 20)
	private String colorCode;

	@Column(name = "RESPONSE_SLA", nullable = false)
	private Integer responseSla;

	@Column(name = "RESOLUTION_SLA", nullable = false)
	private Integer resolutionSla;

	@Builder.Default
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;

	@Column(name = "CREATED_BY", nullable = false)
	private Long createdBy;

	@Column(name = "UPDATED_BY")
	private Long updatedBy;

	@Column(name = "IS_CREATOR_ADMIN", nullable = false)
	private Boolean isCreatorAdmin;

	@Builder.Default
	@Column(name = "IS_UPDATER_ADMIN")
	private Boolean isUpdaterAdmin = false;

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
