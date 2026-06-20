package com.flickzz.desk.model;

import java.time.*;
import java.util.*;

import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_BUSINESS_SERVICE")
public class BusinessService {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "businessServiceGen")
	@SequenceGenerator(name = "businessServiceGen", sequenceName = "BUSINESS_SERVICE_SEQ", allocationSize = 1)
	@Column(name = "SERVICE_ID")
	private Long serviceId;

	@Column(name = "SERVICE_NAME", nullable = false, unique = true, length = 150)
	private String serviceName;

	@Builder.Default
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;

	@OneToMany(mappedBy = "businessService", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<ServiceOffering> serviceOfferings;

	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", foreignKey = @ForeignKey(name = "FK_BUSINESS_SERVICE_COMPANY"), nullable = false)
	private CompanyMaster company;

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
