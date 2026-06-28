package com.flickzz.desk.model;

import java.time.*;
import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_BUSINESS_PARTNER", uniqueConstraints = {
		@UniqueConstraint(name = "UQ_BUSINESS_PARTNER_MAPPING", columnNames = { "COMPANY_ID", "MAPPING_ID" }) })
public class BusinessPartner {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "business_partner_seq")
	@SequenceGenerator(name = "business_partner_seq", sequenceName = "BUSINESS_PARTNER_SEQ", allocationSize = 1)
	@Column(name = "ROLE_ID")
	private Long roleId;

	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", foreignKey = @ForeignKey(name = "FK_BUSINESS_PARTNER_COMPANY"), nullable = false)
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
	@Column(name = "CALL_HORIZON")
	private Integer callHorizon = 0;

	@Column(name = "VALID_FROM", nullable = false)
	private Date validFrom;

	@Column(name = "VALID_TO", nullable = false)
	private Date validTo;

	@Column(name = "REF_NO", length = 100)
	private String refNo;

	@Column(name = "REF_DATE")
	private Date refDate;

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
