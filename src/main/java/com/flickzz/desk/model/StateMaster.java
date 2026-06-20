package com.flickzz.desk.model;

import java.time.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_STATE_MASTER")
public class StateMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stateGen")
	@SequenceGenerator(name = "stateGen", sequenceName = "STATE_SEQ", allocationSize = 1)
	@Column(name = "STATE_ID")
	private Long stateId;

	@Column(name = "STATE_NAME", nullable = false, unique = true, length = 100)
	private String stateName;

	@Column(name = "STATE_CODE", unique = true, length = 10)
	private String stateCode;

	@ManyToOne
	@JoinColumn(name = "COUNTRY_ID", foreignKey = @ForeignKey(name = "FK_STATE_COUNTRY"), nullable = false)
	private CountryMaster country;

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
