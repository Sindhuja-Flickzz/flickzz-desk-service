package com.flickzz.desk.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.flickzz.desk.config.FlickzzDeskConstants;
import com.flickzz.desk.model.AgentMaster;
import com.flickzz.desk.model.AgentSkillsMapping;
import com.flickzz.desk.model.BusinessService;
import com.flickzz.desk.model.CalendarHoliday;
import com.flickzz.desk.model.CalendarMaster;
import com.flickzz.desk.model.CalendarWorkday;
import com.flickzz.desk.model.CityMaster;
import com.flickzz.desk.model.CompanyMaster;
import com.flickzz.desk.model.CountryMaster;
import com.flickzz.desk.model.LanguageMaster;
import com.flickzz.desk.model.LoginMaster;
import com.flickzz.desk.model.PlantMaster;
import com.flickzz.desk.model.PriorityMaster;
import com.flickzz.desk.model.RequestConfig;
import com.flickzz.desk.model.ServiceOffering;
import com.flickzz.desk.model.SkillMaster;
import com.flickzz.desk.model.StateMaster;
import com.flickzz.desk.model.User;
import com.flickzz.desk.vo.AgentMasterVO;
import com.flickzz.desk.vo.AgentSkillsMappingVO;
import com.flickzz.desk.vo.BusinessServiceRequestVO;
import com.flickzz.desk.vo.BusinessServiceVO;
import com.flickzz.desk.vo.CalendarHolidayVO;
import com.flickzz.desk.vo.CalendarMasterRequestVO;
import com.flickzz.desk.vo.CalendarMasterVO;
import com.flickzz.desk.vo.CalendarWorkdayVO;
import com.flickzz.desk.vo.CityMasterVO;
import com.flickzz.desk.vo.CompanyMasterRequestVO;
import com.flickzz.desk.vo.CompanyMasterVO;
import com.flickzz.desk.vo.CountryMasterVO;
import com.flickzz.desk.vo.LanguageMasterVO;
import com.flickzz.desk.vo.PlantMasterVO;
import com.flickzz.desk.vo.PriorityMasterVO;
import com.flickzz.desk.vo.PriorityRequestVO;
import com.flickzz.desk.vo.RegisterLoginRequestVO;
import com.flickzz.desk.vo.RequestConfigVO;
import com.flickzz.desk.vo.ServiceOfferingVO;
import com.flickzz.desk.vo.SkillMasterVO;
import com.flickzz.desk.vo.StateMasterVO;
import com.flickzz.desk.vo.UserVO;

@Component
public class CommonMapper {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public User registerRequesttoUser(RegisterLoginRequestVO request, String rawPassword, CountryMaster country,
			CityMaster city, LanguageMaster language) {
		return User.builder().firstName(request.getFirstname()).lastName(request.getLastname())
				.email(request.getEmail()).userName(request.getEmail()).password(passwordEncoder().encode(rawPassword))
				.role(request.getRole() != null ? request.getRole() : FlickzzDeskConstants.ROLE_ADMIN)
				.phone(request.getPhone()).country(country).city(city).language(language)
				.registerId(request.getRegisterId()).mfaEnabled(request.getMfaEnabled())
				.createdBy(request.getCreatedBy()).build();
	}

	public LoginMaster userToLoginMaster(User user) {
		return LoginMaster.builder().userName(user.getUserName()).password(user.getPassword()).role(user.getRole())
				.createdBy(user.getCreatedBy()).user(user).build();
	}

