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
public class ApprovalMasterVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long approvalId;
    private Long requestId;
    private String requestType;
    private String approvalType;
    private Integer approverLevel;
    private Long approverUserId;
    private Long approverOrgId;
    private String status;
    private String approverType;
    private String description;
    private Boolean mandatory;
    private LocalDateTime approvedOn;
    private List<BPConfigurationChangeRequestRemarkVO> remarks;
    private Long createdBy;
    private LocalDateTime createdOn;
    private Long updatedBy;
    private LocalDateTime updatedOn;
}
