package com.flickzz.desk.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "FD_TICKET_WATCHLIST")
public class TicketWatchlist {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticket_watchlist_seq")
	@SequenceGenerator(name = "ticket_watchlist_seq", sequenceName = "TICKET_WATCHLIST_SEQ", allocationSize = 1)
	@Column(name = "MAPPING_ID")
	private Long mappingId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TICKET_ID", nullable = false)
	@JsonBackReference
	private Ticket ticket;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;

	// getters and setters...
}
