package com.flickzz.desk.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguageMasterVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long languageId;
	private String languageName;
	private String languageCode;
	private Boolean isActive;
	private String createdBy;
	private String updatedBy;
}
