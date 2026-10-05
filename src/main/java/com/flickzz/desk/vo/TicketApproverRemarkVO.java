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
public class TicketApproverRemarkVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long remarkId;

    private Long ticketApproverId;

    private Long ticketId;

    private String remarkType;

    private String remark;

    private Long createdBy;

    private String createdByName;

    private String createdByEmail;

    private LocalDateTime createdOn;

    private Long updatedBy;

    private LocalDateTime updatedOn;

    private Boolean isActive;
}