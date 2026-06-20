package com.flickzz.desk.model;

import java.time.*;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FD_COMPANY_MASTER")
public class CompanyMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "companyGen")
	@SequenceGenerator(name = "companyGen", sequenceName = "COMPANY_SEQ", allocationSize = 1)
	@Column(name = "COMPANY_ID", unique = true, nullable = false)
	private Long companyId;

	@Column(name = "COMPANY_NAME", unique = true, nullable = false, length = 255)
	private String companyName;

	@Column(name = "PHONE_CODE", length = 10)
	private String phoneCode;

	@Column(name = "REGISTERED_NUMBER", nullable = false, length = 100)
	private String registeredNumber;

	@Column(name = "UID", nullable = false, length = 100, unique = true)
	private String uid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COUNTRY_ID", foreignKey = @ForeignKey(name = "FK_COMPANY_COUNTRY"), nullable = false)
	private CountryMaster country;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STATE_ID", foreignKey = @ForeignKey(name = "FK_COMPANY_STATE"))
	private StateMaster state;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CITY_ID", foreignKey = @ForeignKey(name = "FK_COMPANY_CITY"))
	private CityMaster city;

	@Column(name = "PIN_CODE", length = 20)
	private String pinCode;

	@Column(name = "EMPLOYEE_SIZE")
	private Integer employeeSize;

	@Column(name = "ADDRESS_LINE_1", length = 255)
	private String addressLine1;

	@Column(name = "ADDRESS_LINE_2", length = 255)
	private String addressLine2;

	@Column(name = "MAIL", length = 100, nullable = false, unique = true)
	private String mail;

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
