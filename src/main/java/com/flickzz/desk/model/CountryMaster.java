package com.flickzz.desk.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "FD_COUNTRY_MASTER")
public class CountryMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "countryGen")
	@SequenceGenerator(name = "countryGen", sequenceName = "COUNTRY_SEQ", allocationSize = 1)
	@Column(name = "COUNTRY_ID")
	private Long countryId;

	@Column(name = "COUNTRY_NAME", nullable = false, unique = true, length = 255)
	private String countryName;

	@Column(name = "ISO_CODE", nullable = false, unique = true, length = 2)
	private String isoCode;

	@Column(name = "CURRENCY", nullable = false, length = 2)
	private String currency;

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
