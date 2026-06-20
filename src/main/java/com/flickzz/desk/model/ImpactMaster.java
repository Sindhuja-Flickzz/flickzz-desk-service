package com.flickzz.desk.model;

import java.math.*;
import java.time.*;

import jakarta.persistence.*;
import lombok.*;

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
