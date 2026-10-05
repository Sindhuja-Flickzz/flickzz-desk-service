package com.flickzz.desk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketSubtaskVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long subtaskId;
    private TicketMasterVO ticket;
    private String subtaskNumber;
    private AgentMasterVO assignedTo;
    private String status;
}