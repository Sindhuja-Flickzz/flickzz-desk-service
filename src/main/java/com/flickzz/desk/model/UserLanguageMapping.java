package com.flickzz.desk.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
		name = "FD_USER_LANGUAGE_MAPPING",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "UK_USER_LANGUAGE",
						columnNames = {"USER_ID", "LANGUAGE_ID"}
				)
		}
)
public class UserLanguageMapping {

	@Id
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "user_language_mapping_seq"
	)
	@SequenceGenerator(
			name = "user_language_mapping_seq",
			sequenceName = "FD_USER_LANGUAGE_MAPPING_SEQ",
			allocationSize = 1
	)
	@Column(name = "MAPPING_ID")
	private Long mappingId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "USER_ID",
			nullable = false,
			foreignKey = @ForeignKey(name = "FK_USER_LANGUAGE_USER")
	)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "LANGUAGE_ID",
			nullable = false,
			foreignKey = @ForeignKey(name = "FK_USER_LANGUAGE_LANGUAGE")
	)
	private LanguageMaster language;

	@Builder.Default
	@Column(name = "IS_ACTIVE", nullable = false)
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

	@Column(name = "CREATED_AT", nullable = false)
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
