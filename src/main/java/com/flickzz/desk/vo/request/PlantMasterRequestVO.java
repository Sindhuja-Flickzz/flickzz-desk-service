package com.flickzz.desk.vo.request;

import java.io.Serializable;
import java.util.List;

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
    private List<String> weekOff;
	private Long agentId;
	private Boolean isActive;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;

}
