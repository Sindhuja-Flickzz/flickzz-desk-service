package com.flickzz.desk.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
