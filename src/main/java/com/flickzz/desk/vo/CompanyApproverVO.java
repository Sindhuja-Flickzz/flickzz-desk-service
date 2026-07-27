package com.flickzz.desk.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CompanyApproverVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long approverId;
    private CompanyMasterVO company;
    private AgentMasterVO agent;
    private Integer level;
    private Boolean isActive;
    private Long createdBy;
    private Long updatedBy;
    private Boolean isCreatedByAdmin;
    private Boolean isUpdatedByAdmin;
}
