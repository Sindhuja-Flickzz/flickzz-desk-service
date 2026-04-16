package com.flickzz.desk.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
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

	@Column(name = "CREATED_BY", length = 50)
	private String createdBy;

	@Column(name = "UPDATED_BY", length = 50)
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
