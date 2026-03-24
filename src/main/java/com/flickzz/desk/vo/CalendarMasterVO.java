package com.flickzz.desk.vo;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalendarMasterVO {

    private Long calendarId;
    private String calendarCode;
    private String calendarType;
    private Date validFrom;
    private Date validTo;
    private List<CalendarWorkdayVO> workdays;
    private List<CalendarHolidayVO> holidays;
    private LocalDateTime workFrom;
    private LocalDateTime workTo;
    private String timezone;
    private Boolean isActive;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
