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
@Table(name = "FD_REQUEST_ITEM")
public class RequestItem {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "requestTtemGen")
	@SequenceGenerator(name = "requestTtemGen", sequenceName = "REQUEST_ITEM_SEQ", allocationSize = 1)
	@Column(name = "RITM_ID")
	private Long ritmId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TICKET_ID", nullable = false)
	@JsonBackReference
	private Ticket ticket;

	// Add RITM-specific fields here if needed (e.g., approval workflow, catalog
	// item)

	// getters and setters...
}
