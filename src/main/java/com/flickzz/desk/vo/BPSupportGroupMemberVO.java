package com.flickzz.desk.vo;

import java.io.*;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BPSupportGroupMemberVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long memberId;

	private BPSupportGroupVO supportGroup;

	private AgentMasterVO agent;

	private Boolean isGroupLead;

	private Boolean isActive;
}