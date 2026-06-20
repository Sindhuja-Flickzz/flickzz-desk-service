package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

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
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
}
