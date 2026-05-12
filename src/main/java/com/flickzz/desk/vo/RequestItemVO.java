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
public class RequestItemVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long ritmId;
	private Long ticketId; // reference to FD_TICKET

	// Add RITM-specific fields here if needed (approval workflow, catalog item)
}
