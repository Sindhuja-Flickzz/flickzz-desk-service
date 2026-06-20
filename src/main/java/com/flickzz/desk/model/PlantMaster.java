package com.flickzz.desk.model;

import java.time.*;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FD_PLANT_MASTER", uniqueConstraints = {
		@UniqueConstraint(name = "UQ_PLANT_NAME_COMPANY", columnNames = { "PLANT_NAME", "COMPANY_ID" }) })
public class PlantMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plantGen")
	@SequenceGenerator(name = "plantGen", sequenceName = "PLANT_SEQ", allocationSize = 1)
	@Column(name = "PLANT_ID", unique = true, nullable = false)
	private Long plantId;

	@Column(name = "PLANT_NAME", nullable = false, length = 255)
	private String plantName;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COUNTRY_ID", unique = true, foreignKey = @ForeignKey(name = "FK_PLANT_REGION"), nullable = false)
	private CountryMaster region;

	/* Relation: Plant → Calendar */
	@ManyToOne
	@JoinColumn(name = "CALENDAR_ID", nullable = false)
	private CalendarMaster calendar;

	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", foreignKey = @ForeignKey(name = "FK_PLANT_COMPANY"), nullable = false)
	private CompanyMaster company;

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
