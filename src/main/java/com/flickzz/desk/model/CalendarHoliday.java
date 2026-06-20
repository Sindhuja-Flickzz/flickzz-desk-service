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
@Table(name = "FD_CALENDAR_HOLIDAY")
public class CalendarHoliday {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calendarHolidayGen")
	@SequenceGenerator(name = "calendarHolidayGen", sequenceName = "CALENDAR_HOLIDAY_SEQ", allocationSize = 1)
	@Column(name = "HOLIDAY_ID", unique = true, nullable = false)
	private Long holidayId;

	@Column(name = "HOLIDAY_DATE", nullable = false, length = 10)
	private Date holidayDate;

	@Column(name = "DESCRIPTION", length = 255)
	private String description;

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
