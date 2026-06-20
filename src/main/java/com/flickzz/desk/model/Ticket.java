package com.flickzz.desk.model;

import java.time.*;
import java.util.*;

import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FD_TICKET")
public class Ticket {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticketGen")
	@SequenceGenerator(name = "ticketGen", sequenceName = "TICKET_SEQ", allocationSize = 1)
	@Column(name = "TICKET_ID")
	private Long ticketId;

	@Column(name = "REQUEST_TYPE", nullable = false, length = 10)
	private String requestType; // RITM or INC

	@Column(name = "REQUEST_NUMBER", nullable = false, unique = true, length = 50)
	private String requestNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OPENED_BY", nullable = false)
	private User openedBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REQUESTED_FOR", nullable = false)
	private User requestedFor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BUSINESS_SERVICE_ID")
	private BusinessService businessService;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SERVICE_OFFERING_ID")
	private ServiceOffering serviceOffering;

	@Column(name = "CONFIG_ITEM_GROUP", length = 100)
	private String configItemGroup;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IMPACT_ID")
	private ImpactMaster impact;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRIORITY_ID")
	private PriorityMaster priority;

	@Column(name = "SHORT_DESCRIPTION", length = 255)
	private String shortDescription;

	@Column(name = "DESCRIPTION", columnDefinition = "TEXT")
	private String description;

	@Column(name = "ATTACHMENT", length = 255)
	private String attachment;

	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", foreignKey = @ForeignKey(name = "FK_TICKET_COMPANY"), nullable = false)
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

	@Builder.Default
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;

	@OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<RequestItem> requestItems;

	@OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<Incident> incidents;

	@OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<TicketWatchlist> watchlist;

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
