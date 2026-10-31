package com.flickzz.desk.vo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubtaskStatusUpdateRequestVO {

    private Long ticketId;
    private Long statusId;
    private String remarks;
    private Long workItemId;
    private Long updatedBy;
    private Boolean isUpdaterAdmin;
    private Long orgId;
}
