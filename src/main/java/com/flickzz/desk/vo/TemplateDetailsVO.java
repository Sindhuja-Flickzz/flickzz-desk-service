package com.flickzz.desk.vo;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDetailsVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long templateId;
	private String templateName;
	private Long workItemId;
	private CompanyMasterVO company;
	private List<TemplateDetailFieldVO> templateDetails;
	private Boolean isActive;
	private String createdBy;
	private String updatedBy;
}
