package com.flickzz.desk.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FD_PLANT_MASTER")
public class PlantMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plantGen")
	@SequenceGenerator(name = "plantGen", sequenceName = "PLANT_SEQ", allocationSize = 1)
	@Column(name = "PLANT_ID", unique = true, nullable = false)
	private Long plantId;

	@Column(name = "PLANT_NAME", unique = true, nullable = false, length = 255)
	private String plantName;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COUNTRY_ID", unique = true, foreignKey = @ForeignKey(name = "FK_PLANT_REGION"), nullable = false)
	private CountryMaster region;

	/* Relation: Plant → Calendar */
	@ManyToOne
	@JoinColumn(name = "CALENDAR_ID", nullable = false)
	private CalendarMaster calendar;

	@Builder.Default
	@Column(name = "IS_ACTIVE", nullable = false)
	private Boolean isActive = true;

	@Column(name = "CREATED_BY", length = 50)
	private String createdBy;

	@Column(name = "UPDATED_BY", length = 50)
	private String updatedBy;

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
