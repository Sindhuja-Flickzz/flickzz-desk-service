package com.flickzz.desk.mapper;

import java.util.*;
import java.util.stream.*;

import org.springframework.context.annotation.*;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;

import com.flickzz.desk.config.*;
import com.flickzz.desk.model.*;
import com.flickzz.desk.vo.*;

@Component
public class CommonMapper {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public User registerRequesttoUser(RegisterLoginRequestVO request, String rawPassword, CountryMaster country,
			CityMaster city, LanguageMaster language) {
		if (request == null) {
			return null;
		}
		return User.builder().firstName(request.getFirstname()).lastName(request.getLastname())
				.middleName(request.getMiddlename()).email(request.getEmail()).userName(request.getEmail())
				.password(passwordEncoder().encode(rawPassword))
				.role(request.getRole() != null ? request.getRole() : FlickzzDeskConstants.ROLE_ADMIN)
				.phoneCode(request.getPhoneCode()).phoneNumber(request.getPhoneNumber()).country(country).city(city)
				.language(language).registerId(request.getRegisterId()).mfaEnabled(request.getMfaEnabled())
				.createdBy(request.getCreatedBy()).build();
	}

	public LoginMaster userToLoginMaster(User user) {
		if (user == null) {
			return null;
		}
		return LoginMaster.builder().userName(user.getUserName()).password(user.getPassword()).role(user.getRole())
				.createdBy(user.getCreatedBy()).user(user).build();
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
				.createdBy(calendar.getCreatedBy()).updatedBy(calendar.getUpdatedBy()).build();
	}

	public CalendarWorkdayVO toCalendarWorkdayVO(CalendarWorkday workday) {
		if (workday == null) {
			return null;
		}
		return CalendarWorkdayVO.builder().workdayId(workday.getWorkdayId()).workday(workday.getWorkday())
				.isActive(workday.getIsActive()).createdBy(workday.getCreatedBy()).updatedBy(workday.getUpdatedBy())
				.build();
	}

	public CalendarHolidayVO toCalendarHolidayVO(CalendarHoliday holiday) {
		if (holiday == null) {
			return null;
		}
		return CalendarHolidayVO.builder().holidayDate(holiday.getHolidayDate()).description(holiday.getDescription())
				.isActive(holiday.getIsActive()).createdBy(holiday.getCreatedBy()).updatedBy(holiday.getUpdatedBy())
				.build();
	}

	public CalendarMaster toCalendarMasterEntity(CalendarMasterRequestVO request, CalendarType calendarType,
			CompanyMaster company) {
		if (request == null) {
			return null;
		}
		return CalendarMaster.builder().calendarCode(request.getCalendarCode()).calendarType(calendarType)
				.company(company).validFrom(request.getValidFrom()).validTo(request.getValidTo())
				.workFrom(request.getWorkFrom()).workTo(request.getWorkTo()).timezone(request.getTimezone())
				.createdBy(request.getCreatedBy()).updatedBy(request.getUpdatedBy()).build();
	}

	public List<CalendarHoliday> toCalendarHolidayEntity(List<CalendarHolidayVO> calendarHolidayList, String createdBy,
			CalendarMaster entity) {
		if (calendarHolidayList == null) {
			return Collections.emptyList();
		}
		return calendarHolidayList.stream().map(h -> {
			CalendarHoliday holiday = CalendarHoliday.builder().holidayDate(h.getHolidayDate())
					.description(h.getDescription()).calendarMaster(entity).createdBy(createdBy).updatedBy(createdBy)
					.build();
			return holiday;
		}).toList();
	}

