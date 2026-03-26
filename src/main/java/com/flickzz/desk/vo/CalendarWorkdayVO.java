package com.flickzz.desk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarWorkdayVO {
    private Long workdayId;
    private String workday;   // e.g., "Monday"
    private Boolean isActive;
}
