package com.flickzz.desk.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkillRequestVO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Long skillId;
	
	private String skillName;
	
	private String createdBy;
	
	private String updatedBy;
}
