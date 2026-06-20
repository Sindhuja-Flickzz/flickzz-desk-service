package com.flickzz.desk.model;

import java.time.*;

import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;
import lombok.*;

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
