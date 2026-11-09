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
public class WorkItemRequestTypeMappingRequestVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long workItemId;
    private List<Long> requestTypeIds;
    private Long companyId;
    private Long mappingid;
    private Long createdBy;
    private Long updatedBy;
    private Long deletedBy;
    private Boolean isCreatedByAdmin;
    private Boolean isUpdatedByAdmin;
    private Boolean isDeletedByAdmin;
}
