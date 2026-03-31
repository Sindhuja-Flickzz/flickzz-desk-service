package com.flickzz.desk.mapper;

import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.flickzz.desk.model.AgentMaster;
import com.flickzz.desk.model.CalendarHoliday;
import com.flickzz.desk.model.CalendarMaster;
import com.flickzz.desk.model.CalendarWorkday;
import com.flickzz.desk.model.CompanyMaster;
import com.flickzz.desk.model.CountryMaster;
import com.flickzz.desk.model.LoginMaster;
import com.flickzz.desk.model.PlantMaster;
import com.flickzz.desk.model.SkillMaster;
import com.flickzz.desk.model.User;
import com.flickzz.desk.vo.AgentMasterVO;
import com.flickzz.desk.vo.CalendarHolidayVO;
import com.flickzz.desk.vo.CalendarMasterRequestVO;
import com.flickzz.desk.vo.CalendarMasterVO;
import com.flickzz.desk.vo.CalendarWorkdayVO;
import com.flickzz.desk.vo.CompanyMasterRequestVO;
import com.flickzz.desk.vo.CompanyMasterVO;
import com.flickzz.desk.vo.CountryMasterVO;
import com.flickzz.desk.vo.PlantMasterVO;
import com.flickzz.desk.vo.RegisterLoginRequestVO;
import com.flickzz.desk.vo.SkillMasterVO;

@Component
public class CommonMapper {

	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	public User registerRequesttoUser (RegisterLoginRequestVO request) {
		return User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .userName(request.getEmail())
                .password(passwordEncoder().encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : "ADMIN")
                .mfaEnabled(request.getMfaEnabled())
                .build();
	}
	
	public LoginMaster userToLoginMaster (User user) {
		return LoginMaster.builder()
				.userName(user.getUserName())
				.password(user.getPassword())
				.role(user.getRole())
				.user(user)
				.build();
	}
	
	public CalendarMasterVO toCalendarMasterVO(CalendarMaster calendar) {
		if (calendar == null) {
			return null;
		}
		return CalendarMasterVO.builder()
				.calendarId(calendar.getCalendarId())
				.calendarCode(calendar.getCalendarCode())
				.calendarType(calendar.getCalendarType())
				.validFrom(calendar.getValidFrom())
				.validTo(calendar.getValidTo())
				.isActive(calendar.getIsActive())
				.isSupport(calendar.getIsSupport())
				.isRequestor(calendar.getIsRequestor())
				.workdays(calendar.getWorkdays() == null ? null : calendar.getWorkdays().stream()
						.filter(CalendarWorkday::isActive).map(this::toCalendarWorkdayVO).toList())
				.workFrom(calendar.getWorkFrom())
				.workTo(calendar.getWorkTo())
				.timezone(calendar.getTimezone())
				.holidays(calendar.getHolidays() == null ? null : calendar.getHolidays().stream()
						.filter(CalendarHoliday::isActive).map(this::toCalendarHolidayVO).toList())
				.createdBy(calendar.getCreatedBy())
				.updatedBy(calendar.getUpdatedBy())
				.build();
	}

	public CalendarWorkdayVO toCalendarWorkdayVO(CalendarWorkday workday) {
		if (workday == null) {
			return null;
		}
		return CalendarWorkdayVO.builder()
				.workdayId(workday.getWorkdayId())
				.workday(workday.getWorkday())
				.isActive(workday.getIsActive())
				.createdBy(workday.getCreatedBy())
				.updatedBy(workday.getUpdatedBy())
				.build();
	}

	public CalendarHolidayVO toCalendarHolidayVO(CalendarHoliday holiday) {
		if (holiday == null) {
			return null;
		}
		return CalendarHolidayVO.builder()
				.holidayDate(holiday.getHolidayDate())
				.description(holiday.getDescription())
				.isActive(holiday.getIsActive())
				.createdBy(holiday.getCreatedBy())
				.updatedBy(holiday.getUpdatedBy())
				.build();
	}

	public CalendarMaster toCalendarMasterEntity(CalendarMasterRequestVO request) {
		if (request == null) {
			return null;
		}
		return CalendarMaster.builder()
				.calendarCode(request.getCalendarCode())
				.calendarType(request.getCalendarType())
				.validFrom(request.getValidFrom())
				.validTo(request.getValidTo())
				.workFrom(request.getWorkFrom())
				.workTo(request.getWorkTo())
				.timezone(request.getTimezone())
				.isRequestor(request.isRequestor())
				.isSupport(request.isSupport())
				.createdBy(request.getCreateBy())
				.createdAt(new Date())
				.updatedAt(new Date())
				.updatedBy(request.getCreateBy())
				.build();
	}
	
