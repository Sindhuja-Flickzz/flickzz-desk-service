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
public class ProgressStatusVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long progressId;
	private CompanyMasterVO companyId;
	private String progressName;
	private Integer progressSequence;
	private String colorCode;
	private String updatedBy;

}
