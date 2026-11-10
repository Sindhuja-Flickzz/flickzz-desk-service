package com.flickzz.desk.vo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketClosureApprovalConfigRequestVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long configId;
    private Long companyId;
    private Long workItemId;
    private Long approvalPendingStatusId;
    private Integer autoActionTimeline;
    private String autoActionTimeUnit;
    private Long statusAfterAutoAction;
    private Long statusOnApproval;
    private Long statusOnDecline;
    private Long statusOnClarify;
    private Long createdBy;
    private Long updatedBy;
    private Long deletedBy;
    private Boolean isCreatedByAdmin;
    private Boolean isUpdatedByAdmin;
}
