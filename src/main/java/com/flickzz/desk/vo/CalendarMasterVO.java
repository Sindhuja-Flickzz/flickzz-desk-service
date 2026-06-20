package com.flickzz.desk.vo;

import java.io.*;
import java.util.*;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalendarMasterVO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long calendarId;
	private String calendarCode;
	private CalendarTypeVO calendarType;
	private Date validFrom;
	private Date validTo;
	private List<CalendarWorkdayVO> workdays;
	private List<CalendarHolidayVO> holidays;
	private String workFrom;
	private String workTo;
	private String timezone;
	private Boolean isActive;
	private CompanyMasterVO company;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
}
