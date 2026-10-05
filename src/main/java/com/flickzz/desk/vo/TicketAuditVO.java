package com.flickzz.desk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketAuditVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long auditId;
    private TicketMasterVO ticketId;
    private String actionType;
    private String description;
    private List<TicketAuditDetailVO> auditDetails;
    private Long changedBy;
    private LocalDateTime changedAt;
}