	public List<CalendarWorkday> toCalendarWorkDay(List<String> workDayList, String createdBy, CalendarMaster entity) {
		if (workDayList == null) {
			return Collections.emptyList();
		}
		return workDayList.stream().map(workingDay -> {
			CalendarWorkday workday = CalendarWorkday.builder().workday(workingDay).calendarMaster(entity)
					.createdBy(createdBy).updatedBy(createdBy).build();
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
				.createdBy(request.getCreatedBy()).updatedBy(request.getCreatedBy()).build();
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
				.createdBy(entity.getCreatedBy()).uid(entity.getUid()).employeeSize(entity.getEmployeeSize())
				.mail(entity.getMail()).updatedBy(entity.getUpdatedBy()).build();
	}

	public PlantMasterVO toPlantMasterVO(PlantMaster entity) {
		if (entity == null) {
			return null;
		}
		return PlantMasterVO.builder().plantId(entity.getPlantId()).plantName(entity.getPlantName())
				.region(toCountryMasterVO(entity.getRegion())).calendar(toCalendarMasterVO(entity.getCalendar()))
				.company(entity.getCompany() != null ? toCompanyMasterVO(entity.getCompany()) : null)
				.createdBy(entity.getCreatedBy()).updatedBy(entity.getUpdatedBy()).isActive(entity.getIsActive())
				.build();
	}

	public CountryMasterVO toCountryMasterVO(CountryMaster country) {
		if (country == null) {
			return null;
		}
		return CountryMasterVO.builder().countryId(country.getCountryId()).countryName(country.getCountryName())
				.isoCode(country.getIsoCode()).phoneCode(country.getPhoneCode()).currencyCode(country.getCurrencyCode())
				.countryName(country.getCountryName()).timezone(country.getTimezone()).build();
	}

	public SkillMasterVO toSkillMasterVo(SkillMaster save) {
		if (save == null) {
			return null;
		}
		return SkillMasterVO.builder().skillId(save.getSkillId()).skillName(save.getSkillName())
				.isActive(save.getIsActive()).createdBy(save.getCreatedBy()).updatedBy(save.getUpdatedBy()).build();
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
		vo.setLanguage(toLanguageMasterVO(agent.getUser().getLanguage()));
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

	public PriorityMaster toPriorityMaster(PriorityRequestVO vo, CompanyMaster companyMaster) {
		if (vo == null) {
			return null;
		}
		return PriorityMaster.builder().priorityId(vo.getPriorityId()).priorityName(vo.getPriorityName())
				.organization(companyMaster).rank(vo.getRank()).colorCode(vo.getColorCode())
				.responseSla(vo.getResponseSla()).resolutionSla(vo.getResolutionSla()).isActive(vo.getIsActive())
				.createdBy(vo.getCreatedBy()).updatedBy(vo.getUpdatedBy()).build();
	}

	public PriorityMasterVO toPriorityMasterVo(PriorityMaster entity) {
		if (entity == null) {
			return null;
		}
		return PriorityMasterVO.builder().priorityId(entity.getPriorityId()).priorityName(entity.getPriorityName())
				.organization(toCompanyMasterVO(entity.getOrganization())).rank(entity.getRank())
				.colorCode(entity.getColorCode()).responseSla(entity.getResponseSla())
				.resolutionSla(entity.getResolutionSla()).isActive(entity.getIsActive())
				.createdBy(entity.getCreatedBy()).updatedBy(entity.getUpdatedBy()).build();
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
						.language(toLanguageMasterVO(user.getLanguage())).mfaEnabled(user.isMfaEnabled())
						.isActive(user.getIsActive()).createdBy(user.getCreatedBy()).updatedBy(user.getUpdatedBy())
						.build())
				.collect(Collectors.toList());
	}

	public CityMasterVO toCityMasterVO(CityMaster cityMaster) {
		if (cityMaster == null) {
			return null;
		}
		return CityMasterVO.builder().cityId(cityMaster.getCityId()).cityName(cityMaster.getCityName())
				.cityCode(cityMaster.getCityCode()).country(toCountryMasterVO(cityMaster.getCountry()))
				.state(toStateMasterVO(cityMaster.getState())).isActive(cityMaster.getIsActive())
				.createdBy(cityMaster.getCreatedBy()).updatedBy(cityMaster.getUpdatedBy()).build();
	}

	public StateMasterVO toStateMasterVO(StateMaster state) {
		if (state == null) {
			return null;
		}
		return StateMasterVO.builder().stateId(state.getStateId()).stateName(state.getStateName())
				.stateCode(state.getStateCode()).country(toCountryMasterVO(state.getCountry()))
				.isActive(state.getIsActive()).createdBy(state.getCreatedBy()).updatedBy(state.getUpdatedBy()).build();
	}

	public LanguageMasterVO toLanguageMasterVO(LanguageMaster languageMaster) {
		if (languageMaster == null) {
			return null;
		}
		return LanguageMasterVO.builder().languageId(languageMaster.getLanguageId())
				.languageName(languageMaster.getLanguageName()).languageCode(languageMaster.getLanguageCode())
				.isActive(languageMaster.getIsActive()).createdBy(languageMaster.getCreatedBy())
				.updatedBy(languageMaster.getUpdatedBy()).build();
	}

	public BusinessService toBusinessServiceEntity(BusinessServiceRequestVO businessServiceRequestVO) {
		if (businessServiceRequestVO == null) {
			return null;
		}
		return BusinessService.builder().serviceName(businessServiceRequestVO.getServiceName())
				.createdBy(businessServiceRequestVO.getCreatedBy()).updatedBy(businessServiceRequestVO.getCreatedBy())
				.build();
	}

	public ServiceOffering toServiceOfferingEntity(ServiceOfferingVO so, BusinessService saved) {
		if (so == null) {
			return null;
		}
		return ServiceOffering.builder().offeringName(so.getOfferingName()).businessService(saved)
				.createdBy(so.getCreatedBy()).updatedBy(so.getCreatedBy()).build();
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
										.updatedBy(so.getUpdatedBy()).build())
								.toList())
				.createdBy(entity.getCreatedBy()).updatedBy(entity.getUpdatedBy()).build();
	}

