package com.flickzz.desk.model;

import java.time.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_CALENDAR_WORKDAYS")
public class CalendarWorkday {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workdayGen")
	@SequenceGenerator(name = "workdayGen", sequenceName = "WORKDAY_SEQ", allocationSize = 1)
	@Column(name = "WORKDAY_ID", unique = true, nullable = false)
	private Long workdayId;

	@Column(name = "WORKDAY", nullable = false, length = 20)
	private String workday; // e.g., "Monday", "Tuesday"

	@ManyToOne
	@JoinColumn(name = "CALENDAR_ID", nullable = false)
	private CalendarMaster calendarMaster;

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

	// or follow JavaBean convention for booleans:
	public boolean isActive() {
		return Boolean.TRUE.equals(isActive);
	}

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
