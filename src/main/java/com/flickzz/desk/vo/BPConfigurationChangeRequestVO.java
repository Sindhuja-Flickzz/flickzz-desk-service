package com.flickzz.desk.vo;

import com.flickzz.desk.model.BPConfiguration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BPConfigurationChangeRequestVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long ccrId;
    private BPConfigurationVO configurationId;
    private Long changedRequestId;
    private Long sourceChangeId;
    private Boolean bpPriority;
    private Boolean bpSla;
    private Boolean category;
    private Boolean supportGroup;
    private Boolean assignment;
    private String operation;
    private CompanyMasterVO requestedByOrg;
    private Long requestedByUserId;
    private CompanyMasterVO approvalOrg;
    private String status;
    private Integer totalInternalApprovalLevels;
    private Integer currentInternalApprovalLevel;
    private Integer totalBpApprovalLevels;
    private Integer currentBpApprovalLevel;
    private Date createdOn;
    private Date updatedOn;
    private Long createdBy;
    private Long updatedBy;
    private Boolean isCreatorAdmin;
}
