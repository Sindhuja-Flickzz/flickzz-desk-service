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
public class ServiceOfferingVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long offeringId;
	private BusinessServiceVO serviceId; // reference to FD_BUSINESS_SERVICE
	private String offeringName;
	private Boolean isActive;
	private String createdBy;
	private String updatedBy;
}
