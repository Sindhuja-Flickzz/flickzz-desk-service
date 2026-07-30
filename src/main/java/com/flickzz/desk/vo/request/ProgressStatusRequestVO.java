package com.flickzz.desk.vo.request;

import java.io.*;

import com.flickzz.desk.vo.CompanyMasterVO;
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
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
	private Long orgId;
}
