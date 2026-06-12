package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldTypeVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long typeId;
	private String code;
	private String label;
	private Boolean isActive;
	private String createdBy;
	private String updatedBy;
}
