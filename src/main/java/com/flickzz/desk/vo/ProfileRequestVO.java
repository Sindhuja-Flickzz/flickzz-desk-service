package com.flickzz.desk.vo;

import java.io.*;
import java.util.*;

import lombok.*;

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
	private Long createdBy;
	private Long updatedBy;
	private Boolean isCreatedByAdmin;
	private Boolean isUpdatedByAdmin;
	private List<AgentSkillRequestVO> skills;
	private Long calendarId;
	private Long countryId;
	private Long cityId;
	private Long languageId;
}
