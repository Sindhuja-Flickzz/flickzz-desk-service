package com.flickzz.desk.vo;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentMasterVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long agentId;
	private String agentName;
	private String mailId;
	private String accessId;
	private CompanyMasterVO organization;
	private List<AgentSkillsMappingVO> agentSkillsMappings;
	private CalendarMasterVO calendar;
	private String phone;
	private CountryMasterVO country;
	private CityMasterVO city;
	private LanguageMasterVO language;
	private Boolean isActive;
	private String createdBy;
	private String updatedBy;
}
