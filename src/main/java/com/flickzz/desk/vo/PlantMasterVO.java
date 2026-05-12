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
public class PlantMasterVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long plantId;
	private String plantName;
	private CountryMasterVO region;
	private CalendarMasterVO calendar; // reference to CalendarMaster
	private CompanyMasterVO company; // reference to CompanyMaster
	private Boolean isActive;
	private String createdBy;
	private String updatedBy;
}
