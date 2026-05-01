package com.flickzz.desk.model;

import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

	@Column(name = "CREATED_BY", length = 50)
	private String createdBy;

	@Column(name = "UPDATED_BY", length = 50)
	private String updatedBy;

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
