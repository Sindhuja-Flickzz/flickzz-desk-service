package com.flickzz.desk.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpicVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long epicId;
	private ProjectVO projectId;
	private String epicName;
	private Integer epicSequence;
	private ProgressStatusVO progressId;
	private Date startDate;
	private Date endDate;
	private Boolean isActive;
	private String createdBy;
	private String updatedBy;
}
