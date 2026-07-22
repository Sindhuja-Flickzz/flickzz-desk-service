package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BPSlaVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long slaId;

	private BPConfigurationVO configuration;

	private BPPriorityVO priority;

	private Integer firstResponseTime;

	private Character firstResponseTerm;

	private Integer resolutionTime;

	private Character resolutionTerm;

	private Integer updateFrequency;

	private Character updateFrequencyTerm;

	private Boolean isActive;

	private Long createdBy;

	private Long updatedBy;
}