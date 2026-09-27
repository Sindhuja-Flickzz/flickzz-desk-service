package com.flickzz.desk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestApproverVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long approverId;

    private RequestApproverConfigVO approverConfig;

    private AgentMasterVO agent;

    private Integer approverSequence;

    private Boolean isActive;

    private Long createdBy;

    private Long updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}