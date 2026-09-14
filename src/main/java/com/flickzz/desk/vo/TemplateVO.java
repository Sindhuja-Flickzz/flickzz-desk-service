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
public class TemplateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long templateId;
    private String templateName;
    private Long workItemId;
    private CompanyMasterVO company;
    private List<TemplateFieldVO> templateDetails;
    private Boolean isActive;
    private Long createdBy;
    private Long updatedBy;
    private Boolean isCreatedByAdmin;
    private Boolean isUpdatedByAdmin;
}
