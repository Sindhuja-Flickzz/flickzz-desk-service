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
public class RequestConfigVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long configId;
	private String requestType; // RITM or INC
	private String requestPrefix; // Prefix for request number, e.g., RITM, INC
	private Integer revision;
	private Integer rangeFrom;
	private Integer rangeTo;
	private Boolean calculateBackward;
	private PlantMasterVO plant;
	private Boolean isActive;
	private String createdBy;
	private String updatedBy;
}
