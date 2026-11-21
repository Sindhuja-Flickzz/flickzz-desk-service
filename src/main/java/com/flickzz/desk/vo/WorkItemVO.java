package com.flickzz.desk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkItemVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long itemId;
    private String code;
    private String description;
    private Boolean isParent;
    private Boolean isServiceRequest;
    private WorkItemVO parentItem;
    private Boolean isActive;
    private Long createdBy;
    private Long updatedBy;
    private Boolean isCreatedByAdmin;
    private Boolean isUpdatedByAdmin;
}
