package com.flickzz.desk.vo;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BPConfigurationChangeRequestRemarkVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long remarkId;

    private BPConfigurationChangeRequestVO ccrId;

    private ConfigChangeApprovalVO approval;

    private String remarkType;

    private Integer approverLevel;

    private String approvalStatus;

    private Long userId;

    private Long organizationId;

    private String remark;

    private LocalDateTime createdOn;
}