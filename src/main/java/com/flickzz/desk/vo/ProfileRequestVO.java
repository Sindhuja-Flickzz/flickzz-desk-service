package com.flickzz.desk.vo;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRequestVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long agentId;
	private String agentName;
	private String mailId;
	private String accessId;
	private String phoneCode;
	private String phoneNumber;
	private Long orgId;
	private String createdBy;
	private String updatedBy;
	private List<AgentSkillRequestVO> skills;
	private Long calendarId;
	private Long countryId;
	private Long cityId;
	private Long languageId;
}
