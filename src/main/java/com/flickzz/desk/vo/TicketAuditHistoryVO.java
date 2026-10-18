package com.flickzz.desk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketAuditHistoryVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long auditDetailId;
    private Long auditId;
    private String ritmNumber;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private Long changedBy;
    private LocalDateTime changedAt;
}
