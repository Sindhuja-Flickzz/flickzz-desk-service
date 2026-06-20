package com.flickzz.desk.model;

import java.time.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_CALENDAR_TYPE")
public class CalendarType {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calendar_type_seq")
	@SequenceGenerator(name = "calendar_type_seq", sequenceName = "CALENDAR_TYPE_SEQ", allocationSize = 1)
	@Column(name = "CALENDAR_TYPE_ID")
	private Long calendarTypeId;

	@Column(name = "TYPE_NAME", nullable = false, length = 100)
	private String typeName;

	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", foreignKey = @ForeignKey(name = "FK_CALENDAR_TYPE_COMPANY"), nullable = false)
	private CompanyMaster company;

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
