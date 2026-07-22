package com.flickzz.desk.vo;

import java.io.*;
import java.util.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BPSupportGroupVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long supportGroupId;

	private BPConfigurationVO configuration;

	private List<BPSupportGroupMemberVO> members;

	private String groupName;

	private Boolean isActive;

	private Long createdBy;

	private Long updatedBy;
}