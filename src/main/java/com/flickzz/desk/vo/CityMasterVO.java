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
public class CityMasterVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long cityId;
	private String cityName;
	private String cityCode;
	private StateMasterVO state;
	private CountryMasterVO country;
	private String timezone;
	private Boolean isActive;
	private String createdBy;
	private String updatedBy;
}
