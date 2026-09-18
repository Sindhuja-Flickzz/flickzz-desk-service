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
public class RitmFieldValueVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long ritmFieldValueId;
    private RitmMasterVO ritmId;
    private TemplateFieldVO templateFieldId;
    private String fieldValue;
    private Boolean isActive;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isCreatorAdmin;
    private Boolean isUpdaterAdmin;
}
