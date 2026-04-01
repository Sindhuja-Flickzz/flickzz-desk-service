package com.flickzz.desk.model;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "workdays", "holidays", "agentMasters" })
@Table(name = "FD_CALENDAR_MASTER")
public class CalendarMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calendarGen")
	@SequenceGenerator(name = "calendarGen", sequenceName = "CALENDAR_SEQ", allocationSize = 1)
	@Column(name = "CALENDAR_ID", unique = true, nullable = false)
	private Long calendarId;

	@Column(name = "CALENDAR_CODE", nullable = false, length = 20, unique = true)
	private String calendarCode;

	@Column(name = "CALENDAR_TYPE", nullable = false, length = 20, unique = true)
	private String calendarType;

	@Column(name = "VALID_FROM", nullable = false)
	private Date validFrom;

	@Column(name = "VALID_TO", nullable = false)
	private Date validTo;

	/* Relation: One Calendar → Many Workdays */
	@OneToMany(mappedBy = "calendarMaster", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CalendarWorkday> workdays;

	@OneToMany(mappedBy = "calendarMaster", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CalendarHoliday> holidays;

	@OneToMany(mappedBy = "calendarMaster", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<AgentMaster> agentMasters;

	@Builder.Default
	@Column(name = "IS_SUPPORT", nullable = false)
	private Boolean isSupport = false;

	@Builder.Default
	@Column(name = "IS_REQUESTOR", nullable = false)
	private Boolean isRequestor = false;

	@Column(name = "WORK_FROM")
	private String workFrom; // HH:mm

	@Column(name = "WORK_TO")
	private String workTo; // HH:mm

	@Column(name = "TIMEZONE", length = 50)
	private String timezone;

	@Builder.Default
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;

	@Column(name = "CREATED_BY", length = 50)
	private String createdBy;

	@Column(name = "UPDATED_BY", length = 50)
	private String updatedBy;

	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(name = "UPDATED_AT")
	private LocalDateTime updatedAt = LocalDateTime.now();

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
