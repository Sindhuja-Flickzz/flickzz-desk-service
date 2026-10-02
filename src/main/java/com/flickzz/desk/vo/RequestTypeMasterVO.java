package com.flickzz.desk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestTypeMasterVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long requestTypeId;

    private String requestTypeName;

    private CompanyMasterVO company;

    private Boolean isActive;

    private Long createdBy;

    private Long updatedBy;

    private Boolean isCreatorAdmin;

    private Boolean isUpdaterAdmin;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}