	public CalendarMasterVO toCalendarMasterVO(CalendarMaster calendar) {
		if (calendar == null) {
			return null;
		}
		return CalendarMasterVO.builder().calendarId(calendar.getCalendarId()).calendarCode(calendar.getCalendarCode())
				.calendarType(calendar.getCalendarType()).validFrom(calendar.getValidFrom())
				.validTo(calendar.getValidTo()).isActive(calendar.getIsActive()).isSupport(calendar.getIsSupport())
				.isRequestor(calendar.getIsRequestor())
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

	public CalendarMaster toCalendarMasterEntity(CalendarMasterRequestVO request) {
		if (request == null) {
			return null;
		}
		return CalendarMaster.builder().calendarCode(request.getCalendarCode()).calendarType(request.getCalendarType())
				.validFrom(request.getValidFrom()).validTo(request.getValidTo()).workFrom(request.getWorkFrom())
				.workTo(request.getWorkTo()).timezone(request.getTimezone()).isRequestor(request.isRequestor())
				.isSupport(request.isSupport()).createdBy(request.getCreateBy()).updatedBy(request.getCreateBy())
				.build();
	}

	public List<CalendarHoliday> toCalendarHolidayEntity(List<CalendarHolidayVO> calendarHolidayList, String createdBy,
			CalendarMaster entity) {
		return calendarHolidayList.stream().map(h -> {
			CalendarHoliday holiday = CalendarHoliday.builder().holidayDate(h.getHolidayDate())
					.description(h.getDescription()).calendarMaster(entity).createdBy(createdBy).updatedBy(createdBy)
					.build();
			return holiday;
		}).toList();
	}

	public List<CalendarWorkday> toCalendarWorkDay(List<String> workDayList, String createdBy, CalendarMaster entity) {
		return workDayList.stream().map(workingDay -> {
			CalendarWorkday workday = CalendarWorkday.builder().workday(workingDay).calendarMaster(entity)
					.createdBy(createdBy).updatedBy(createdBy).build();
			return workday;
		}).toList();
	}

	public CompanyMaster toCompanyMasterEntity(CompanyMasterRequestVO request, CountryMaster country) {
		if (request == null) {
			return null;
		}
		return CompanyMaster.builder().companyName(request.getCompanyName())
				.registeredNumber(request.getRegisteredNumber()).country(country).address(request.getAddress())
				.mail(request.getMail()).isBoth(request.getMarkAsServiceProvider() ? true : false)
				.isServiceProvider(request.getMarkAsServiceProvider() ? false : request.getIsServiceProvider())
				.isRequestor(request.getMarkAsServiceProvider() ? false : request.getIsRequestor())
				.createdBy(request.getCreatedBy()).updatedBy(request.getCreatedBy()).build();
	}

	public CompanyMasterVO toCompanyMasterVO(CompanyMaster entity) {
		if (entity == null) {
			return null;
		}
		return CompanyMasterVO.builder().companyId(entity.getCompanyId()).companyName(entity.getCompanyName())
				.registeredNumber(entity.getRegisteredNumber()).country(toCountryMasterVO(entity.getCountry()))
				.address(entity.getAddress()).mail(entity.getMail()).isBoth(entity.getIsBoth())
				.isServiceProvider(entity.getIsServiceProvider()).isRequestor(entity.getIsRequestor())
				.isActive(entity.getIsActive()).createdBy(entity.getCreatedBy()).updatedBy(entity.getUpdatedBy())
				.build();
	}

	public PlantMasterVO toPlantMasterVO(PlantMaster entity) {
		if (entity == null) {
			return null;
		}
		return PlantMasterVO.builder().plantId(entity.getPlantId()).plantName(entity.getPlantName())
				.region(toCountryMasterVO(entity.getRegion())).calendar(toCalendarMasterVO(entity.getCalendar()))
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
		vo.setPhone(agent.getUser().getPhone());
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
		return PriorityMaster.builder().priorityId(vo.getPriorityId()).priorityName(vo.getPriorityName())
				.organization(companyMaster).rank(vo.getRank()).colorCode(vo.getColorCode())
				.responseSla(vo.getResponseSla()).resolutionSla(vo.getResolutionSla()).isActive(vo.getIsActive())
				.createdBy(vo.getCreatedBy()).updatedBy(vo.getUpdatedBy()).build();
	}

	public PriorityMasterVO toPriorityMasterVo(PriorityMaster entity) {
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
		return users.stream().map(user -> UserVO.builder().userId(user.getUserId()).firstName(user.getFirstName())
				.lastName(user.getLastName()).email(user.getEmail()).userName(user.getUserName()).role(user.getRole())
				.registerId(user.getRegisterId()).phone(user.getPhone()).country(toCountryMasterVO(user.getCountry()))
				.city(toCityMasterVO(user.getCity())).language(toLanguageMasterVO(user.getLanguage()))
				.mfaEnabled(user.isMfaEnabled()).isActive(user.getIsActive()).createdBy(user.getCreatedBy())
				.updatedBy(user.getUpdatedBy()).build()).collect(Collectors.toList());
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

	private StateMasterVO toStateMasterVO(StateMaster state) {
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
				.calculateBackward(request.getCalculateBackward()).plant(plant).createdBy(request.getCreatedBy())
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
				.plant(toPlantMasterVO(requestConfig.getPlant())).createdBy(requestConfig.getCreatedBy())
				.updatedBy(requestConfig.getUpdatedBy()).build();
	}

	public List<RequestConfigVO> toRequestConfigVOList(List<RequestConfig> requestConfigs) {
		if (requestConfigs == null) {
			return Collections.emptyList();
		}
		return requestConfigs.stream().map(this::toRequestConfigVO).toList();
	}
}
