package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BPSupportGroupVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long supportGroupId;

	private BPConfigurationVO configuration;

	private String groupName;

	private Boolean isActive;

	private Long createdBy;

	private Long updatedBy;
}