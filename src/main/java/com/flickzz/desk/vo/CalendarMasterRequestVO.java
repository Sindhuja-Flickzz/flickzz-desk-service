package com.flickzz.desk.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	private String createdBy;
	private String updatedBy;
	private List<String> calendarTypeList;
}
