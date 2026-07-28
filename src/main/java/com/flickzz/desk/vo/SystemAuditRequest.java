package com.flickzz.desk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemAuditRequest {
    private String module;
    private String area;
    private String entityName;
    private Long entityId;
    private String action;
    private String oldValue;
    private String newValue;
    private String changedFields;
    private Long userId;
    private Long companyId;
    private String status;
    private String errorMessage;
}