	public RequestConfig toRequestConfigEntity(RequestConfigVO request, PlantMaster plant) {
		if (request == null) {
			return null;
		}
		return RequestConfig.builder().requestType(request.getRequestType()).requestPrefix(request.getRequestPrefix())
				.revision(request.getRevision()).rangeFrom(request.getRangeFrom()).rangeTo(request.getRangeTo())
				.calculateBackward(request.getCalculateBackward()).createdBy(request.getCreatedBy())
				.updatedBy(request.getCreatedBy()).build();
	}

	public RequestConfigVO toRequestConfigVO(RequestConfig requestConfig) {
		if (requestConfig == null) {
			return null;
		}
		return RequestConfigVO.builder().configId(requestConfig.getConfigId())
				.requestType(requestConfig.getRequestType()).requestPrefix(requestConfig.getRequestPrefix())
				.revision(requestConfig.getRevision()).rangeFrom(requestConfig.getRangeFrom())
				.rangeTo(requestConfig.getRangeTo()).calculateBackward(requestConfig.getCalculateBackward())
				.createdBy(requestConfig.getCreatedBy()).updatedBy(requestConfig.getUpdatedBy()).build();
	}

	public List<RequestConfigVO> toRequestConfigVOList(List<RequestConfig> requestConfigs) {
		if (requestConfigs == null) {
			return Collections.emptyList();
		}
		return requestConfigs.stream().map(this::toRequestConfigVO).toList();
	}

	public ImpactMaster toImpactMaster(ImpactRequestVO request, CompanyMaster companyMaster) {
		if (request == null) {
			return null;
		}
		return ImpactMaster.builder().impactId(request.getImpactId()).impactCode(request.getImpactCode())
				.organization(companyMaster).impactLevel(request.getImpactLevel())
				.slaMultiplier(request.getSlaMultiplier()).createdBy(request.getCreatedBy())
				.updatedBy(request.getUpdatedBy()).build();
	}

	public ImpactMasterVO toImpactMasterVo(ImpactMaster save) {
		if (save == null) {
			return null;
		}
		return ImpactMasterVO.builder().impactId(save.getImpactId()).impactCode(save.getImpactCode())
				.organization(toCompanyMasterVO(save.getOrganization())).impactLevel(save.getImpactLevel())
				.slaMultiplier(save.getSlaMultiplier()).isActive(save.getIsActive()).createdBy(save.getCreatedBy())
				.updatedBy(save.getUpdatedBy()).build();
	}

