package com.flickzz.desk.vo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentRequestVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long agentId;
    private String agentName;
    private String mailId;
    private String accessId;
    private String phoneCode;
    private String phoneNumber;
    private Long orgId;
    private BigDecimal dailyCapacity;
    private Long createdBy;
    private Long updatedBy;
    private Boolean isCreatedByAdmin;
    private Boolean isUpdatedByAdmin;
    private List<AgentSkillRequestVO> skills;
    private Long calendarId;
    private Long countryId;
    private Long cityId;
    private List<Long> languageIds;
}
