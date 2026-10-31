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
public class TicketEffortSummaryVO {

    private Long ticketId;
    private String ticketNumber;
    private EffortDurationVO plannedEffort;
    private EffortDurationVO ticketLoggedEffort;
    private EffortDurationVO subtasksLoggedEffort;
    private EffortDurationVO totalLoggedEffort;
    private List<AgentEffortSummaryVO> agents;
    private List<SubtaskEffortSummaryVO> subtasks;
}
