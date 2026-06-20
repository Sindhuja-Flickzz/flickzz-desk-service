package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkillRequestVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long skillId;
	private String skillName;
	private Long companyId;
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
}
