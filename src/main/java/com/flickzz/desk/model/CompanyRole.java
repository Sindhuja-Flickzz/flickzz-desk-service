package com.flickzz.desk.model;

import java.time.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_COMPANY_ROLES", uniqueConstraints = {
		@UniqueConstraint(name = "UQ_COMPANY_ROLE_MAPPING", columnNames = { "COMPANY_ID", "MAPPING_ID" }) })
public class CompanyRole {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_role_seq")
	@SequenceGenerator(name = "company_role_seq", sequenceName = "COMPANY_ROLE_SEQ", allocationSize = 1)
	@Column(name = "ROLE_ID")
	private Long roleId;

	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", foreignKey = @ForeignKey(name = "FK_COMPANY_ROLE_COMPANY"), nullable = false)
	private CompanyMaster company;

	@ManyToOne
	@JoinColumn(name = "MAPPING_ID", foreignKey = @ForeignKey(name = "FK_COMPANY_ROLE_MAPPING_COMPANY"), nullable = false)
	private CompanyMaster mappedCompany;

	@Builder.Default
	@Column(name = "IS_SERVICE_PROVIDER")
	private Boolean isServiceProvider = false;

	@Builder.Default
	@Column(name = "IS_REQUESTOR")
	private Boolean isRequestor = false;

	@Builder.Default
	@Column(name = "IS_BOTH")
	private Boolean isBoth = false;

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
