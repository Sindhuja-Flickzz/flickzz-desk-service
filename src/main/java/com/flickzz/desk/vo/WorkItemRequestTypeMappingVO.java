package com.flickzz.desk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkItemRequestTypeMappingVO {

    private Long mappingId;
    private Long companyId;
    private Long workItemId;
    private String workItemCode;
    private String workItemLabel;
    private Long requestTypeId;
    private String requestTypeName;
    private Boolean isActive;
    private Long createdBy;
    private Boolean isCreatedByAdmin;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private Boolean isUpdatedByAdmin;
    private LocalDateTime updatedAt;
}
