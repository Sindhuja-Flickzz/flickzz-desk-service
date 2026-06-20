package com.flickzz.desk.model;

import java.time.*;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FD_ENQUIRY_REGISTRATION")
public class EnquiryRegistration {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "enquiryRegGen")
	@SequenceGenerator(name = "enquiryRegGen", sequenceName = "ENQUIRY_REG_SEQ", allocationSize = 1)
	@Column(name = "ENQUIRY_ID")
	private Long enquiryId;

	@Column(name = "FIRST_NAME", nullable = false, length = 100)
	private String firstName;

	@Column(name = "MIDDLE_NAME", length = 100)
	private String middleName;

	@Column(name = "LAST_NAME", nullable = false, length = 100)
	private String lastName;

	@Column(name = "USER_NAME", nullable = false, length = 100)
	private String userName;

	@Column(name = "PASSWORD", length = 255)
	private String password;

	@Column(name = "USER_ROLE", nullable = false, length = 50)
	private String userRole;

	@Column(name = "EMAIL", nullable = false, length = 150)
	private String email;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMPANY_ID", foreignKey = @ForeignKey(name = "FK_ENQUIRY_COMPANY"), nullable = false)
	private CompanyMaster company; // FK to FD_COMPANY_MASTER

	@Column(name = "PHONE_CODE", length = 10)
	private String phoneCode;

	@Column(name = "PHONE_NUMBER", length = 20)
	private String phoneNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COUNTRY_ID", foreignKey = @ForeignKey(name = "FK_ENQUIRY_COUNTRY"), nullable = false)
	private CountryMaster country; // FK to FD_COUNTRY_MASTER

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STATE_ID", foreignKey = @ForeignKey(name = "FK_ENQUIRY_STATE"))
	private StateMaster state;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CITY_ID", foreignKey = @ForeignKey(name = "FK_ENQUIRY_CITY"))
	private CityMaster city;

	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt;

	@Column(name = "UPDATED_AT")
	private LocalDateTime updatedAt;

	@Builder.Default
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;

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
