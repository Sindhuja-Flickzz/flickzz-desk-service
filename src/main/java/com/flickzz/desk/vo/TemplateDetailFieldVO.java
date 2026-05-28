package com.flickzz.desk.vo;

import java.io.*;
import java.util.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDetailFieldVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long fieldId;
	private String fieldName;
	private Long fieldTypeId;
	private Boolean mandatory;
	private Integer fieldSequence;
	private Boolean isActive;
	private List<TemplateFieldOptionVO> options;
}
