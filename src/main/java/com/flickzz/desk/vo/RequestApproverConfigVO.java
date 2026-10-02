package com.flickzz.desk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestApproverConfigVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long approverConfigId;

    private String approverCode;

    private CompanyMasterVO company;

    private Boolean followSequence;

    private Boolean isAnyApprovalSufficient;

    private Boolean isActive;

    private Long createdBy;

    private Long updatedBy;

    private Boolean isCreatorAdmin;

    private Boolean isUpdaterAdmin;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<RequestApproverVO> approvers;
}