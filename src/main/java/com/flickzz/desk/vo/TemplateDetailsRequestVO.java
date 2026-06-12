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
public class TemplateDetailsRequestVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long templateId;
	private String templateName;
	private Long workItemId;
	private Long companyId;
	private List<TemplateFieldDetailVO> templateDetails;
	private String createdBy;
	private String updatedBy;
}
