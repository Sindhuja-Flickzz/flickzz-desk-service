package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

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
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
}
