package com.flickzz.desk.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalendarHolidayVO {
    private Date holidayDate;
    private String description;
    private Boolean isActive;
}
