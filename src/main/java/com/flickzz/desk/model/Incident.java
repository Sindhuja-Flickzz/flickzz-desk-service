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
@Table(name = "FD_INCIDENT")
public class Incident {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "incidentGen")
	@SequenceGenerator(name = "incidentGen", sequenceName = "INCIDENT_SEQ", allocationSize = 1)
	@Column(name = "INCIDENT_ID")
	private Long incidentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TICKET_ID", nullable = false)
	@JsonBackReference
	private Ticket ticket;

	// Add INC-specific fields here if needed (e.g., severity, resolution SLA)

	// getters and setters...
}
