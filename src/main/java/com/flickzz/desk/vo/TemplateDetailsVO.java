package com.flickzz.desk.vo;

import java.io.*;
import java.util.*;

import lombok.*;

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
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
}
