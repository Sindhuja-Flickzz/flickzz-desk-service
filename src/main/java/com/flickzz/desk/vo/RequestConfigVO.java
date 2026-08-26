package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestConfigVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long configId;
	private String requestType; // RITM or INC
	private String requestPrefix; // Prefix for request number, e.g., RITM, INC
	private Integer revision;
	private Integer rangeFrom;
	private Integer rangeTo;
	private Integer currentRange;
	private Boolean calculateBackward;
	private CompanyMasterVO company;
	private Integer callHorizonPercentage;
	private Integer callHorizonDays;
	private Boolean isActive;
	private Boolean isEnabled;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
}
