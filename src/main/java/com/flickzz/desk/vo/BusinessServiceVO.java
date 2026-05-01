package com.flickzz.desk.vo;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessServiceVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long serviceId;
	private String serviceName;
	private List<ServiceOfferingVO> serviceOfferings; // mapped list
	private Boolean isActive;
	private String createdBy;
	private String updatedBy;
}
