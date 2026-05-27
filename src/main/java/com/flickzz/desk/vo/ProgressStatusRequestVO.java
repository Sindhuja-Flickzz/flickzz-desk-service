package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressStatusRequestVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long progressId;
	private CompanyMasterVO company;
	private String progressName;
	private Integer progressSequence;
	private String colorCode;
	private String updatedBy;
	private String createdBy;
	private Long orgId;
}
