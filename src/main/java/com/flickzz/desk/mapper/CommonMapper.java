package com.flickzz.desk.mapper;

import com.flickzz.desk.config.FlickzzDeskConstants;
import com.flickzz.desk.model.*;
import com.flickzz.desk.vo.*;
import com.flickzz.desk.vo.request.*;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommonMapper {

    public User registerRequesttoUser(RegisterLoginRequestVO request,
                                      @SuppressWarnings("deprecation") @NonNull String rawPassword, CountryMaster country, CityMaster city,
                                      LanguageMaster language) {
        if (request == null) {
            return null;
        }
        return User.builder().firstName(request.getFirstname()).lastName(request.getLastname())
                .middleName(request.getMiddlename()).email(request.getEmail()).userName(request.getEmail())
                .password(passwordEncoder().encode(rawPassword))
//				.languageIds()
                .role(request.getRole() != null ? request.getRole() : FlickzzDeskConstants.ROLE_ADMIN)
                .phoneCode(request.getPhoneCode()).phoneNumber(request.getPhoneNumber()).country(country).city(city)
                .registerId(request.getRegisterId()).mfaEnabled(request.getMfaEnabled())
                .createdBy(request.getCreatedBy()).isCreatorAdmin(request.getIsCreatedByAdmin()).build();
    }

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public LoginMaster userToLoginMaster(User user) {
        if (user == null) {
            return null;
        }
        return LoginMaster.builder().userName(user.getUserName()).password(user.getPassword()).role(user.getRole())
                .createdBy(user.getCreatedBy()).isCreatorAdmin(user.getIsCreatorAdmin()).user(user).build();
    }

    public CalendarMasterVO toCalendarMasterVO(CalendarMaster calendar) {
        if (calendar == null) {
            return null;
        }
        return CalendarMasterVO.builder().calendarId(calendar.getCalendarId()).calendarCode(calendar.getCalendarCode())
                .calendarType(toCalendarTypeVO(calendar.getCalendarType()))
                .company(toCompanyMasterVO(calendar.getCompany())).validFrom(calendar.getValidFrom())
                .validTo(calendar.getValidTo()).isActive(calendar.getIsActive())
                .workdays(calendar.getWorkdays() == null ? null
                        : calendar.getWorkdays().stream().filter(CalendarWorkday::isActive)
                        .map(this::toCalendarWorkdayVO).toList())
                .workFrom(calendar.getWorkFrom()).workTo(calendar.getWorkTo()).timezone(calendar.getTimezone())
                .holidays(calendar.getHolidays() == null ? null
                        : calendar.getHolidays().stream().filter(CalendarHoliday::isActive)
                        .map(this::toCalendarHolidayVO).toList())
                .createdBy(calendar.getCreatedBy()).isCreatedByAdmin(calendar.getIsCreatorAdmin())
                .updatedBy(calendar.getUpdatedBy()).isUpdatedByAdmin(calendar.getIsUpdaterAdmin()).build();
    }

    public CalendarWorkdayVO toCalendarWorkdayVO(CalendarWorkday workday) {
        if (workday == null) {
            return null;
        }
        return CalendarWorkdayVO.builder().workdayId(workday.getWorkdayId()).workday(workday.getWorkday())
                .isActive(workday.getIsActive()).createdBy(workday.getCreatedBy())
                .isCreatedByAdmin(workday.getIsCreatorAdmin()).updatedBy(workday.getUpdatedBy())
                .isUpdatedByAdmin(workday.getIsUpdaterAdmin()).build();
    }

    public CalendarHolidayVO toCalendarHolidayVO(CalendarHoliday holiday) {
        if (holiday == null) {
            return null;
        }
        return CalendarHolidayVO.builder().holidayDate(holiday.getHolidayDate()).description(holiday.getDescription())
                .isActive(holiday.getIsActive()).createdBy(holiday.getCreatedBy())
                .isCreatedByAdmin(holiday.getIsCreatorAdmin()).updatedBy(holiday.getUpdatedBy())
                .isUpdatedByAdmin(holiday.getIsUpdaterAdmin()).build();
    }

    public CalendarMaster toCalendarMasterEntity(CalendarMasterRequestVO request, CalendarType calendarType,
                                                 CompanyMaster company) {
        if (request == null) {
            return null;
        }
        return CalendarMaster.builder().calendarCode(request.getCalendarCode()).calendarType(calendarType)
                .company(company).validFrom(request.getValidFrom()).validTo(request.getValidTo())
                .workFrom(request.getWorkFrom()).workTo(request.getWorkTo()).timezone(request.getTimezone())
                .createdBy(request.getCreatedBy()).isCreatorAdmin(request.getIsCreatedByAdmin())
                .updatedBy(request.getUpdatedBy()).isUpdaterAdmin(request.getIsUpdatedByAdmin()).build();
    }

    public List<CalendarHoliday> toCalendarHolidayEntity(List<CalendarHolidayVO> calendarHolidayList, Long createdBy,
                                                         CalendarMaster entity) {
        if (calendarHolidayList == null) {
            return Collections.emptyList();
        }
        return calendarHolidayList.stream().map(h -> {
            CalendarHoliday holiday = CalendarHoliday.builder().holidayDate(h.getHolidayDate())
                    .description(h.getDescription()).calendarMaster(entity).createdBy(createdBy)
                    .isCreatorAdmin(h.getIsCreatedByAdmin() != null ? h.getIsCreatedByAdmin() : entity.getIsCreatorAdmin() != null ? entity.getIsCreatorAdmin() : false).updatedBy(createdBy)
                    .isUpdaterAdmin(h.getIsUpdatedByAdmin()).build();
            return holiday;
        }).toList();
    }

    public List<CalendarWorkday> toCalendarWorkDay(List<String> workDayList, Long createdBy, CalendarMaster entity) {
        if (workDayList == null) {
            return Collections.emptyList();
        }
        return workDayList.stream().map(workingDay -> {
            CalendarWorkday workday = CalendarWorkday.builder().workday(workingDay).calendarMaster(entity)
                    .createdBy(createdBy)
                    .isCreatorAdmin(entity.getIsCreatorAdmin() != null ? entity.getIsCreatorAdmin() : false)
                    .updatedBy(createdBy).build();
            return workday;
        }).toList();
    }

    public CompanyMaster toCompanyMasterEntity(CompanyMasterRequestVO request, CountryMaster country,
                                               StateMaster stateMaster, CityMaster cityMaster) {
        if (request == null) {
            return null;
        }
        return CompanyMaster.builder().companyName(request.getCompanyName()).uid(request.getUid())
                .employeeSize(request.getEmployeeSize()).registeredNumber(request.getRegisteredNumber())
                .pinCode(request.getPinCode()).country(country).state(stateMaster).city(cityMaster)
                .addressLine1(request.getAddressLine1()).addressLine2(request.getAddressLine2()).mail(request.getMail())
                .createdBy(request.getCreatedBy())
                .isCreatorAdmin(request.getIsCreatedByAdmin() != null ? request.getIsCreatedByAdmin() : false)
                .updatedBy(request.getCreatedBy())
                .isUpdaterAdmin(request.getIsUpdatedByAdmin() != null ? request.getIsUpdatedByAdmin() : false).build();
    }

    public AgentMasterVO toAgentMasterVO(AgentMaster agent) {
        if (agent == null) {
            return null;
        }

        AgentMasterVO vo = new AgentMasterVO();
        vo.setAgentId(agent.getAgentId());
        vo.setAgentName(agent.getAgentName());
        vo.setMailId(agent.getMailId());
        vo.setAccessId(agent.getAccessId());
        vo.setOrganization(toCompanyMasterVO(agent.getOrganization()));
        vo.setCalendar(toCalendarMasterVO(agent.getCalendarMaster()));
        vo.setPhoneCode(agent.getUser().getPhoneCode());
        vo.setPhoneNumber(agent.getUser().getPhoneNumber());
        vo.setCountry(toCountryMasterVO(agent.getUser().getCountry()));
        vo.setCity(toCityMasterVO(agent.getUser().getCity()));
        vo.setLanguages(agent.getUser().getLanguages() == null ? null
                : agent.getUser().getLanguages().stream()
                .map(this::toNoBackRefUserLanguageMappingVO)
                .collect(Collectors.toList()));
        return vo;
    }

    public AgentSkillsMappingVO toAgentSkillMappingVo(AgentSkillsMapping agentSkillsMappings) {
        if (agentSkillsMappings == null) {
            return null;
        }
        return AgentSkillsMappingVO.builder().agentSkillId(agentSkillsMappings.getAgentSkillId())
                .skill(toSkillMasterVo(agentSkillsMappings.getSkill()))
                .agent(toAgentMasterVO(agentSkillsMappings.getAgent()))
                .experienceYears(agentSkillsMappings.getExperienceYears())
                .experienceMonths(agentSkillsMappings.getExperienceMonths()).build();
    }

    public List<UserVO> usersToUserVO(List<User> users) {
        if (users == null) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(user -> UserVO.builder().userId(user.getUserId()).firstName(user.getFirstName())
                        .middleName(user.getMiddleName()).lastName(user.getLastName()).email(user.getEmail())
                        .userName(user.getUserName()).role(user.getRole()).registerId(user.getRegisterId())
                        .phoneCode(user.getPhoneCode()).phoneNumber(user.getPhoneNumber())
                        .country(toCountryMasterVO(user.getCountry())).city(toCityMasterVO(user.getCity()))
                        .languages(user.getLanguages() == null ? null : user.getLanguages().stream()
                                .map(this::toNoBackRefUserLanguageMappingVO)
                                .collect(Collectors.toList()))
                        .mfaEnabled(user.isMfaEnabled())
                        .isActive(user.getIsActive()).createdBy(user.getCreatedBy())
                        .isCreatedByAdmin(user.getIsCreatorAdmin()).updatedBy(user.getUpdatedBy())
                        .isUpdatedByAdmin(user.getIsUpdaterAdmin()).build())
                .collect(Collectors.toList());
    }

    public CountryMasterVO toCountryMasterVO(CountryMaster country) {
        if (country == null) {
            return null;
        }
        return CountryMasterVO.builder().countryId(country.getCountryId()).countryName(country.getCountryName())
                .isoCode(country.getIsoCode()).phoneCode(country.getPhoneCode()).currencyCode(country.getCurrencyCode())
                .countryName(country.getCountryName()).timezone(country.getTimezone()).build();
    }

    public CityMasterVO toCityMasterVO(CityMaster cityMaster) {
        if (cityMaster == null) {
            return null;
        }
        return CityMasterVO.builder().cityId(cityMaster.getCityId()).cityName(cityMaster.getCityName())
                .cityCode(cityMaster.getCityCode()).country(toCountryMasterVO(cityMaster.getCountry()))
                .state(toStateMasterVO(cityMaster.getState())).isActive(cityMaster.getIsActive())
                .createdBy(cityMaster.getCreatedBy()).timezone(cityMaster.getTimezone())
                .isCreatedByAdmin(cityMaster.getIsCreatorAdmin() != null ? cityMaster.getIsCreatorAdmin() : false)
                .updatedBy(cityMaster.getUpdatedBy())
                .isUpdatedByAdmin(cityMaster.getIsUpdaterAdmin() != null ? cityMaster.getIsUpdaterAdmin() : false)
                .build();
    }

    private UserLanguageMappingVO toNoBackRefUserLanguageMappingVO(UserLanguageMapping userLanguageMapping) {
        if (userLanguageMapping == null) {
            return null;
        }
        return UserLanguageMappingVO.builder()
                .language(toLanguageMasterVO(userLanguageMapping.getLanguage()))
                .build();
    }

    public StateMasterVO toStateMasterVO(StateMaster state) {
        if (state == null) {
            return null;
        }
        return StateMasterVO.builder().stateId(state.getStateId()).stateName(state.getStateName())
                .stateCode(state.getStateCode()).country(toCountryMasterVO(state.getCountry()))
                .isActive(state.getIsActive()).createdBy(state.getCreatedBy())
                .isCreatedByAdmin(state.getIsCreatorAdmin() != null ? state.getIsCreatorAdmin() : false)
                .updatedBy(state.getUpdatedBy())
                .isUpdatedByAdmin(state.getIsUpdaterAdmin() != null ? state.getIsUpdaterAdmin() : false).build();
    }

    public LanguageMasterVO toLanguageMasterVO(LanguageMaster languageMaster) {
        if (languageMaster == null) {
            return null;
        }
        return LanguageMasterVO.builder().languageId(languageMaster.getLanguageId())
                .languageName(languageMaster.getLanguageName()).languageCode(languageMaster.getLanguageCode())
                .isActive(languageMaster.getIsActive()).createdBy(languageMaster.getCreatedBy())
                .isCreatedByAdmin(languageMaster.getIsCreatorAdmin()).updatedBy(languageMaster.getUpdatedBy())
                .isUpdatedByAdmin(languageMaster.getIsUpdaterAdmin()).build();
    }

    public BusinessService toBusinessServiceEntity(BusinessServiceRequestVO businessServiceRequestVO) {
        if (businessServiceRequestVO == null) {
            return null;
        }
        return BusinessService.builder().serviceName(businessServiceRequestVO.getServiceName())
                .createdBy(businessServiceRequestVO.getCreatedBy())
                .isCreatorAdmin(businessServiceRequestVO.getIsCreatedByAdmin() != null
                        ? businessServiceRequestVO.getIsCreatedByAdmin()
                        : false)
                .updatedBy(businessServiceRequestVO.getCreatedBy())
                .isUpdaterAdmin(businessServiceRequestVO.getIsUpdatedByAdmin()).build();
    }

    public ServiceOffering toServiceOfferingEntity(ServiceOfferingVO so, BusinessService saved) {
        if (so == null) {
            return null;
        }
        return ServiceOffering.builder().offeringName(so.getOfferingName()).businessService(saved)
                .createdBy(so.getCreatedBy()).isCreatorAdmin(so.getIsCreatedByAdmin()).updatedBy(so.getCreatedBy())
                .isUpdaterAdmin(so.getIsUpdatedByAdmin()).build();
    }

    public BusinessServiceVO toBusinessServiceVO(BusinessService entity) {
        if (entity == null) {
            return null;
        }
        return BusinessServiceVO.builder().serviceId(entity.getServiceId()).serviceName(entity.getServiceName())
                .serviceOfferings(entity.getServiceOfferings() == null ? null
                        : entity.getServiceOfferings().stream()
                        .map(so -> ServiceOfferingVO.builder().offeringId(so.getOfferingId())
                                .offeringName(so.getOfferingName()).createdBy(so.getCreatedBy())
                                .isCreatedByAdmin(so.getIsCreatorAdmin())
                                .isUpdatedByAdmin(so.getIsUpdaterAdmin()).updatedBy(so.getUpdatedBy()).build())
                        .toList())
                .createdBy(entity.getCreatedBy())
                .isCreatedByAdmin(entity.getIsCreatorAdmin() != null ? entity.getIsCreatorAdmin() : false)
                .updatedBy(entity.getUpdatedBy())
                .isUpdatedByAdmin(entity.getIsUpdaterAdmin() != null ? entity.getIsUpdaterAdmin() : false).build();
    }

    public RequestConfig toRequestConfigEntity(RequestConfigVO request, PlantMaster plant) {
        if (request == null) {
            return null;
        }
        return RequestConfig.builder().requestType(request.getRequestType()).requestPrefix(request.getRequestPrefix())
                .revision(request.getRevision()).rangeFrom(request.getRangeFrom()).rangeTo(request.getRangeTo())
                .callHorizonPercentage(request.getCallHorizonPercentage()).callHorizonDays(request.getCallHorizonDays()).calculateBackward(request.getCalculateBackward()).createdBy(request.getCreatedBy())
                .isCreatorAdmin(request.getIsCreatedByAdmin()).updatedBy(request.getCreatedBy())
                .isUpdaterAdmin(request.getIsUpdatedByAdmin()).build();
    }

    public List<RequestConfigVO> toRequestConfigVOList(List<RequestConfig> requestConfigs) {
        if (requestConfigs == null) {
            return Collections.emptyList();
        }
        return requestConfigs.stream().map(this::toRequestConfigVO).toList();
    }

    public RequestConfigVO toRequestConfigVO(RequestConfig requestConfig) {
        if (requestConfig == null) {
            return null;
        }
        return RequestConfigVO.builder().configId(requestConfig.getConfigId())
                .requestType(requestConfig.getRequestType()).requestPrefix(requestConfig.getRequestPrefix())
                .revision(requestConfig.getRevision()).rangeFrom(requestConfig.getRangeFrom()).callHorizonPercentage(requestConfig.getCallHorizonPercentage())
                .callHorizonDays(requestConfig.getCallHorizonDays()).rangeTo(requestConfig.getRangeTo()).calculateBackward(requestConfig.getCalculateBackward())
                .createdBy(requestConfig.getCreatedBy()).isCreatedByAdmin(requestConfig.getIsCreatorAdmin()).isActive(requestConfig.getIsActive() != null ? requestConfig.getIsActive() : false)
                .isEnabled(requestConfig.getIsEnabled() != null ? requestConfig.getIsEnabled() : false).updatedBy(requestConfig.getUpdatedBy()).isUpdatedByAdmin(requestConfig.getIsUpdaterAdmin()).build();
    }

    public ImpactMaster toImpactMaster(ImpactRequestVO request, CompanyMaster companyMaster) {
        if (request == null) {
            return null;
        }
        return ImpactMaster.builder().impactId(request.getImpactId()).impactCode(request.getImpactCode())
                .organization(companyMaster).impactLevel(request.getImpactLevel()).createdBy(request.getCreatedBy())
                .isCreatorAdmin(request.getIsCreatedByAdmin()).updatedBy(request.getUpdatedBy())
                .isUpdaterAdmin(request.getIsUpdatedByAdmin() != null ? request.getIsUpdatedByAdmin() : false).build();
    }

    public ImpactMasterVO toImpactMasterVo(ImpactMaster save) {
        if (save == null) {
            return null;
        }
        return ImpactMasterVO.builder().impactId(save.getImpactId()).impactCode(save.getImpactCode())
                .organization(toCompanyMasterVO(save.getOrganization())).impactLevel(save.getImpactLevel())
                .isActive(save.getIsActive()).createdBy(save.getCreatedBy())
                .isCreatedByAdmin(save.getIsCreatorAdmin()).updatedBy(save.getUpdatedBy())
                .isUpdatedByAdmin(save.getIsUpdaterAdmin() != null ? save.getIsUpdaterAdmin() : false).build();
    }

    public CompanyMasterVO toCompanyMasterVO(CompanyMaster entity) {
        if (entity == null) {
            return null;
        }
        return CompanyMasterVO.builder().companyId(entity.getCompanyId()).companyName(entity.getCompanyName())
                .phoneCode(entity.getPhoneCode()).registeredNumber(entity.getRegisteredNumber())
                .country(toCountryMasterVO(entity.getCountry())).state(toStateMasterVO(entity.getState()))
                .city(toCityMasterVO(entity.getCity())).addressLine1(entity.getAddressLine1())
                .addressLine2(entity.getAddressLine2()).pinCode(entity.getPinCode()).isActive(entity.getIsActive())
                .approvers(entity.getApprovers() == null ? null
                        : entity.getApprovers().stream().filter(CompanyApprover::getIsActive)
                        .map(approver -> CompanyApproverVO.builder()
                                .approverId(approver.getApproverId())
                                .company(null)
                                .agent(toCompanyApproverAgentVO(approver.getAgent()))
                                .level(approver.getLevel()).isActive(approver.getIsActive())
                                .createdBy(approver.getCreatedBy())
                                .isCreatedByAdmin(
                                        approver.getIsCreatorAdmin() != null ? approver.getIsCreatorAdmin() : false)
                                .updatedBy(approver.getUpdatedBy())
                                .isUpdatedByAdmin(
                                        approver.getIsUpdaterAdmin() != null ? approver.getIsUpdaterAdmin() : false)
                                .build())
                        .toList())
                .createdBy(entity.getCreatedBy())
                .isCreatedByAdmin(entity.getIsCreatorAdmin() != null ? entity.getIsCreatorAdmin() : false)
                .uid(entity.getUid()).employeeSize(entity.getEmployeeSize()).mail(entity.getMail())
                .updatedBy(entity.getUpdatedBy())
                .isUpdatedByAdmin(entity.getIsUpdaterAdmin() != null ? entity.getIsUpdaterAdmin() : false).build();
    }

    private AgentMasterVO toCompanyApproverAgentVO(AgentMaster agent) {
        if (agent == null) {
            return null;
        }
        return AgentMasterVO.builder()
                .agentId(agent.getAgentId())
                .agentName(agent.getAgentName())
                .build();
    }

    public UserVO userToUserVO(User user) {
        if (user == null) {
            return null;
        }
        return UserVO.builder().userId(user.getUserId()).firstName(user.getFirstName()).lastName(user.getLastName())
                .email(user.getEmail()).userName(user.getUserName()).role(user.getRole())
                .middleName(user.getMiddleName()).registerId(user.getRegisterId()).phoneCode(user.getPhoneCode())
                .phoneNumber(user.getPhoneNumber()).country(toCountryMasterVO(user.getCountry()))
                .city(toCityMasterVO(user.getCity()))
                .languages(user.getLanguages() == null ? null : user.getLanguages().stream()
                        .map(this::toNoBackRefUserLanguageMappingVO)
                        .collect(Collectors.toList()))
                .mfaEnabled(user.isMfaEnabled()).agent(toAgentMasterVO(user.getAgent())).isActive(user.getIsActive())
                .createdBy(user.getCreatedBy())
                .isCreatedByAdmin(user.getIsCreatorAdmin() != null ? user.getIsCreatorAdmin() : false)
                .updatedBy(user.getUpdatedBy())
                .isUpdatedByAdmin(user.getIsUpdaterAdmin() != null ? user.getIsUpdaterAdmin() : false).build();
    }

    public EnquiryRegistration enquiryRegisterRequestToEnquiryRegistration(EnquiryRegisterRequestVO request,
                                                                           CountryMaster country, String role, CompanyMaster company) {
        if (request == null) {
            return null;
        }
        return EnquiryRegistration.builder().firstName(request.getFirstName()).middleName(request.getMiddleName())
                .lastName(request.getLastName()).email(request.getEmail()).phoneNumber(request.getPhoneNumber())
                .phoneCode(request.getPhoneCode()).userName(request.getEmail()).userRole(role).company(company)
                .country(country).build();
    }

    public EnquiryInfoVO toEnquiryInfoVo(EnquiryInfo enquiryInfo) {
        if (enquiryInfo == null) {
            return null;
        }
        return EnquiryInfoVO.builder().id(enquiryInfo.getId()).token(enquiryInfo.getToken()).used(enquiryInfo.getUsed())
                .enquiryRegistration(toEnquiryRegistrationVO(enquiryInfo.getEnquiryRegistration()))
                .expiryTime(enquiryInfo.getExpiryTime()).build();
    }

    public EnquiryRegistrationVO toEnquiryRegistrationVO(EnquiryRegistration enquiryRegistration) {
        if (enquiryRegistration == null) {
            return null;
        }
        return EnquiryRegistrationVO.builder().enquiryId(enquiryRegistration.getEnquiryId())
                .firstName(enquiryRegistration.getFirstName()).middleName(enquiryRegistration.getMiddleName())
                .lastName(enquiryRegistration.getLastName()).email(enquiryRegistration.getEmail())
                .phoneNumber(enquiryRegistration.getPhoneNumber()).phoneCode(enquiryRegistration.getPhoneCode())
                .company(toCompanyMasterVO(enquiryRegistration.getCompany()))
                .userRole(enquiryRegistration.getUserRole())
                .country(toCountryMasterVO(enquiryRegistration.getCountry()))
                .state(toStateMasterVO(enquiryRegistration.getState()))
                .city(toCityMasterVO(enquiryRegistration.getCity())).build();
    }

    public CalendarType toCalendarTypeEntity(String type, CompanyMaster company, Long createBy) {
        if (type == null) {
            return null;
        }
        return CalendarType.builder().typeName(type).company(company).createdBy(createBy)
                .isCreatorAdmin(company.getIsCreatorAdmin() != null ? company.getIsCreatorAdmin() : false)
                .updatedBy(createBy)
                .isUpdaterAdmin(company.getIsUpdaterAdmin() != null ? company.getIsUpdaterAdmin() : false).build();
    }

    public CalendarTypeVO toCalendarTypeVO(CalendarType entity) {
        if (entity == null) {
            return null;
        }
        return CalendarTypeVO.builder().calendarTypeId(entity.getCalendarTypeId()).typeName(entity.getTypeName())
                .company(toCompanyMasterVO(entity.getCompany())).isActive(entity.getIsActive())
                .createdBy(entity.getCreatedBy())
                .isCreatedByAdmin(entity.getIsCreatorAdmin() != null ? entity.getIsCreatorAdmin() : false)
                .updatedBy(entity.getUpdatedBy())
                .isUpdatedByAdmin(entity.getIsUpdaterAdmin() != null ? entity.getIsUpdaterAdmin() : false).build();
    }

    public Project toProject(ProjectRequestVO request, CompanyMaster company) {
        if (request == null) {
            return null;
        }

        return Project.builder().company(company).projectName(request.getProjectName())
                .projectDesc(request.getProjectName()).projectDesc(request.getProjectDesc())
                .projectCode(request.getProjectName().toUpperCase().replaceAll("\\s+", "_")).isSaved(request.isSave())
                .isSubmited(request.isSubmit()).createdBy(request.getCreatedBy())
                .isCreatorAdmin(request.getIsCreatedByAdmin() != null ? request.getIsCreatedByAdmin() : false).build();
    }

    public ProgressStatusVO toProgressStatusVO(ProgressStatus progressStatus) {
        if (progressStatus == null) {
            return null;
        }
        return ProgressStatusVO.builder().progressId(progressStatus.getProgressId())
                .company(toCompanyMasterVO(progressStatus.getCompany())).progressName(progressStatus.getProgressName())
                .progressSequence(progressStatus.getProgressSequence()).colorCode(progressStatus.getColorCode())
                .updatedBy(progressStatus.getUpdatedBy()).isUpdatedByAdmin(progressStatus.getIsUpdaterAdmin()).build();
    }

    public EpicVO toEpicVO(Epic epic) {
        if (epic == null) {
            return null;
        }
        // Create a minimal ProjectVO to avoid circular reference
        ProjectVO projectVO = ProjectVO.builder().projectId(epic.getProject().getProjectId()).build();

        return EpicVO.builder().epicId(epic.getEpicId()).project(projectVO).epicName(epic.getEpicName())
                .epicDesc(epic.getEpicDesc()).epicSequence(epic.getEpicSequence())
                .progress(toProgressStatusVO(epic.getProgressStatus())).plannedStartDate(epic.getPlannedStartDate())
                .plannedEndDate(epic.getPlannedEndDate())
                .userStories(
                        epic.getUserStories() != null ? epic.getUserStories().stream().map(this::toUserStoryVO).toList()
                                : null)
                .isActive(epic.getIsActive()).createdBy(epic.getCreatedBy()).isCreatedByAdmin(epic.getIsCreatorAdmin())
                .isUpdatedByAdmin(epic.getIsUpdaterAdmin()).updatedBy(epic.getUpdatedBy()).build();
    }

    public UserStoryVO toUserStoryVO(UserStory userStory) {
        if (userStory == null) {
            return null;
        }
        // Create a minimal EpicVO to avoid circular reference
        EpicVO epicVO = EpicVO.builder().epicId(userStory.getEpic().getEpicId()).build();

        return UserStoryVO.builder().storyId(userStory.getStoryId()).epicId(epicVO)
                .progress(toProgressStatusVO(userStory.getProgressStatus())).storyCode(userStory.getStoryCode())
                .title(userStory.getTitle()).description(userStory.getDescription())
                .storySequence(userStory.getStorySequence()).agentId(toAgentMasterVO(userStory.getAgent()))
                .plannedStartDate(userStory.getPlannedStartDate()).plannedEndDate(userStory.getPlannedEndDate())
                .actualStartDate(userStory.getActualStartDate()).actualEndDate(userStory.getActualEndDate())
                .predecessorId(userStory.getPredecessor() != null ? userStory.getPredecessor().getStoryId() : null)
                .leads(userStory.getProjectLeadAssignments() != null
                        ? userStory.getProjectLeadAssignments().stream().map(this::toProjectLeadAssignmentVO).toList()
                        : null)
                .storyPoints(userStory.getStoryPoints()).isActive(userStory.getIsActive())
                .createdBy(userStory.getCreatedBy()).isCreatedByAdmin(userStory.getIsCreatorAdmin())
                .isUpdatedByAdmin(userStory.getIsUpdaterAdmin()).updatedBy(userStory.getUpdatedBy()).build();
    }

    public ProjectLeadAssignmentVO toProjectLeadAssignmentVO(ProjectLeadAssignment pla) {
        if (pla == null) {
            return null;
        }
        // Create a minimal UserStoryVO to avoid circular reference
        UserStoryVO userStoryVO = UserStoryVO.builder().storyId(pla.getUserStory().getStoryId()).build();

        return ProjectLeadAssignmentVO.builder().assignmentId(pla.getAssignmentId())
                .company(toCompanyMasterVO(pla.getCompany())).story(userStoryVO).isActive(pla.getIsActive())
                .createdBy(pla.getCreatedBy())
                .isCreatedByAdmin(pla.getIsCreatorAdmin() != null ? pla.getIsCreatorAdmin() : false)
                .updatedBy(pla.getUpdatedBy()).isUpdatedByAdmin(pla.getIsUpdaterAdmin()).build();
    }

    public ProjectVO toProjectVO(Project project) {
        if (project == null) {
            return null;
        }
        return ProjectVO.builder().projectId(project.getProjectId()).company(toCompanyMasterVO(project.getCompany()))
                .projectCode(project.getProjectCode()).projectName(project.getProjectName())
                .projectDesc(project.getProjectDesc())
                .epics(project.getEpics() != null ? project.getEpics().stream().map(this::toEpicVO).toList() : null)
                .plannedStartDate(project.getPlannedStartDate()).plannedEndDate(project.getPlannedEndDate())
                .isActive(project.getIsActive()).createdBy(project.getCreatedBy())
                .isCreatedByAdmin(project.getIsCreatorAdmin() != null ? project.getIsCreatorAdmin() : false)
                .updatedBy(project.getUpdatedBy())
                .isUpdatedByAdmin(project.getIsUpdaterAdmin() != null ? project.getIsUpdaterAdmin() : false)
                .isSaved(project.getIsSaved()).isSubmitted(project.getIsSubmited()).build();
    }

    public Epic toEpic(EpicVO epicVO, Project project, ProgressStatus defaultProgressStatus, int maxProgressStatus) {
        if (epicVO == null) {
            return null;
        }
        return Epic.builder().epicName(epicVO.getEpicName()).epicDesc(epicVO.getEpicDesc())
                .epicSequence(epicVO.getEpicSequence()).progressStatus(defaultProgressStatus)
                .maxProgressStatus(maxProgressStatus).project(project).build();
    }

    public ProjectVO toNoBackRefProjectVO(Project project) {
        if (project == null) {
            return null;
        }
        return ProjectVO.builder().projectId(project.getProjectId()).company(toCompanyMasterVO(project.getCompany()))
                .projectCode(project.getProjectCode()).projectName(project.getProjectName())
                .projectDesc(project.getProjectDesc()).isSaved(project.getIsSaved())
                .isSubmitted(project.getIsSubmited())
                .epics(project.getEpics() != null ? project.getEpics().stream().map(this::toNoBackRefEpicVO).toList()
                        : null)
                .plannedStartDate(project.getPlannedStartDate()).plannedEndDate(project.getPlannedEndDate())
                .isActive(project.getIsActive()).createdBy(project.getCreatedBy())
                .isCreatedByAdmin(project.getIsCreatorAdmin()).updatedBy(project.getUpdatedBy())
                .isUpdatedByAdmin(project.getIsUpdaterAdmin() != null ? project.getIsUpdaterAdmin() : false).build();
    }

    public EpicVO toNoBackRefEpicVO(Epic epic) {
        if (epic == null) {
            return null;
        }
        // Create a minimal ProjectVO to avoid circular reference
//		ProjectVO projectVO = ProjectVO.builder().projectId(epic.getProject().getProjectId()).build();

        return EpicVO.builder().epicId(epic.getEpicId()).project(null).epicName(epic.getEpicName())
                .epicDesc(epic.getEpicDesc()).epicSequence(epic.getEpicSequence())
                .progress(toNoBackRefProgressStatusVO(epic.getProgressStatus()))
                .maxProgress(epic.getMaxProgressStatus()).plannedStartDate(epic.getPlannedStartDate())
                .plannedEndDate(epic.getPlannedEndDate())
                .userStories(epic.getUserStories() != null
                        ? epic.getUserStories().stream().map(this::toNoBackRefUserStoryVO).toList()
                        : null)
                .isActive(epic.getIsActive()).createdBy(epic.getCreatedBy())
                .isCreatedByAdmin(epic.getIsCreatorAdmin() != null ? epic.getIsCreatorAdmin() : false)
                .updatedBy(epic.getUpdatedBy())
                .isUpdatedByAdmin(epic.getIsUpdaterAdmin() != null ? epic.getIsUpdaterAdmin() : false).build();
    }

    public ProgressStatusVO toNoBackRefProgressStatusVO(ProgressStatus progressStatus) {
        if (progressStatus == null) {
            return null;
        }
        return ProgressStatusVO.builder().progressId(progressStatus.getProgressId()).company(null)
                .progressName(progressStatus.getProgressName()).progressSequence(progressStatus.getProgressSequence())
                .colorCode(progressStatus.getColorCode()).updatedBy(progressStatus.getUpdatedBy())
                .isUpdatedByAdmin(progressStatus.getIsUpdaterAdmin()).build();
    }

    public UserStoryVO toNoBackRefUserStoryVO(UserStory userStory) {
        if (userStory == null) {
            return null;
        }

        return UserStoryVO.builder().storyId(userStory.getStoryId()).epicId(null)
                .progress(toNoBackRefProgressStatusVO(userStory.getProgressStatus()))
                .maxProgress(userStory.getMaxProgressStatus()).storyCode(userStory.getStoryCode())
                .title(userStory.getTitle()).description(userStory.getDescription())
                .tasks(userStory.getTasks() != null
                        ? userStory.getTasks().stream().map(this::toNoBackRefTaskVO).toList()
                        : null)
                .storySequence(userStory.getStorySequence()).agentId(toAgentMasterVO(userStory.getAgent()))
                .plannedStartDate(userStory.getPlannedStartDate()).plannedEndDate(userStory.getPlannedEndDate())
                .actualStartDate(userStory.getActualStartDate()).actualEndDate(userStory.getActualEndDate())
                .predecessorId(userStory.getPredecessor() != null ? userStory.getPredecessor().getStoryId() : null)
                .leads(userStory.getProjectLeadAssignments() != null ? userStory.getProjectLeadAssignments().stream()
                        .map(this::toNoBackRefProjectLeadAssignmentVO).toList() : null)
                .storyPoints(userStory.getStoryPoints()).isActive(userStory.getIsActive())
                .createdBy(userStory.getCreatedBy()).isCreatedByAdmin(userStory.getIsCreatorAdmin())
                .isUpdatedByAdmin(userStory.getIsUpdaterAdmin()).updatedBy(userStory.getUpdatedBy()).build();
    }

    public TaskVO toNoBackRefTaskVO(Task task) {
        if (task == null) {
            return null;
        }

        return TaskVO.builder().title(task.getTitle()).description(task.getDescription()).taskId(task.getTaskId())
                .progress(toNoBackRefProgressStatusVO(task.getProgressStatus())).taskSequence(task.getTaskSequence())
                .agentId(toAgentMasterVO(task.getAgent())).maxProgress(task.getMaxProgressStatus())
                .plannedStartDate(task.getPlannedStartDate()).plannedEndDate(task.getPlannedEndDate())
                .actualStartDate(task.getActualStartDate()).actualEndDate(task.getActualEndDate())
                .subTasks(task.getSubTasks() != null
                        ? task.getSubTasks().stream().map(this::toNoBackRefSubTaskVO).toList()
                        : null)
                .build();
    }

    public SubTaskVO toNoBackRefSubTaskVO(SubTask subTask) {
        if (subTask == null) {
            return null;
        }

        return SubTaskVO.builder().title(subTask.getTitle()).description(subTask.getDescription())
                .subTaskId(subTask.getSubTaskId()).progress(toNoBackRefProgressStatusVO(subTask.getProgressStatus()))
                .agentId(toAgentMasterVO(subTask.getAgent())).maxProgress(subTask.getMaxProgressStatus())
                .plannedStartDate(subTask.getPlannedStartDate()).plannedEndDate(subTask.getPlannedEndDate())
                .actualStartDate(subTask.getActualStartDate()).actualEndDate(subTask.getActualEndDate())
                .createdBy(subTask.getCreatedBy())
                .isCreatedByAdmin(subTask.getIsCreatorAdmin() != null ? subTask.getIsCreatorAdmin() : false).build();
    }

    public ProjectLeadAssignmentVO toNoBackRefProjectLeadAssignmentVO(ProjectLeadAssignment pla) {
        if (pla == null) {
            return null;
        }
        return ProjectLeadAssignmentVO.builder().assignmentId(pla.getAssignmentId())
                .company(toNoBackRefCompanyMasterVO(pla.getCompany())).story(null).isActive(pla.getIsActive())
                .createdBy(pla.getCreatedBy()).isCreatedByAdmin(pla.getIsCreatorAdmin())
                .isUpdatedByAdmin(pla.getIsUpdaterAdmin() != null ? pla.getIsUpdaterAdmin() : false)
                .updatedBy(pla.getUpdatedBy()).build();
    }

    public WorkItemVO toWorkItemVO(WorkItem workItem) {
        if (workItem == null) {
            return null;
        }
        return WorkItemVO.builder().itemId(workItem.getItemId()).code(workItem.getCode()).label(workItem.getLabel())
                .isActive(workItem.getIsActive()).createdBy(workItem.getCreatedBy())
                .isCreatedByAdmin(workItem.getIsCreatorAdmin()).isUpdatedByAdmin(workItem.getIsUpdaterAdmin())
                .updatedBy(workItem.getUpdatedBy()).build();
    }

    public FieldTypeVO toFieldTypeVO(FieldType fieldType) {
        if (fieldType == null) {
            return null;
        }
        return FieldTypeVO.builder().typeId(fieldType.getTypeId()).code(fieldType.getCode()).label(fieldType.getLabel())
                .isActive(fieldType.getIsActive()).createdBy(fieldType.getCreatedBy())
                .isCreatedByAdmin(fieldType.getIsCreatorAdmin()).isUpdatedByAdmin(fieldType.getIsUpdaterAdmin())
                .updatedBy(fieldType.getUpdatedBy()).build();
    }

    public TemplateDetailsVO toTemplateDetailsVO(TemplateDetails entity) {
        if (entity == null) {
            return null;
        }
        return TemplateDetailsVO.builder().templateId(entity.getTemplateId()).templateName(entity.getTemplateName())
                .workItemId(entity.getWorkItem().getItemId()).company(toCompanyMasterVO(entity.getCompany()))
                .templateDetails(entity.getFields() != null
                        ? entity.getFields().stream().map(this::toTemplateDetailFieldVO).toList()
                        : null)
                .isActive(entity.getIsActive()).createdBy(entity.getCreatedBy())
                .isCreatedByAdmin(entity.getIsCreatorAdmin()).isUpdatedByAdmin(entity.getIsUpdaterAdmin())
                .updatedBy(entity.getUpdatedBy()).build();
    }

    public com.flickzz.desk.vo.TemplateDetailFieldVO toTemplateDetailFieldVO(TemplateDetailField field) {
        if (field == null) {
            return null;
        }
        return TemplateDetailFieldVO.builder().fieldId(field.getFieldId()).fieldName(field.getFieldName())
                .fieldTypeId(field.getFieldType().getTypeId()).mandatory(field.getMandatory())
                .fieldSequence(field.getFieldSequence()).isActive(field.getIsActive())
                .options(field.getOptions() != null
                        ? field.getOptions().stream().map(this::toTemplateFieldOptionVO).toList()
                        : null)
                .build();
    }

    public com.flickzz.desk.vo.TemplateFieldOptionVO toTemplateFieldOptionVO(TemplateFieldOption option) {
        if (option == null) {
            return null;
        }
        return TemplateFieldOptionVO.builder().optionId(option.getOptionId()).label(option.getLabel())
                .value(option.getValue()).defaultSelected(option.getDefaultSelected())
                .optionSequence(option.getOptionSequence()).isActive(option.getIsActive()).build();
    }

    public BPPriority toBPPriority(BpConfigRequestVO request, BPConfiguration config, TicketTypeMaster ticketType) {

        if (request == null) {
            return null;
        }
        return BPPriority.builder().configuration(config).level(request.getLevel()).code(request.getCode())
                .ticketType(ticketType).description(request.getDescription()).createdBy(request.getCreatedBy())
                .updatedBy(request.getUpdatedBy()).build();
    }

    public BPSla toBPSla(BpConfigRequestVO request, BPConfiguration config, BPPriority bpPriority) {

        if (request == null) {
            return null;
        }
        return BPSla.builder().configuration(config).priority(bpPriority)
                .firstResponseTime(request.getFirstResponseTime()).firstResponseTerm(request.getFirstResponseTerm())
                .resolutionTime(request.getResolutionTime()).resolutionTerm(request.getResolutionTerm())
                .updateFrequency(request.getUpdateFrequency()).updateFrequencyTerm(request.getUpdateFrequencyTerm())
                .createdBy(request.getCreatedBy())
                .updatedBy(request.getUpdatedBy() == null ? request.getCreatedBy() : request.getUpdatedBy()).build();
    }

    public BPSlaVO toBPSlaVo(BPSla bpSla) {

        if (bpSla == null) {
            return null;
        }
        return BPSlaVO.builder().slaId(bpSla.getSlaId()).configuration(toBPConfigurationVO(bpSla.getConfiguration()))
                .priority(toBPPriorityVo(bpSla.getPriority())).firstResponseTime(bpSla.getFirstResponseTime())
                .firstResponseTerm(bpSla.getFirstResponseTerm()).resolutionTime(bpSla.getResolutionTime())
                .resolutionTerm(bpSla.getResolutionTerm()).updateFrequency(bpSla.getUpdateFrequency())
                .updateFrequencyTerm(bpSla.getUpdateFrequencyTerm()).isActive(bpSla.getIsActive())
                .isUnderApproval(bpSla.getIsUnderApproval()).createdBy(bpSla.getCreatedBy())
                .updatedBy(bpSla.getUpdatedBy()).build();
    }

    public BPCategory toBPCategory(BpConfigRequestVO request, BPConfiguration config) {

        if (request == null) {
            return null;
        }
        return BPCategory.builder().configuration(config).categoryName(request.getCategoryName())
                .createdBy(request.getCreatedBy()).updatedBy(request.getUpdatedBy()).build();
    }

    public BPCategoryVO toBPCategoryVo(BPCategory bpCategory) {
        if (bpCategory == null) {
            return null;
        }
        List<BPSubCategoryVO> subCategories = null;
        if (bpCategory.getSubCategories() != null && !bpCategory.getSubCategories().isEmpty()) {
            subCategories = bpCategory.getSubCategories().stream().map(this::toNoBackRefBPSubCategoryVo).toList();
        }
        return BPCategoryVO.builder().categoryId(bpCategory.getCategoryId())
                .configuration(toBPConfigurationVO(bpCategory.getConfiguration()))
                .categoryName(bpCategory.getCategoryName()).isActive(bpCategory.getIsActive()).isUnderApproval(bpCategory.getIsUnderApproval())
                .createdBy(bpCategory.getCreatedBy()).updatedBy(bpCategory.getUpdatedBy()).subCategories(subCategories)
                .build();
    }

    public BPSubCategoryVO toBPSubCategoryVo(BPSubCategory bpSubCategory) {
        if (bpSubCategory == null) {
            return null;
        }
        return BPSubCategoryVO.builder().subCategoryId(bpSubCategory.getSubCategoryId())
                .category(toBPCategoryVo(bpSubCategory.getCategory()))
                .subCategoryName(bpSubCategory.getSubCategoryName()).isActive(bpSubCategory.getIsActive())
                .createdBy(bpSubCategory.getCreatedBy()).updatedBy(bpSubCategory.getUpdatedBy()).build();
    }

    public BPSupportGroupVO toSupportGroupVo(BPSupportGroup supportGroup) {
        if (supportGroup == null) {
            return null;
        }
        return BPSupportGroupVO.builder().supportGroupId(supportGroup.getSupportGroupId())
                .configuration(toBPConfigurationVO(supportGroup.getConfiguration()))
                .members(supportGroup.getMembers() != null
                        ? supportGroup.getMembers().stream().filter(member -> Boolean.TRUE.equals(member.getIsActive()))
                        .map(this::toSupportGroupMemberVo).toList()
                        : null)
                .managers(supportGroup.getManagers() != null
                        ? supportGroup.getManagers().stream().filter(manager -> Boolean.TRUE.equals(manager.getIsActive()))
                        .map(this::toSupportGroupManagerVo).toList()
                        : null)
                .groupName(supportGroup.getGroupName()).isActive(supportGroup.getIsActive())
                .createdBy(supportGroup.getCreatedBy()).updatedBy(supportGroup.getUpdatedBy()).build();
    }

    private BPSupportGroupManagerVO toSupportGroupManagerVo(BPSupportGroupManager bpSupportGroupManager) {
        if (bpSupportGroupManager == null) {
            return null;
        }
        return BPSupportGroupManagerVO.builder().managerId(bpSupportGroupManager.getManagerId()).supportGroup(null)
                .agent(toAgentMasterVO(bpSupportGroupManager.getAgent())).isActive(bpSupportGroupManager.getIsActive())
                .isInternal(bpSupportGroupManager.getIsInternal()).isBP(bpSupportGroupManager.getIsBP()).build();
    }

    public BPSupportGroupMemberVO toSupportGroupMemberVo(BPSupportGroupMember supportGroupMember) {
        if (supportGroupMember == null) {
            return null;
        }
        return BPSupportGroupMemberVO.builder().memberId(supportGroupMember.getMemberId()).supportGroup(null)
                .agent(toAgentMasterVO(supportGroupMember.getAgent())).isGroupLead(supportGroupMember.getIsGroupLead())
                .isActive(supportGroupMember.getIsActive()).build();
    }

    public BPAssignmentVO toBPAssignmentVo(BPAssignment assignment) {
        if (assignment == null) {
            return null;
        }
        return BPAssignmentVO.builder().assignmentId(assignment.getAssignmentId())
                .configuration(toBPConfigurationVO(assignment.getConfiguration()))
                .subCategory(toBPSubCategoryVo(assignment.getSubCategory()))
                .supportGroup(toSupportGroupVo(assignment.getSupportGroup())).isActive(assignment.getIsActive())
                .isUnderApproval(assignment.getIsUnderApproval())
                .createdBy(assignment.getCreatedBy()).updatedBy(assignment.getUpdatedBy()).build();
    }

    public List<PlantWeekoff> toWeekOffEntity(List<String> weekOff, PlantMaster plant) {
        if (weekOff == null) {
            return Collections.EMPTY_LIST;
        }
        return weekOff.stream().map(off -> PlantWeekoff.builder().weekoff(off).plant(plant)
                .createdBy(plant.getCreatedBy()).isCreatorAdmin(plant.getIsCreatorAdmin())
                .updatedBy(plant.getUpdatedBy()).isUpdaterAdmin(plant.getIsUpdaterAdmin() != null ? plant.getIsUpdaterAdmin() : false)
                .build()).toList();

    }

    public CompanyApproverVO toCompanyApproverVO(CompanyApprover entity) {
        if (entity == null) {
            return null;
        }
        CompanyApproverVO vo = new CompanyApproverVO();
        vo.setApproverId(entity.getApproverId());
        vo.setAgent(toCompanyApproverAgentVO(entity.getAgent()));
        vo.setLevel(entity.getLevel());
        vo.setIsActive(entity.getIsActive());
        vo.setCreatedBy(entity.getCreatedBy());
        vo.setIsCreatedByAdmin(entity.getIsCreatorAdmin() != null ? entity.getIsCreatorAdmin() : false);
        vo.setUpdatedBy(entity.getUpdatedBy());
        vo.setIsUpdatedByAdmin(entity.getIsUpdaterAdmin() != null ? entity.getIsUpdaterAdmin() : false);
        return vo;
    }

    public CompanyApprover toCompanyApproverEntity(AgentMaster agent, CompanyMaster company, Integer level, Long createdBy, Boolean isCreatorAdmin) {
        if (agent == null || company == null) {
            return null;
        }
        return CompanyApprover.builder()
                .agent(agent)
                .company(company)
                .level(level)
                .createdBy(createdBy)
                .isCreatorAdmin(isCreatorAdmin != null ? isCreatorAdmin : false)
                .updatedBy(createdBy)
                .isUpdaterAdmin(false)
                .build();
    }

    public SystemAuditRequest toSystemAuditRequest(String module, String area, String entityName, Long entityId, String action, String newValue, String oldValue, String changedFields, Long userId, String userName, Long companyId, String status, String errorMessage) {
        return SystemAuditRequest.builder()
                .module(module)
                .area(area)
                .entityName(entityName)
                .entityId(entityId)
                .action(action)
                .newValue(newValue)
                .oldValue(oldValue)
                .changedFields(changedFields)
                .userId(userId)
                .userName(userName)
                .companyId(companyId)
                .status(status)
                .errorMessage(errorMessage)
                .build();
    }

    public SystemAuditVO toSystemAuditVO(SystemAudit systemAudit) {
        if (systemAudit == null) {
            return null;
        }
        return SystemAuditVO.builder()
                .auditId(systemAudit.getAuditId())
                .module(systemAudit.getModule())
                .area(systemAudit.getArea())
                .entityName(systemAudit.getEntityName())
                .entityId(systemAudit.getEntityId())
                .action(systemAudit.getAction())
                .newValue(systemAudit.getNewValue())
                .oldValue(systemAudit.getOldValue())
                .changedFields(systemAudit.getChangedFields())
                .changedBy(systemAudit.getUserId())
                .userName(systemAudit.getUserName())
                .companyId(systemAudit.getCompanyId())
                .status(systemAudit.getStatus())
                .errorMessage(systemAudit.getErrorMessage())
                .createdAt(systemAudit.getCreatedAt())
                .build();
    }

    public ConfigChangeNotificationVO toNotificationVO(ConfigChangeNotification notification) {
        if (notification == null) {
            return null;
        }

        return ConfigChangeNotificationVO.builder()
                .notificationId(notification.getNotificationId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .notificationType(notification.getNotificationType())
                .action(notification.getAction())
                .referenceType(notification.getReferenceType())
                .referenceId(notification.getReferenceId())
                .triggeredByUser(notification.getTriggeredByUser())
                .triggeredUserOrg(notification.getTriggeredUserOrg())
                .recipientUserId(notification.getRecipientUserId())
                .recipientUserName(notification.getRecipientUserName())
                .recipientOrgId(notification.getRecipientOrgId())
                .isRead(notification.getIsRead())
                .active(notification.getIsActive())
                .createdBy(notification.getCreatedBy())
                .createdOn(notification.getCreatedOn())
                .updatedBy(notification.getUpdatedBy())
                .updatedOn(notification.getUpdatedOn())
                .build();
    }

    public ConfigChangeApprovalVO toConfigChangeApprovalVO(ConfigChangeApproval approval) {
        if (approval == null) {
            return null;
        }

        return ConfigChangeApprovalVO.builder()
                .approvalId(approval.getApprovalId())
                .changeRequest(approval.getConfigChangeRequest() != null ? toBPConfigurationChangeRequestVO(approval.getConfigChangeRequest()) : null)
                .approvalType(approval.getApprovalType())
                .approverLevel(approval.getApproverLevel())
                .approverUserId(approval.getApproverUserId())
                .approverOrgId(approval.getApproverOrgId())
                .status(approval.getStatus())
                .approverType(approval.getApproverType())
                .mandatory(approval.getMandatory())
                .approvedOn(approval.getApprovedOn())
                .remarks(approval.getRemark() != null ? approval.getRemark().stream().map(this::toBPConfigurationChangeRequestRemarkVO).toList() : List.of())
                .createdBy(approval.getCreatedBy())
                .createdOn(approval.getCreatedOn())
                .updatedBy(approval.getUpdatedBy())
                .updatedOn(approval.getUpdatedOn())
                .build();
    }

    public BPConfigurationChangeRequestVO toBPConfigurationChangeRequestVO(BPConfigurationChangeRequest changeRequest) {
        if (changeRequest == null) {
            return null;
        }

        return BPConfigurationChangeRequestVO.builder()
                .ccrId(changeRequest.getCcrId())
                .configuration(changeRequest.getConfiguration() != null ? toBPConfigurationVO(changeRequest.getConfiguration()) : null)
                .changedRequestId(changeRequest.getChangedRequestId())
                .sourceChangeId(changeRequest.getSourceChangeId())
                .bpPriority(changeRequest.getBpPriority())
                .bpSla(changeRequest.getBpSla())
                .category(changeRequest.getCategory())
                .supportGroup(changeRequest.getSupportGroup())
                .assignment(changeRequest.getAssignment())
                .operation(changeRequest.getOperation())
                .requestedByOrg(changeRequest.getRequestedByOrg() != null ? toCompanyMasterVO(changeRequest.getRequestedByOrg()) : null)
                .requestedByUserId(changeRequest.getRequestedByUserId())
                .approvalOrg(changeRequest.getApprovalOrg() != null ? toCompanyMasterVO(changeRequest.getApprovalOrg()) : null)
                .internalApprovalCompleted(changeRequest.getInternalApprovalCompleted())
                .bpApprovalCompleted(changeRequest.getBpApprovalCompleted())
                .status(changeRequest.getStatus())
                .totalInternalApprovalLevels(changeRequest.getTotalInternalApprovalLevels())
                .currentInternalApprovalLevel(changeRequest.getCurrentInternalApprovalLevel())
                .totalBpApprovalLevels(changeRequest.getTotalBpApprovalLevels())
                .currentBpApprovalLevel(changeRequest.getCurrentBpApprovalLevel())
                .createdOn(changeRequest.getCreatedOn() != null ? java.sql.Timestamp.valueOf(changeRequest.getCreatedOn()) : null)
                .updatedOn(changeRequest.getUpdatedOn() != null ? java.sql.Timestamp.valueOf(changeRequest.getUpdatedOn()) : null)
                .createdBy(changeRequest.getCreatedBy())
                .updatedBy(changeRequest.getUpdatedBy())
                .isCreatorAdmin(changeRequest.getIsCreatorAdmin())
                .build();
    }

    public BPConfigurationChangeRequestRemarkVO toBPConfigurationChangeRequestRemarkVO(BPConfigurationChangeRequestRemark remark) {
        if (remark == null) {
            return null;
        }

        return BPConfigurationChangeRequestRemarkVO.builder()
                .remarkId(remark.getRemarkId())
                .ccrId(remark.getConfigurationChangeRequest() != null ? toBPConfigurationChangeRequestVO(remark.getConfigurationChangeRequest()) : null)
                .remarkType(remark.getRemarkType())
                .approverLevel(remark.getApproverLevel())
                .approvalStatus(remark.getApprovalStatus())
                .userId(remark.getUserId())
                .organizationId(remark.getOrganizationId())
                .remark(remark.getRemark())
                .createdOn(remark.getCreatedOn())
                .build();
    }

    public AgentPlantMappingVO toAgentPlantMappingVO(AgentPlantMapping agentPlantMapping) {
        if (agentPlantMapping == null) {
            return null;
        }

        return AgentPlantMappingVO.builder()
                .mappingId(agentPlantMapping.getMappingId())
                .agent(toSkillMappedAgentMasterVO(agentPlantMapping.getAgent()))
                .plant(toPlantMasterVO(agentPlantMapping.getPlant()))
                .active(agentPlantMapping.getIsActive())
                .createdBy(agentPlantMapping.getCreatedBy())
                .createdAt(agentPlantMapping.getCreatedAt())
                .creatorAdmin(agentPlantMapping.getCreatorAdmin())
                .build();
    }

    private AgentMasterVO toSkillMappedAgentMasterVO(AgentMaster agent) {
        if (agent == null) {
            return null;
        }

        return AgentMasterVO.builder()
                .agentId(agent.getAgentId())
                .agentName(agent.getAgentName())
                .agentSkillsMappings(agent.getAgentSkillsMappings() != null
                        ? agent.getAgentSkillsMappings().stream()
                        .map(this::toNoBackRefAgentSkillMappingVo)
                        .toList()
                        : null)
                .build();
    }

    public PlantMasterVO toPlantMasterVO(PlantMaster entity) {
        if (entity == null) {
            return null;
        }
        return PlantMasterVO.builder().plantId(entity.getPlantId()).plantName(entity.getPlantName())
                .region(toCountryMasterVO(entity.getRegion())).calendar(
                        entity.getCalendar() != null ? CalendarMasterVO.builder().calendarId(entity.getCalendar().getCalendarId())
                                .calendarCode(entity.getCalendar().getCalendarCode()).build()
                                : null
                )
                .weekOff(entity.getWeekoff() == null ? null
                        : entity.getWeekoff().stream().filter(PlantWeekoff::isActive)
                        .map(this::toPlantWeekoffVo).toList())
                .agentPlantMappings(entity.getAgentPlantMappings() == null ? null
                        : entity.getAgentPlantMappings().stream().filter(AgentPlantMapping::getIsActive)
                        .map(this::toAgentPlantMappingVo).toList())
                .createdBy(entity.getCreatedBy())
                .isCreatedByAdmin(entity.getIsCreatorAdmin() != null ? entity.getIsCreatorAdmin() : false)
                .updatedBy(entity.getUpdatedBy())
                .isUpdatedByAdmin(entity.getIsUpdaterAdmin() != null ? entity.getIsUpdaterAdmin() : false)
                .isActive(entity.getIsActive()).build();
    }

    public AgentSkillsMappingVO toNoBackRefAgentSkillMappingVo(AgentSkillsMapping agentSkillsMappings) {
        if (agentSkillsMappings == null) {
            return null;
        }
        return AgentSkillsMappingVO.builder().agentSkillId(agentSkillsMappings.getAgentSkillId())
                .skill(toSkillMasterVo(agentSkillsMappings.getSkill()))
                .experienceYears(agentSkillsMappings.getExperienceYears())
                .experienceMonths(agentSkillsMappings.getExperienceMonths()).build();
    }

    private PlantWeekoffVO toPlantWeekoffVo(PlantWeekoff plantWeekoff) {
        if (plantWeekoff == null) {
            return null;
        }
        return PlantWeekoffVO.builder().weekoffId(plantWeekoff.getWeekoffId())
                .weekoff(plantWeekoff.getWeekoff()).isActive(plantWeekoff.getIsActive())
                .createdBy(plantWeekoff.getCreatedBy())
                .isCreatedByAdmin(plantWeekoff.getIsCreatorAdmin() != null ? plantWeekoff.getIsCreatorAdmin() : false)
                .updatedBy(plantWeekoff.getUpdatedBy())
                .isUpdatedByAdmin(plantWeekoff.getIsUpdaterAdmin() != null ? plantWeekoff.getIsUpdaterAdmin() : false)
                .build();
    }

    private AgentPlantMappingVO toAgentPlantMappingVo(AgentPlantMapping agentPlantMapping) {
        if (agentPlantMapping == null) {
            return null;
        }
        return AgentPlantMappingVO.builder().mappingId(agentPlantMapping.getMappingId())
                .agent(toNoBackRefAgentMasterVO(agentPlantMapping.getAgent()))
                .active(agentPlantMapping.getIsActive()).createdBy(agentPlantMapping.getCreatedBy())
                .creatorAdmin(agentPlantMapping.getCreatorAdmin() != null ? agentPlantMapping.getCreatorAdmin() : false)
                .updatedBy(agentPlantMapping.getUpdatedBy())
                .updaterAdmin(agentPlantMapping.getUpdaterAdmin() != null ? agentPlantMapping.getUpdaterAdmin() : false)
                .build();
    }

    public SkillMasterVO toSkillMasterVo(SkillMaster save) {
        if (save == null) {
            return null;
        }
        return SkillMasterVO.builder().skillId(save.getSkillId()).skillName(save.getSkillName())
                .isActive(save.getIsActive()).createdBy(save.getCreatedBy())
                .isCreatedByAdmin(save.getIsCreatorAdmin() != null ? save.getIsCreatorAdmin() : false)
                .updatedBy(save.getUpdatedBy())
                .isUpdatedByAdmin(save.getIsUpdaterAdmin() != null ? save.getIsUpdaterAdmin() : false).build();
    }

    private AgentMasterVO toNoBackRefAgentMasterVO(AgentMaster agent) {
        if (agent == null) {
            return null;
        }
        return AgentMasterVO.builder().agentId(agent.getAgentId()).agentName(agent.getAgentName()).build();
    }

    public RitmMasterVO toRitmMasterVo(RitmMaster savedRitm) {
        if (savedRitm == null) {
            return null;
        }
        return RitmMasterVO.builder()
                .ritmId(savedRitm.getRitmId())
                .ritmNumber(savedRitm.getRitmNumber())
                .requestedBy(toRitmAgentMasterVO(savedRitm.getRequestedBy()))
                .requestedFor(toRitmAgentMasterVO(savedRitm.getRequestedFor()))
                .category(toRitmBPCategoryVo(savedRitm.getCategory()))
                .subCategory(toRitmBPSubCategoryVo(savedRitm.getSubCategory()))
                .supportGroup(toRitmSupportGroupVo(savedRitm.getSupportGroup()))
                .priority(toRitmBPPriorityVo(savedRitm.getPriority()))
                .shortDescription(savedRitm.getShortDescription())
                .description(savedRitm.getDescription())
                .stepsToReproduce(savedRitm.getStepsToReproduce())
                .otherNotes(savedRitm.getOtherNotes())
                .assignedTo(toRitmAgentMasterVO(savedRitm.getAssignedTo()))
                .watchlist(savedRitm.getWatchlist() != null
                        ? savedRitm.getWatchlist().stream().map(this::toRitmWatchlistVOForRitm).toList()
                        : null)
                .ritmAttachments(savedRitm.getAttachment() != null
                        ? savedRitm.getAttachment().stream().map(this::toRitmAttachmentVO).toList()
                        : null)
                .comments(savedRitm.getComments() != null
                        ? savedRitm.getComments().stream().map(this::toRitmCommentVO).toList()
                        : null)
                .audits(savedRitm.getAudits() != null
                        ? savedRitm.getAudits().stream().map(this::toRitmAuditVO).toList()
                        : null)
                .status(savedRitm.getStatus())
                .requestedAt(savedRitm.getRequestedAt())
                .dueDate(savedRitm.getDueDate())
                .resolvedAt(savedRitm.getResolvedAt())
                .closedAt(savedRitm.getClosedAt())
                .cancelledAt(savedRitm.getCancelledAt())
                .actionReason(savedRitm.getActionReason())
                .isActive(savedRitm.getIsActive())
                .createdBy(savedRitm.getCreatedBy())
                .updatedBy(savedRitm.getUpdatedBy())
                .createdAt(savedRitm.getCreatedAt())
                .updatedAt(savedRitm.getUpdatedAt())
                .isCreatorAdmin(savedRitm.getIsCreatorAdmin())
                .isUpdaterAdmin(savedRitm.getIsUpdaterAdmin())
                .build();
    }

    private AgentMasterVO toRitmAgentMasterVO(AgentMaster requestedBy) {
        if (requestedBy == null) {
            return null;
        }
        return AgentMasterVO.builder()
                .agentId(requestedBy.getAgentId())
                .agentName(requestedBy.getAgentName())
                .build();
    }

    private BPCategoryVO toRitmBPCategoryVo(BPCategory category) {
        if (category == null) {
            return null;
        }
        return BPCategoryVO.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .build();
    }

    private BPSubCategoryVO toRitmBPSubCategoryVo(BPSubCategory subCategory) {
        if (subCategory == null) {
            return null;
        }
        return BPSubCategoryVO.builder()
                .subCategoryId(subCategory.getSubCategoryId())
                .subCategoryName(subCategory.getSubCategoryName())
                .build();
    }

    private BPSupportGroupVO toRitmSupportGroupVo(BPSupportGroup supportGroup) {
        if (supportGroup == null) {
            return null;
        }
        return BPSupportGroupVO.builder()
                .supportGroupId(supportGroup.getSupportGroupId())
                .groupName(supportGroup.getGroupName())
                .build();
    }

    private BPPriorityVO toRitmBPPriorityVo(BPPriority priority) {
        if (priority == null) {
            return null;
        }
        return BPPriorityVO.builder().priorityId(priority.getPriorityId())
                .code(priority.getCode()).build();
    }

    private RitmWatchlistVO toRitmWatchlistVOForRitm(RitmWatchlist ritmWatchlist) {
        if (ritmWatchlist == null) {
            return null;
        }
        return RitmWatchlistVO.builder()
                .watchlistId(ritmWatchlist.getWatchlistId())
                .watchedBy(toRitmAgentMasterVO(ritmWatchlist.getWatchedBy()))
                .build();
    }

    private RitmAttachmentVO toRitmAttachmentVO(RitmAttachment ritmAttachment) {
        if (ritmAttachment == null) {
            return null;
        }
        return RitmAttachmentVO.builder()
                .attachmentId(ritmAttachment.getAttachmentId())
                .fileName(ritmAttachment.getFileName())
                .originalFileName(ritmAttachment.getOriginalFileName())
                .mimeType(ritmAttachment.getMimeType())
                .fileSize(ritmAttachment.getFileSize())
                .storageType(ritmAttachment.getStorageType())
                .storagePath(ritmAttachment.getStoragePath())
                .isActive(ritmAttachment.getIsActive())
                .uploadedBy(ritmAttachment.getUploadedBy())
                .uploadedAt(ritmAttachment.getUploadedAt())
                .deletedBy(ritmAttachment.getDeletedBy())
                .deletedAt(ritmAttachment.getDeletedAt())
                .build();
    }

    private RitmCommentVO toRitmCommentVO(RitmComment ritmComment) {
        if (ritmComment == null) {
            return null;
        }
        return RitmCommentVO.builder()
                .commentId(ritmComment.getCommentId())
                .commentType(ritmComment.getCommentType())
                .commentText(ritmComment.getCommentText())
                .isInternal(ritmComment.getIsInternal())
                .createdBy(ritmComment.getCreatedBy())
                .createdAt(ritmComment.getCreatedAt())
                .updatedBy(ritmComment.getUpdatedBy())
                .updatedAt(ritmComment.getUpdatedAt())
                .build();
    }

    private RitmAuditVO toRitmAuditVO(RitmAudit ritmAudit) {
        if (ritmAudit == null) {
            return null;
        }
        return RitmAuditVO.builder()
                .auditId(ritmAudit.getAuditId())
                .actionType(ritmAudit.getActionType())
                .description(ritmAudit.getDescription())
                .auditDetails(ritmAudit.getAuditDetails() != null
                        ? ritmAudit.getAuditDetails().stream().map(this::toRitmAuditDetailVO).toList()
                        : null)
                .changedBy(ritmAudit.getChangedBy())
                .changedAt(ritmAudit.getChangedAt())
                .build();
    }

    private RitmAuditDetailVO toRitmAuditDetailVO(RitmAuditDetail ritmAuditDetail) {
        if (ritmAuditDetail == null) {
            return null;
        }
        return RitmAuditDetailVO.builder()
                .auditDetailId(ritmAuditDetail.getAuditDetailId())
                .fieldName(ritmAuditDetail.getFieldName())
                .oldValue(ritmAuditDetail.getOldValue())
                .newValue(ritmAuditDetail.getNewValue())
                .build();
    }

    private RitmWatchlistVO toNoBackRefRitmWatchlistVO(RitmWatchlist ritmWatchlist) {
        if (ritmWatchlist == null) {
            return null;
        }
        return RitmWatchlistVO.builder()
                .watchlistId(ritmWatchlist.getWatchlistId())
                .ritm(null)
                .watchedBy(toNoBackRefAgentMasterVO(ritmWatchlist.getWatchedBy()))
                .isActive(ritmWatchlist.getIsActive())
                .createdAt(ritmWatchlist.getCreatedAt())
                .removedAt(ritmWatchlist.getRemovedAt())
                .build();
    }

    public BPPriorityVO toBPPriorityVo(BPPriority bpPriority) {
        if (bpPriority == null) {
            return null;
        }
        return BPPriorityVO.builder().priorityId(bpPriority.getPriorityId())
                .configuration(toBPConfigurationVO(bpPriority.getConfiguration())).level(bpPriority.getLevel())
                .code(bpPriority.getCode()).ticketType(toTicketTypeMasterVo(bpPriority.getTicketType()))
                .description(bpPriority.getDescription()).isActive(bpPriority.getIsActive()).isUnderApproval(bpPriority.getIsUnderApproval())
                .createdBy(bpPriority.getCreatedBy()).updatedBy(bpPriority.getUpdatedBy()).build();
    }

    public BPConfigurationVO toBPConfigurationVO(BPConfiguration configurations) {
        if (configurations == null) {
            return null;
        }
        return BPConfigurationVO.builder().configurationId(configurations.getConfigurationId())
                .businessPartner(toBusinessPartnerVO(configurations.getBusinessPartner()))
                .isActive(configurations.getIsActive()).createdBy(configurations.getCreatedBy())
                .isCreatorAdmin(configurations.getIsCreatorAdmin() != null ? configurations.getIsCreatorAdmin() : false)
                .updatedBy(configurations.getUpdatedBy())
                .isUpdaterAdmin(configurations.getIsUpdaterAdmin() != null ? configurations.getIsUpdaterAdmin() : false)
                .build();
    }

    public TicketTypeMasterVO toTicketTypeMasterVo(TicketTypeMaster ticketType) {
        if (ticketType == null) {
            return null;
        }
        return TicketTypeMasterVO.builder().ticketTypeId(ticketType.getTicketTypeId())
                .ticketTypeName(ticketType.getTicketTypeName()).isActive(ticketType.getIsActive())
                .createdBy(ticketType.getCreatedBy())
                .isCreatorAdmin(ticketType.getIsCreatorAdmin() != null ? ticketType.getIsCreatorAdmin() : false)
                .updatedBy(ticketType.getUpdatedBy())
                .isUpdaterAdmin(ticketType.getIsUpdaterAdmin() != null ? ticketType.getIsUpdaterAdmin() : false)
                .build();
    }

    public BusinessPartnerVO toBusinessPartnerVO(BusinessPartner role) {
        if (role == null) {
            return null;
        }
        return BusinessPartnerVO.builder().businessPartnerId(role.getBusinessPartnerId())
                .company(toNoBackRefCompanyMasterVO(role.getCompany()))
                .mappedCompany(toNoBackRefCompanyMasterVO(role.getMappedCompany()))
                .isServiceProvider(role.getIsServiceProvider()).isRequestor(role.getIsRequestor())
                .isBoth(role.getIsBoth()).isActive(role.getIsActive()).createdBy(role.getCreatedBy())
                .isCreatedByAdmin(role.getIsCreatorAdmin()).updatedBy(role.getUpdatedBy())
                .isUpdatedByAdmin(role.getIsUpdaterAdmin() != null ? role.getIsUpdaterAdmin() : false)
                .callHorizon(role.getCallHorizon()).validFrom(role.getValidFrom()).validTo(role.getValidTo())
                .refNo(role.getRefNo()).refDate(role.getRefDate()).build();
    }

    public CompanyMasterVO toNoBackRefCompanyMasterVO(CompanyMaster entity) {
        if (entity == null) {
            return null;
        }
        return CompanyMasterVO.builder().companyId(entity.getCompanyId()).companyName(entity.getCompanyName())
                .phoneCode(entity.getPhoneCode()).registeredNumber(entity.getRegisteredNumber()).country(null)
                .state(null).city(null).addressLine1(entity.getAddressLine1()).addressLine2(entity.getAddressLine2())
                .pinCode(entity.getPinCode()).isActive(entity.getIsActive()).createdBy(entity.getCreatedBy())
                .isCreatedByAdmin(entity.getIsCreatorAdmin()).uid(entity.getUid())
                .employeeSize(entity.getEmployeeSize()).mail(entity.getMail()).updatedBy(entity.getUpdatedBy())
                .isUpdatedByAdmin(entity.getIsUpdaterAdmin() != null ? entity.getIsUpdaterAdmin() : false).build();
    }

    public BPSupportGroupVO toNoBakcRefSupportGroupVo(BPSupportGroup supportGroup) {
        if (supportGroup == null) {
            return null;
        }
        return BPSupportGroupVO.builder().supportGroupId(supportGroup.getSupportGroupId())
                .configuration(null)
                .members(supportGroup.getMembers() != null
                        ? supportGroup.getMembers().stream().filter(member -> Boolean.TRUE.equals(member.getIsActive()))
                        .map(this::toNoBackRefSupportGroupMemberVo).toList()
                        : null)
                .managers(supportGroup.getManagers() != null
                        ? supportGroup.getManagers().stream().filter(manager -> Boolean.TRUE.equals(manager.getIsActive()))
                        .map(this::toNoBackRefSupportGroupManagerVo).toList()
                        : null)
                .groupName(supportGroup.getGroupName()).isActive(supportGroup.getIsActive()).isUnderApproval(supportGroup.getIsUnderApproval())
                .createdBy(supportGroup.getCreatedBy()).updatedBy(supportGroup.getUpdatedBy()).build();
    }

    private BPSupportGroupMemberVO toNoBackRefSupportGroupMemberVo(BPSupportGroupMember bpSupportGroupMember) {
        if (bpSupportGroupMember == null) {
            return null;
        }
        return BPSupportGroupMemberVO.builder().memberId(bpSupportGroupMember.getMemberId()).supportGroup(null)
                .agent(toNoBackRefAgentMasterVO(bpSupportGroupMember.getAgent())).isGroupLead(bpSupportGroupMember.getIsGroupLead())
                .isActive(bpSupportGroupMember.getIsActive()).build();
    }

    private BPSupportGroupManagerVO toNoBackRefSupportGroupManagerVo(BPSupportGroupManager bpSupportGroupManager) {
        if (bpSupportGroupManager == null) {
            return null;
        }
        return BPSupportGroupManagerVO.builder().managerId(bpSupportGroupManager.getManagerId()).supportGroup(null)
                .agent(toNoBackRefAgentMasterVO(bpSupportGroupManager.getAgent()))
                .isInternal(bpSupportGroupManager.getIsInternal())
                .isBP(bpSupportGroupManager.getIsBP())
                .isActive(bpSupportGroupManager.getIsActive()).build();
    }

    public BPSubCategoryVO toNoBackRefBPSubCategoryVo(BPSubCategory bpSubCategory) {
        if (bpSubCategory == null) {
            return null;
        }
        return BPSubCategoryVO.builder().subCategoryId(bpSubCategory.getSubCategoryId()).category(null)
                .subCategoryName(bpSubCategory.getSubCategoryName()).isActive(bpSubCategory.getIsActive())
                .createdBy(bpSubCategory.getCreatedBy()).updatedBy(bpSubCategory.getUpdatedBy()).build();
    }
}
