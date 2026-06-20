package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequestVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long ticketId;
	private String requestType; // RITM or INC
	private String requestNumber;
	private Long openedById; // FK reference to FD_USER
	private Long requestedForId; // FK reference to FD_USER
	private Long businessServiceId; // FK reference
	private Long serviceOfferingId; // FK reference
	private String configItemGroup;
	private Long impactId; // FK reference
	private Long priorityId; // FK reference
	private String shortDescription;
	private String description;
	private String attachment;
	private Boolean isActive;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
}
