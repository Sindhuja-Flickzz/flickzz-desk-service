package com.flickzz.desk.vo;

import java.io.*;
import java.util.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessServiceRequestVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long serviceId;
	private String serviceName;
	private List<ServiceOfferingVO> serviceOfferings; // mapped list
	private Boolean isActive;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
}
