package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long ticketId;
	private String requestType; // RITM or INC
	private String requestNumber;
	private UserVO openedBy; // FK reference to FD_USER
	private UserVO requestedFor; // FK reference to FD_USER
	private BusinessServiceVO businessService; // FK reference
	private ServiceOfferingVO serviceOffering; // FK reference
	private String configItemGroup;
	private ImpactMasterVO impact; // FK reference
	private PriorityMasterVO priority; // FK reference
	private String shortDescription;
	private String description;
	private String attachment;
	private CompanyMasterVO company; // FK reference
	private Boolean isActive;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
}
