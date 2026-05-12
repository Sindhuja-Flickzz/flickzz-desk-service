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
public class CalendarTypeVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long calendarTypeId;
	private String typeName;
	private CompanyMasterVO company;
	private Boolean isActive;
	private String createdBy;
	private String updatedBy;
}
