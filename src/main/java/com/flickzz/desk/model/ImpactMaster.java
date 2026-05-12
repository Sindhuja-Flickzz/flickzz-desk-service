package com.flickzz.desk.model;

import java.math.BigDecimal;
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
@Table(name = "FD_IMPACT_MASTER")
public class ImpactMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "impactGeq")
	@SequenceGenerator(name = "impactGeq", sequenceName = "IMPACT_SEQ", allocationSize = 1)
	@Column(name = "IMPACT_ID")
	private Long impactId;

	@Column(name = "IMPACT_CODE", nullable = false, unique = true, length = 50)
	private String impactCode; // e.g., LOW, MEDIUM, HIGH, CRITICAL

	@Column(name = "IMPACT_LEVEL", nullable = false)
	private Integer impactLevel; // numeric (1 = highest impact)

	@Builder.Default
	@Column(name = "SLA_MULTIPLIER", precision = 5, scale = 2)
	private BigDecimal slaMultiplier = BigDecimal.ONE.setScale(2);

	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", foreignKey = @ForeignKey(name = "FK_IMPACT_COMPANY"), nullable = false)
	private CompanyMaster organization;

	@Builder.Default
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;

	@Column(name = "CREATED_BY", length = 100)
	private String createdBy;

	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(name = "UPDATED_BY", length = 100)
	private String updatedBy;

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
