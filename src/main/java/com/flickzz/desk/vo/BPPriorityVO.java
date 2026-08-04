package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BPPriorityVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long priorityId;

	private BPConfigurationVO configuration;

	private Integer level;

	private String code;

	private String description;

	private TicketTypeMasterVO ticketType;

	private Integer version;

	private Boolean isUnderApproval;

	private Boolean isActive;

	private Long createdBy;

	private Long updatedBy;
}