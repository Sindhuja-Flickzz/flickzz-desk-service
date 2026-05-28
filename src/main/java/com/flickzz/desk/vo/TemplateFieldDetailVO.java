package com.flickzz.desk.vo;

import java.io.*;
import java.util.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateFieldDetailVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String fieldName;
	private Long fieldTypeId;
	private Boolean mandatory;
	private List<TemplateFieldOptionVO> options;
}
