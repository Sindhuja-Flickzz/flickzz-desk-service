package com.flickzz.desk.mapper;

import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.flickzz.desk.model.CalendarHoliday;
import com.flickzz.desk.model.CalendarMaster;
import com.flickzz.desk.model.CalendarWorkday;
import com.flickzz.desk.model.LoginMaster;
import com.flickzz.desk.model.User;
import com.flickzz.desk.security.CustomUserDetails;
import com.flickzz.desk.vo.CalendarHolidayVO;
import com.flickzz.desk.vo.CalendarMasterRequestVO;
import com.flickzz.desk.vo.CalendarMasterVO;
import com.flickzz.desk.vo.CalendarWorkdayVO;
import com.flickzz.desk.vo.RegisterLoginRequestVO;

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
				.build();
	}

	public CalendarMaster toCalendarMasterEntity(CalendarMasterRequestVO request, CustomUserDetails user) {
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
				.createdBy(user != null && user.getRole() != null?user.getRole().toUpperCase() : "SYSTEM")
				.createdAt(new Date())
				.updatedAt(new Date())
				.updatedBy(user != null && user.getRole() != null?user.getRole().toUpperCase() : "SYSTEM")
				.build();
	}
	
	public List<CalendarHoliday> toCalendarHolidayEntity(List<CalendarHolidayVO> calendarHolidayList, CustomUserDetails user, CalendarMaster entity) {
		return calendarHolidayList.stream().map(h -> {
			CalendarHoliday holiday = CalendarHoliday.builder()
			.holidayDate(h.getHolidayDate())
			.description(h.getDescription())
			.calendarMaster(entity)
			.createdBy(user != null && user.getRole() != null?user.getRole().toUpperCase() : "SYSTEM")
			.createdAt(new Date())
			.updatedAt(new Date())
			.updatedBy(user != null && user.getRole() != null?user.getRole().toUpperCase() : "SYSTEM")
			.build();
		return holiday;
		}).toList();
	}
	
	public List<CalendarWorkday> toCalendarWorkDay (List<String> workDayList, CustomUserDetails user, CalendarMaster entity) {
		return workDayList.stream().map(workingDay -> {
			CalendarWorkday workday = CalendarWorkday.builder()
					.workday(workingDay)
					.calendarMaster(entity)
					.createdBy(user != null && user.getRole() != null?user.getRole().toUpperCase() : "SYSTEM")
					.createdAt(new Date())
					.updatedAt(new Date())
					.updatedBy(user != null && user.getRole() != null?user.getRole().toUpperCase() : "SYSTEM"	)
					.build();
			return workday;
		}).toList();
	}
}

