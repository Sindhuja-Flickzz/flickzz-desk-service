package com.flickzz.desk.vo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusVisibilityUpdateRequestVO {

    private Long statusId;
    private Long companyId;
    private String requestType;
    private String statusCode;
    private Integer sequenceNo;
    private String statusColor;
    private List<String> visibleStatuses;
    private Long updatedBy;
    private Boolean isUpdaterAdmin;
}
