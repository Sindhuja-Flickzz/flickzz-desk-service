package com.flickzz.desk.model;

import java.time.*;

import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "FD_USER")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userGenn")
	@Column(name = "USER_ID", unique = true, nullable = false)
	@SequenceGenerator(name = "userGenn", sequenceName = "USER_SEQ", allocationSize = 1)
	private Long userId;

	@Column(name = "USER_NAME", nullable = false, length = 50, unique = true)
	private String userName;

	@Column(name = "PASSWORD", nullable = false, length = 255)
	private String password;

	@Column(name = "FIRST_NAME", length = 50)
	private String firstName;

	@Column(name = "MIDDLE_NAME", length = 50)
	private String middleName;

	@Column(name = "LAST_NAME", length = 50)
	private String lastName;

	@Column(name = "REGISTER_ID", length = 50)
	private String registerId;

	@Column(name = "EMAIL", nullable = false, length = 50, unique = true)
	private String email;

	@Column(name = "ROLE", nullable = false, length = 100)
	private String role;

	@Column(name = "PHONE_CODE", length = 10)
	private String phoneCode;

	@Column(name = "PHONE_NUMBER", length = 20, unique = true, nullable = false)
	private String phoneNumber;

	@ManyToOne
	@JoinColumn(name = "COUNTRY_ID", foreignKey = @ForeignKey(name = "FK_AGENT_COUNTRY"))
	private CountryMaster country;

	@ManyToOne
	@JoinColumn(name = "CITY_ID", foreignKey = @ForeignKey(name = "FK_AGENT_CITY"))
	private CityMaster city;

	@ManyToOne
	@JoinColumn(name = "LANGUAGE_ID", foreignKey = @ForeignKey(name = "FK_AGENT_LANGUAGE"))
	private LanguageMaster language;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private AgentMaster agent;

	@Column(name = "MFA_ENABLED", nullable = false)
	private boolean mfaEnabled;

	@Column(name = "SECRET", length = 255)
	private String secret;

	/* ===================== STATUS ===================== */
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
