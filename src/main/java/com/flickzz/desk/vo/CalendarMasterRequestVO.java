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
    private String calendarCode;
    private String calendarType;
    @JsonFormat(pattern = "dd.MM.yyyy")
    private Date validFrom;
    @JsonFormat(pattern = "dd.MM.yyyy")
    private Date validTo;
    private List<String> workingDays;
    private List<CalendarHolidayVO> holidays;
    private String workFrom;
    private String workTo;
    private String timezone;
    private boolean isSupport;
    private boolean isRequestor;
    private String createBy;
}
