package com.flickzz.desk.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
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
