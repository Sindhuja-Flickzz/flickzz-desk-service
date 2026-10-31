package com.flickzz.desk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketClosureApprovalConfigVO {

    private Long configId;
    private Long companyId;
    private Long workItemId;
    private Boolean isApprovalRequired;
    private Long approvalPendingStatusId;
    private String approvalPendingStatusCode;
    private Boolean autoActionEnabled;
    private Integer autoActionTimeline;
    private String autoActionTimeUnit;
    private Long statusAfterAutoAction;
    private String statusAfterAutoActionCode;
    private Boolean isActive;
    private Long createdBy;
    private Boolean isCreatedByAdmin;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private Boolean isUpdatedByAdmin;
    private LocalDateTime updatedAt;
}
