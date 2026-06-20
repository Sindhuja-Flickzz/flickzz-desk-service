package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityMasterVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long cityId;
	private String cityName;
	private String cityCode;
	private StateMasterVO state;
	private CountryMasterVO country;
	private String timezone;
	private Boolean isActive;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
}
