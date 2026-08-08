package com.flickzz.desk.vo.response;

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
public class ApprovalProgressRemarkResponseVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long remarkId;
    private String stage;
    private String remarkType;
    private Integer approverLevel;
    private String approvalStatus;
    private Long userId;
    private String userName;
    private Long organizationId;
    private String remark;
    private LocalDateTime createdOn;
}

