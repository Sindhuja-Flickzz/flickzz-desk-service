package com.flickzz.desk.vo;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalendarMasterRequestVO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long companyId;
	private String calendarCode;
	private Long calendarType;
	@JsonFormat(pattern = "dd.MM.yyyy")
	private Date validFrom;
	@JsonFormat(pattern = "dd.MM.yyyy")
	private Date validTo;
	private List<String> workingDays;
	private List<CalendarHolidayVO> holidays;
	private Long company;
	private String workFrom;
	private String workTo;
	private String timezone;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
	private List<String> calendarTypeList;
}
