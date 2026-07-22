package com.flickzz.desk.model;

import java.time.*;

import org.hibernate.annotations.*;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FD_TICKET_TYPE_MASTER")
public class TicketTypeMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticketTypeSeq")
	@SequenceGenerator(name = "ticketTypeSeq", sequenceName = "TICKET_TYPE_SEQ", allocationSize = 1)
	@Column(name = "TICKET_TYPE_ID")
	private Long ticketTypeId;

	@Column(name = "TICKET_TYPE_NAME", nullable = false, length = 100)
	private String ticketTypeName;

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

	@CreationTimestamp
	@Column(name = "CREATED_AT", updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
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