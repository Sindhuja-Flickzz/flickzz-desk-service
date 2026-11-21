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
public class StatusCreateRequestVO {

    private Long companyId;
    private String requestType;
    private String statusCode;
    private Integer sequenceNo;
    private String statusColor;
    private Boolean isActive;
    private Long createdBy;
    private Boolean isCreatorAdmin;
    private List<String> visibleStatuses;
}
