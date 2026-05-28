package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateFieldOptionVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long optionId;
	private String label;
	private String value;
	private Boolean defaultSelected;
	private Integer optionSequence;
	private Boolean isActive;
}
