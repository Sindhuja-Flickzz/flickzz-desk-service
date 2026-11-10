package com.flickzz.desk.vo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkItemEffortTypeMappingRequestVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long mappingId;
    private Long companyId;
    private Long workItemId;
    private Long effortTypeId;
    private List<Long> effortTypeIds;
    private Long createdBy;
    private Long updatedBy;
    private Long deletedBy;
    private Boolean isCreatedByAdmin;
    private Boolean isUpdatedByAdmin;
    private Boolean isDeletedByAdmin;
}
