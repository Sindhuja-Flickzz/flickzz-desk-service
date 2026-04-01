package com.flickzz.desk.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentSkillsMappingVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long agentSkillId;
	private AgentMasterVO agent;
	private SkillMasterVO skill;
}