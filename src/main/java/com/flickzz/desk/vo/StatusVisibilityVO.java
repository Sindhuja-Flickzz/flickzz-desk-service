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
public class StatusVisibilityVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long visibilityId;

    private String statusCode;

    private CompanyMasterVO company;

    private WorkItemVO workItem;

    private StatusMasterVO currentStatus;

    private StatusMasterVO visibleStatus;

    private Boolean isActive;

    private Long createdBy;

    private LocalDateTime createdAt;

    private Boolean isCreatorAdmin;

    private Boolean isUpdaterAdmin;

    private Long updatedBy;

    private LocalDateTime updatedAt;
}