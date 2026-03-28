package com.flickzz.desk.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalendarHolidayVO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Date holidayDate;
    private String description;
    private Boolean isActive;
}
