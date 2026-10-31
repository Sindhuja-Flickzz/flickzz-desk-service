package com.flickzz.desk.vo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EffortTypeRequestVO {

    private String typeName;
    private Integer sequence;
    private Long orgId;
    private Long createdBy;
    private Long updatedBy;
    private Boolean isCreatedByAdmin;
    private Boolean isUpdatedByAdmin;
}
