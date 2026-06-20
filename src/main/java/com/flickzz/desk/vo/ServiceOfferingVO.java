package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOfferingVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long offeringId;
	private BusinessServiceVO serviceId; // reference to FD_BUSINESS_SERVICE
	private String offeringName;
	private Boolean isActive;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
}
