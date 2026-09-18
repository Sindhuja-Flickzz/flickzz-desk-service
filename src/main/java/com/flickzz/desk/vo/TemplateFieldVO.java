package com.flickzz.desk.vo;

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
public class TemplateFieldVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long fieldId;
    private String fieldName;
    private String defaultValue;
    private boolean isEditable;
    private String description;
    private Long fieldTypeId;
    private Boolean mandatory;
    private Integer fieldSequence;
    private Boolean isActive;
    private List<TemplateFieldOptionVO> options;
}
