package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlantMasterRequestVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long plantId;
	private String plantName;
	private Long countryId; // reference to CountryMaster;
	private Long calendarId; // reference to CalendarMaster
	private Long companyId;
	private Boolean isActive;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;

}
