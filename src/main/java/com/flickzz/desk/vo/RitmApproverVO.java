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
public class RitmApproverVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long ritmApproverId;

    private RitmMasterVO ritm;

    private Boolean isGroupApprover;

    private RequestApproverConfigVO approverConfig;

    private AgentMasterVO approverAgent;

    private Integer approverSequence;

    private Boolean isMainApprover;

    private String approvalStatus;

    private String approvalRemark;

    private LocalDateTime approvedOn;

    private Long createdBy;

    private LocalDateTime createdOn;

    private Long updatedBy;

    private LocalDateTime updatedOn;

    private Boolean isActive;
}