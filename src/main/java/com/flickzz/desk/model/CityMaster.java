package com.flickzz.desk.model;

import java.time.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_CITY_MASTER")
public class CityMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cityGen")
	@SequenceGenerator(name = "cityGen", sequenceName = "CITY_SEQ", allocationSize = 1)
	@Column(name = "CITY_ID")
	private Long cityId;

	@Column(name = "CITY_NAME", nullable = false, length = 100)
	private String cityName;

	@Column(name = "CITY_CODE", nullable = false, unique = true, length = 50)
	private String cityCode;

	@ManyToOne
	@JoinColumn(name = "STATE_ID", foreignKey = @ForeignKey(name = "FK_CITY_STATE"), nullable = false)
	private StateMaster state;

	@ManyToOne
	@JoinColumn(name = "COUNTRY_ID", foreignKey = @ForeignKey(name = "FK_CITY_COUNTRY"), nullable = false)
	private CountryMaster country;

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
