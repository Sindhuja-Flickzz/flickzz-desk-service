package com.flickzz.desk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlannedHoursVO {

    private Long ticketId;
    private Long orgId;
    private Long allocationId;
    private Long agentId;
    private Long workItemId;
    private Integer hoursPlanned;
    private Integer minutesPlanned;
}