	public UserVO userToUserVO(User user) {
		if (user == null) {
			return null;
		}
		return UserVO.builder().userId(user.getUserId()).firstName(user.getFirstName()).lastName(user.getLastName())
				.email(user.getEmail()).userName(user.getUserName()).role(user.getRole())
				.middleName(user.getMiddleName()).registerId(user.getRegisterId()).phoneCode(user.getPhoneCode())
				.phoneNumber(user.getPhoneNumber()).country(toCountryMasterVO(user.getCountry()))
				.city(toCityMasterVO(user.getCity())).language(toLanguageMasterVO(user.getLanguage()))
				.mfaEnabled(user.isMfaEnabled()).agent(toAgentMasterVO(user.getAgent())).isActive(user.getIsActive())
				.createdBy(user.getCreatedBy()).updatedBy(user.getUpdatedBy()).build();
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

	public CalendarType toCalendarTypeEntity(String type, CompanyMaster company, String createBy) {
		if (type == null) {
			return null;
		}
		return CalendarType.builder().typeName(type).company(company).createdBy(createBy).updatedBy(createBy).build();
	}

	public CalendarTypeVO toCalendarTypeVO(CalendarType entity) {
		if (entity == null) {
			return null;
		}
		return CalendarTypeVO.builder().calendarTypeId(entity.getCalendarTypeId()).typeName(entity.getTypeName())
				.company(toCompanyMasterVO(entity.getCompany())).isActive(entity.getIsActive())
				.createdBy(entity.getCreatedBy()).updatedBy(entity.getUpdatedBy()).build();
	}

	public Project toProject(ProjectRequestVO request, CompanyMaster company) {
		if (request == null) {
			return null;
		}

		return Project.builder().company(company).projectName(request.getProjectName())
				.projectDesc(request.getProjectName()).projectDesc(request.getProjectDesc())
				.projectCode(request.getProjectName().toUpperCase().replaceAll("\\s+", "_")).isSaved(request.isSave())
				.isSubmited(request.isSubmit()).createdBy(request.getCreatedBy()).build();
	}

	public ProgressStatusVO toProgressStatusVO(ProgressStatus progressStatus) {
		if (progressStatus == null) {
			return null;
		}
		return ProgressStatusVO.builder().progressId(progressStatus.getProgressId())
				.company(toCompanyMasterVO(progressStatus.getCompany())).progressName(progressStatus.getProgressName())
				.progressSequence(progressStatus.getProgressSequence()).colorCode(progressStatus.getColorCode())
				.updatedBy(progressStatus.getUpdatedBy()).build();
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
				.isActive(epic.getIsActive()).createdBy(epic.getCreatedBy()).updatedBy(epic.getUpdatedBy()).build();
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
				.priorityId(toPriorityMasterVo(userStory.getPriority())).storyPoints(userStory.getStoryPoints())
				.isActive(userStory.getIsActive()).createdBy(userStory.getCreatedBy())
				.updatedBy(userStory.getUpdatedBy()).build();
	}

	public ProjectLeadAssignmentVO toProjectLeadAssignmentVO(ProjectLeadAssignment pla) {
		if (pla == null) {
			return null;
		}
		// Create a minimal UserStoryVO to avoid circular reference
		UserStoryVO userStoryVO = UserStoryVO.builder().storyId(pla.getUserStory().getStoryId()).build();

		return ProjectLeadAssignmentVO.builder().assignmentId(pla.getAssignmentId())
				.company(toCompanyMasterVO(pla.getCompany())).story(userStoryVO).isActive(pla.getIsActive())
				.createdBy(pla.getCreatedBy()).updatedBy(pla.getUpdatedBy()).build();
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
				.isActive(project.getIsActive()).createdBy(project.getCreatedBy()).updatedBy(project.getUpdatedBy())
				.isSaved(project.getIsSaved()).isSubmitted(project.getIsSubmited()).build();
	}

	public Epic toEpic(EpicVO epicVO, Project project, ProgressStatus defaultProgressStatus, int maxProgressStatus,
			String createdBy) {
		if (epicVO == null) {
			return null;
		}
		return Epic.builder().epicName(epicVO.getEpicName()).epicDesc(epicVO.getEpicDesc())
				.epicSequence(epicVO.getEpicSequence()).progressStatus(defaultProgressStatus)
				.maxProgressStatus(maxProgressStatus).project(project).createdBy(createdBy).build();
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
				.isActive(project.getIsActive()).createdBy(project.getCreatedBy()).updatedBy(project.getUpdatedBy())
				.build();
	}

	public EpicVO toNoBackRefEpicVO(Epic epic) {
		if (epic == null) {
			return null;
		}
		// Create a minimal ProjectVO to avoid circular reference
		ProjectVO projectVO = ProjectVO.builder().projectId(epic.getProject().getProjectId()).build();

		return EpicVO.builder().epicId(epic.getEpicId()).project(null).epicName(epic.getEpicName())
				.epicDesc(epic.getEpicDesc()).epicSequence(epic.getEpicSequence())
				.progress(toNoBackRefProgressStatusVO(epic.getProgressStatus()))
				.maxProgress(epic.getMaxProgressStatus()).plannedStartDate(epic.getPlannedStartDate())
				.plannedEndDate(epic.getPlannedEndDate())
				.userStories(epic.getUserStories() != null
						? epic.getUserStories().stream().map(this::toNoBackRefUserStoryVO).toList()
						: null)
				.isActive(epic.getIsActive()).createdBy(epic.getCreatedBy()).updatedBy(epic.getUpdatedBy()).build();
	}

	public ProgressStatusVO toNoBackRefProgressStatusVO(ProgressStatus progressStatus) {
		if (progressStatus == null) {
			return null;
		}
		return ProgressStatusVO.builder().progressId(progressStatus.getProgressId()).company(null)
				.progressName(progressStatus.getProgressName()).progressSequence(progressStatus.getProgressSequence())
				.colorCode(progressStatus.getColorCode()).updatedBy(progressStatus.getUpdatedBy()).build();
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
				.priorityId(toPriorityMasterVo(userStory.getPriority())).storyPoints(userStory.getStoryPoints())
				.isActive(userStory.getIsActive()).createdBy(userStory.getCreatedBy())
				.updatedBy(userStory.getUpdatedBy()).build();
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
				.createdBy(subTask.getCreatedBy()).build();
	}

	public ProjectLeadAssignmentVO toNoBackRefProjectLeadAssignmentVO(ProjectLeadAssignment pla) {
		if (pla == null) {
			return null;
		}
		return ProjectLeadAssignmentVO.builder().assignmentId(pla.getAssignmentId())
				.company(toNoBackRefCompanyMasterVO(pla.getCompany())).story(null).isActive(pla.getIsActive())
				.createdBy(pla.getCreatedBy()).updatedBy(pla.getUpdatedBy()).build();
	}

	public CompanyMasterVO toNoBackRefCompanyMasterVO(CompanyMaster entity) {
		if (entity == null) {
			return null;
		}
		return CompanyMasterVO.builder().companyId(entity.getCompanyId()).companyName(entity.getCompanyName())
				.phoneCode(entity.getPhoneCode()).registeredNumber(entity.getRegisteredNumber()).country(null)
				.state(null).city(null).addressLine1(entity.getAddressLine1()).addressLine2(entity.getAddressLine2())
				.pinCode(entity.getPinCode()).isActive(entity.getIsActive()).createdBy(entity.getCreatedBy())
				.uid(entity.getUid()).employeeSize(entity.getEmployeeSize()).mail(entity.getMail())
				.updatedBy(entity.getUpdatedBy()).build();
	}

	public CompanyRoleVO toCompanyRoleVO(CompanyRole role) {
		if (role == null) {
			return null;
		}
		return CompanyRoleVO.builder().roleId(role.getRoleId()).company(toNoBackRefCompanyMasterVO(role.getCompany()))
				.mappedCompany(toNoBackRefCompanyMasterVO(role.getMappedCompany()))
				.isServiceProvider(role.getIsServiceProvider()).isRequestor(role.getIsRequestor())
				.isBoth(role.getIsBoth()).isActive(role.getIsActive()).createdBy(role.getCreatedBy())
				.updatedBy(role.getUpdatedBy()).build();
	}

	public WorkItemVO toWorkItemVO(WorkItem workItem) {
		if (workItem == null) {
			return null;
		}
		return WorkItemVO.builder().itemId(workItem.getItemId()).code(workItem.getCode()).label(workItem.getLabel())
				.isActive(workItem.getIsActive()).createdBy(workItem.getCreatedBy()).updatedBy(workItem.getUpdatedBy())
				.build();
	}

	public FieldTypeVO toFieldTypeVO(FieldType fieldType) {
		if (fieldType == null) {
			return null;
		}
		return FieldTypeVO.builder().typeId(fieldType.getTypeId()).code(fieldType.getCode()).label(fieldType.getLabel())
				.isActive(fieldType.getIsActive()).createdBy(fieldType.getCreatedBy())
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
				.isActive(entity.getIsActive()).createdBy(entity.getCreatedBy()).updatedBy(entity.getUpdatedBy())
				.build();
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
}
