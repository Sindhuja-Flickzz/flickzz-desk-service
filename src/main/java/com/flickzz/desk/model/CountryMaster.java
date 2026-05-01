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

	@Column(name = "COUNTRY_NAME", nullable = false, unique = true, length = 100)
	private String countryName;

	@Column(name = "ISO_CODE", nullable = false, unique = true, length = 3)
	private String isoCode;

	@Column(name = "PHONE_CODE", nullable = false, length = 10)
	private String phoneCode;

	@Column(name = "CURRENCY_CODE", length = 10)
	private String currencyCode;

	@Column(name = "CURRENCY_NAME", length = 50)
	private String currencyName;

	@Column(name = "TIMEZONE", length = 100)
	private String timezone;

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
