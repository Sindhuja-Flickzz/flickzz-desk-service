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
public class StatusMasterVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long statusId;
    private Long companyId;
    private Long workItemId;
    private WorkItemVO workItem;
    private CompanyMasterVO company;
    private String statusCode;
    private Integer sequenceNo;
    private String statusColor;
    private Boolean isActive;
    private Long createdBy;
    private Boolean isCreatorAdmin;
    private Boolean isUpdaterAdmin;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String requestType;
    private List<StatusVisibilityVO> visibleStatuses;
}
