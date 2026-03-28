package com.flickzz.desk.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarWorkdayVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long workdayId;
    private String workday;   // e.g., "Monday"
    private Boolean isActive;
}