	public List<CalendarHoliday> toCalendarHolidayEntity(List<CalendarHolidayVO> calendarHolidayList, String createdBy, CalendarMaster entity) {
		return calendarHolidayList.stream().map(h -> {
			CalendarHoliday holiday = CalendarHoliday.builder()
			.holidayDate(h.getHolidayDate())
			.description(h.getDescription())
			.calendarMaster(entity)
			.createdBy(createdBy)
			.createdAt(new Date())
			.updatedAt(new Date())
			.updatedBy(createdBy)
			.build();
		return holiday;
		}).toList();
	}
	
	public List<CalendarWorkday> toCalendarWorkDay (List<String> workDayList, String createdBy, CalendarMaster entity) {
		return workDayList.stream().map(workingDay -> {
			CalendarWorkday workday = CalendarWorkday.builder()
					.workday(workingDay)
					.calendarMaster(entity)
					.createdBy(createdBy)
					.createdAt(new Date())
					.updatedAt(new Date())
					.updatedBy(createdBy)
					.build();
			return workday;
		}).toList();
	}

	public CompanyMaster toCompanyMasterEntity(CompanyMasterRequestVO request) {
		if (request == null) {
			return null;
		}
		return CompanyMaster.builder()
				.companyName(request.getCompanyName())
				.registeredNumber(request.getRegisteredNumber())
				.currency(request.getCurrency())
				.address(request.getAddress())
				.mail(request.getMail())
				.markAsServiceProvider(request.getMarkAsServiceProvider())
				.isServiceProvider(request.getIsServiceProvider())
				.isRequestor(request.getIsRequestor())
				.createdBy(request.getCreatedBy())
				.createdAt(new Date())
				.updatedAt(new Date())
				.updatedBy(request.getCreatedBy()).build();
	}

	public CompanyMasterVO toCompanyMasterVO(CompanyMaster entity) {
		if (entity == null) {
			return null;
		}
		return CompanyMasterVO.builder()
				.companyId(entity.getCompanyId())
				.companyName(entity.getCompanyName())
				.registeredNumber(entity.getRegisteredNumber())
				.currency(entity.getCurrency())
				.address(entity.getAddress())
				.mail(entity.getMail())
				.markAsServiceProvider(entity.getMarkAsServiceProvider())
				.isServiceProvider(entity.getIsServiceProvider())
				.isRequestor(entity.getIsRequestor())
				.isActive(entity.getIsActive())
				.createdBy(entity.getCreatedBy())
				.createdAt(entity.getCreatedAt())
				.updatedBy(entity.getUpdatedBy())
				.updatedAt(entity.getUpdatedAt()).build();
	}

	public PlantMasterVO toPlantMasterVO(PlantMaster entity) {
		if (entity == null) {
			return null;
		}
		return PlantMasterVO.builder()
				.plantId(entity.getPlantId())
				.plantName(entity.getPlantName())
				.region(toCountryMasterVO(entity.getRegion()))
				.calendar(toCalendarMasterVO(entity.getCalendar()))
				.createdBy(entity.getCreatedBy())
				.updatedBy(entity.getUpdatedBy())
				.isActive(entity.getIsActive()).build();
	}

	public CountryMasterVO toCountryMasterVO(CountryMaster country) {
		if (country == null) {
            return null;
        }
        return CountryMasterVO.builder()
                .countryId(country.getCountryId())
                .countryName(country.getCountryName())
                .isoCode(country.getIsoCode())
                .createdBy(country.getCreatedBy())
                .updatedBy(country.getUpdatedBy())
                .build();
	}

	public SkillMasterVO toSkillMasterVo(SkillMaster save) {
		if (save == null) {
			return null;
		}
		return SkillMasterVO.builder()
				.skillId(save.getSkillId())
				.skillName(save.getSkillName())
				.isActive(save.getIsActive())
				.createdBy(save.getCreatedBy())
				.updatedBy(save.getUpdatedBy())
				.build();
	}

	public AgentMasterVO toAgentMasterVO(AgentMaster agent) {
		if (agent == null) {
			return null;
		}
		return AgentMasterVO.builder()
				.agentId(agent.getAgentId())
				.agentName(agent.getAgentName())
				.mailId(agent.getMailId())
				.accessId(agent.getAccessId())
				.phone(agent.getPhone())
				.organization(toCompanyMasterVO(agent.getOrganization()))
				.calendar(toCalendarMasterVO(agent.getCalendar()))
				.createdBy(agent.getCreatedBy())
				.isActive(agent.getIsActive()).build();
	}
}

