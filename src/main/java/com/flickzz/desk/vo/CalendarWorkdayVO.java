package com.flickzz.desk.vo;

import java.util.Date;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarWorkdayVO {
    private Long workdayId;
    private String workday;   // e.g., "Monday"
    private Boolean isActive;
    private String createdBy;
    private String updatedBy;
    private Date createdAt; // formatted as string for frontend
    private Date updatedAt;
}
