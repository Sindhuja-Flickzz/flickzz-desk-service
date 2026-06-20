package com.flickzz.desk.model;

import java.time.*;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FD_REQUEST_CONFIG")
public class RequestConfig {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "requestConfigGen")
	@SequenceGenerator(name = "requestConfigGen", sequenceName = "REQUEST_CONFIG_SEQ", allocationSize = 1)
	@Column(name = "CONFIG_ID")
	private Long configId;

	@Column(name = "REQUEST_TYPE", nullable = false, length = 10)
	private String requestType; // RITM or INC

	@Column(name = "REQUEST_PREFIX", length = 20, nullable = false)
	private String requestPrefix; // Prefix for request number, e.g., RITM, INC

	@Column(name = "REVISION", nullable = false)
	private Integer revision;

	@Column(name = "RANGE_FROM", nullable = false)
	private Integer rangeFrom;

	@Column(name = "RANGE_TO", nullable = false)
	private Integer rangeTo;

	@Builder.Default
	@Column(name = "CALCULATE_BACKWARD")
	private Boolean calculateBackward = false;

	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", foreignKey = @ForeignKey(name = "FK_REQUEST_CONFIG_COMPANY"), nullable = false)
	private CompanyMaster company;

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
