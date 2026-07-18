package com.flickzz.desk.vo;

import java.io.*;
import java.time.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketTypeMasterVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long ticketTypeId;
	private String ticketTypeName;
	private Boolean isActive;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatorAdmin;
	private Boolean isUpdaterAdmin;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
