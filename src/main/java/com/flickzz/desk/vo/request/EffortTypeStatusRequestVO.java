package com.flickzz.desk.vo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EffortTypeStatusRequestVO {

    private Long effortTypeId;
    private Long orgId;
    private Boolean isActive;
    private Long updatedBy;
    private Boolean isUpdaterAdmin;
}
