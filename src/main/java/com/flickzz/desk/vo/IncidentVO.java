package com.flickzz.desk.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long incidentId;
	private Long ticketId; // reference to FD_TICKET

	// Add INC-specific fields here if needed (severity, resolution SLA)
}
