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

	@Column(name = "CREATED_BY", length = 50)
	private String createdBy;

	@Column(name = "UPDATED_BY", length = 50)
	private String updatedBy;

	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(name = "UPDATED_AT")
	private LocalDateTime updatedAt = LocalDateTime.now();

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
