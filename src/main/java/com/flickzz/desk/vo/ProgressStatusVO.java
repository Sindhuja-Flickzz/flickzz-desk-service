package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressStatusVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long progressId;
	private CompanyMasterVO company;
	private String progressName;
	private Integer progressSequence;
	private String colorCode;
	private Long updatedBy;
	private Boolean isUpdatedByAdmin;

}
