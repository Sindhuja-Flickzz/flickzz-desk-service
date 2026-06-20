package com.flickzz.desk.model;

import java.time.*;
import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "workdays", "holidays", "agentMasters" })
@Table(name = "FD_CALENDAR_MASTER", uniqueConstraints = {
		@UniqueConstraint(name = "UQ_CALENDAR_CODE_COMPANY", columnNames = { "CALENDAR_CODE", "COMPANY_ID" }) })
public class CalendarMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calendarGen")
	@SequenceGenerator(name = "calendarGen", sequenceName = "CALENDAR_SEQ", allocationSize = 1)
	@Column(name = "CALENDAR_ID", unique = true, nullable = false)
	private Long calendarId;

	@Column(name = "CALENDAR_CODE", nullable = false, length = 20)
	private String calendarCode;

	@ManyToOne
	@JoinColumn(name = "CALENDAR_TYPE_ID", foreignKey = @ForeignKey(name = "FK_CALENDAR_TYPE"), nullable = false)
	private CalendarType calendarType;

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

	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", foreignKey = @ForeignKey(name = "FK_CALENDAR_COMPANY"), nullable = false)
	private CompanyMaster company;

	@Column(name = "WORK_FROM")
	private String workFrom; // HH:mm

	@Column(name = "WORK_TO")
	private String workTo; // HH:mm

	@Column(name = "TIMEZONE", length = 50)
	private String timezone;

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
