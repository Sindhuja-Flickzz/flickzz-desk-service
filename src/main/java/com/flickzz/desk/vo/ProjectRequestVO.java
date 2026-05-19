package com.flickzz.desk.vo;

import java.io.*;
import java.util.*;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequestVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long projectId;
	private String projectName;
	private String projectDesc;
	private Long orgId;
	private List<EpicVO> epics;
	private boolean isSave;
	private boolean isSubmit;
	private String createdBy;
	private String updatedBy;
}
