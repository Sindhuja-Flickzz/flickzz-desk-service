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
public class TemplateFieldDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long fieldId;
    private String fieldName;
    private Long fieldTypeId;
    private Boolean mandatory;
    private Boolean showInInfoPage;
    private List<TemplateFieldOptionVO> options;
}
