package com.flickzz.desk.vo.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkItemRequestVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;
    private String description;
    private Long companyId;
    private Long createdBy;
    private Boolean isCreatedByAdmin;
}
