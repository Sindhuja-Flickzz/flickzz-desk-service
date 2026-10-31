package com.flickzz.desk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubtaskEffortSummaryVO {

    private Long subtaskId;
    private String subtaskNumber;
    private EffortDurationVO totalLoggedEffort;
    private List<AgentEffortSummaryVO> agents;
}
