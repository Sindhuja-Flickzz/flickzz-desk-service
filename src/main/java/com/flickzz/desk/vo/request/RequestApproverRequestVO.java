package com.flickzz.desk.vo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestApproverRequestVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long approverConfigId;
    private String approverCode;
    private List<ApproverEntry> approvers;
    private Boolean followSequence;
    private Boolean isAnyApprovalSufficient;
    private Long companyId;
    private Long createdBy;
    private Long updatedBy;
    private Boolean isCreatedByAdmin;
    private Boolean isUpdatedByAdmin;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApproverEntry implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long agentId;
        private Integer approverSequence;
    }
}