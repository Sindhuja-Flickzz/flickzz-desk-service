package com.flickzz.desk.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RitmApproverRequestVO {

    private Long ritmId;
    private Long companyId;
    private String reason;
    private Long assignedBy;
    private Boolean isGroupApprover;
    private Long approverConfigId;
    private List<Long> approverIds;
    private Boolean isCreatorAdmin;
}