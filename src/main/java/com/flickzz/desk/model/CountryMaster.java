package com.flickzz.desk.model;

import java.time.*;

import jakarta.persistence.*;
import lombok.*;

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
