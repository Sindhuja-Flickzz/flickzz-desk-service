package com.flickzz.desk.vo;

import java.io.*;
import java.util.*;

import lombok.*;

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
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
}
