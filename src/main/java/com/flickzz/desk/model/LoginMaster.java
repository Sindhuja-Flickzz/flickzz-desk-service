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
@Getter
@Setter
@Table(name = "FD_LOGIN_MASTER")
public class LoginMaster {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loginGenn")
	@Column(name = "LOGIN_ID", unique = true, nullable = false)
	@SequenceGenerator(name = "loginGenn", sequenceName = "LOGIN_SEQ", allocationSize = 1)
	private Long loginId;

	@Column(name = "USER_NAME", nullable = false, length = 255, unique = true)
	private String userName;

	@Column(name = "PASSWORD", nullable = false, length = 255, unique = true)
	private String password;

	@Column(name = "ROLE", nullable = false, length = 100)
	private String role;

	@Column(name = "LAST_LOGIN_DATETIME")
	private Date lastLoginDatetime;

	// Foreign key to FD_USER
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID", nullable = false, unique = true, foreignKey = @ForeignKey(name = "FK_LOGIN_USER"))
	private User user;

	// Foreign key to AUTH
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AUTH_ID", unique = true, foreignKey = @ForeignKey(name = "FK_LOGIN_AUTH"))
	private Auth auth;

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
