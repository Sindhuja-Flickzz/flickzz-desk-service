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
public class EffortTypeVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long effortTypeId;
    private String typeName;
    private Integer sequence;
    private Long orgId;
    private Long createdBy;
    private Long updatedBy;
    private Boolean isCreatedByAdmin;
    private Boolean isUpdatedByAdmin;
    private Boolean isActive;
}
