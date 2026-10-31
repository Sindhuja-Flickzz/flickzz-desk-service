package com.flickzz.desk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

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
    private BigDecimal dailyCapacity;
    private String phoneCode;
    private String phoneNumber;
    private CountryMasterVO country;
    private CityMasterVO city;
    private List<UserLanguageMappingVO> languages;
    private Boolean isActive;
    private Long createdBy;
    private Long updatedBy;
    private Boolean isCreatedByAdmin;
    private Boolean isUpdatedByAdmin;
}
