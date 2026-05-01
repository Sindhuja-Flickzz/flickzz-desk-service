package com.flickzz.desk.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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
@Table(name = "FD_SERVICE_OFFERING")
public class ServiceOffering {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "service_offering_seq")
	@SequenceGenerator(name = "service_offering_seq", sequenceName = "SERVICE_OFFERING_SEQ", allocationSize = 1)
	@Column(name = "OFFERING_ID")
	private Long offeringId;

	@ManyToOne
	@JoinColumn(name = "SERVICE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_OFFERING_SERVICE"))
	@JsonManagedReference
	private BusinessService businessService; // FK relationship

	@Column(name = "OFFERING_NAME", nullable = false, length = 150)
	private String offeringName;

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
