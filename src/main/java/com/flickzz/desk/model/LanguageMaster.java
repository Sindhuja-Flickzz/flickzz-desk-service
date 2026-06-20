package com.flickzz.desk.model;

import java.time.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_LANGUAGE_MASTER")
public class LanguageMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "languageGen")
	@SequenceGenerator(name = "languageGen", sequenceName = "LANGUAGE_SEQ", allocationSize = 1)
	@Column(name = "LANGUAGE_ID")
	private Long languageId;

	@Column(name = "LANGUAGE_NAME", nullable = false, unique = true, length = 100)
	private String languageName;

	@Column(name = "LANGUAGE_CODE", nullable = false, unique = true, length = 10)
	private String languageCode;

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
