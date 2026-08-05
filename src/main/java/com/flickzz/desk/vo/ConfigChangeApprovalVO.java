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
public class ConfigChangeApprovalVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long approvalId;
    private Long ccrId;
    private String approvalType;
    private Integer approverLevel;
    private Long approverUserId;
    private Long approverOrgId;
    private String status;
    private Boolean mandatory;
    private LocalDateTime approvedOn;
    private Long remarkId;
    private Long createdBy;
    private LocalDateTime createdOn;
    private Long updatedBy;
    private LocalDateTime updatedOn;
}
