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
@Table(name = "FD_AUTH")
public class Auth {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authGenn")
	@Column(name = "AUTH_ID", unique = true, nullable = false)
	@SequenceGenerator(name = "authGenn", sequenceName = "AUTH_SEQ", allocationSize = 1)
	private Long authId;

	@Column(name = "TOKEN", nullable = false, length = 255, unique = true)
	private String token;

	@Column(name = "EXPIRES_AT", nullable = false)
	private Date expiresAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID", foreignKey = @ForeignKey(name = "FD_USERS_REFRESH_TOKEN"))
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ENQUIRY_ID", foreignKey = @ForeignKey(name = "FD_ENQUIRY_REFRESH_TOKEN"))
	private EnquiryRegistration enquiryRegistration;

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
