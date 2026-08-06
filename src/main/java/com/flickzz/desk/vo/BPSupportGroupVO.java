package com.flickzz.desk.vo;

import java.io.Serializable;
import java.util.List;

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
	
	private List<BPSupportGroupManagerVO> managers;

	private String groupName;

	private Boolean isActive;

	private Boolean isUnderApproval;

	private Long createdBy;

	private Long updatedBy;
}