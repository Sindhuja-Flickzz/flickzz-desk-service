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
public class RitmAuditDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long auditDetailId;
    private RitmAuditVO audit;
    private String fieldName;
    private String oldValue;
    private String newValue;
}
