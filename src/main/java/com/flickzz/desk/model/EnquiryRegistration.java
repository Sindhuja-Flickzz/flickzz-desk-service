package com.flickzz.desk.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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

	@Column(name = "ORG_NAME", length = 200)
	private String orgName;

	@Column(name = "PHONE", length = 20)
	private String phone;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COUNTRY_ID", foreignKey = @ForeignKey(name = "FK_ENQUIRY_COUNTRY"), nullable = false)
	private CountryMaster country; // FK to FD_COUNTRY_MASTER

	@Column(name = "EMPLOYEE_SIZE")
	private Integer employeeSize;

	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(name = "UPDATED_AT")
	private LocalDateTime updatedAt = LocalDateTime.now();

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
