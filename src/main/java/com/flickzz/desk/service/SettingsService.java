package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.CALENDAR_CODE;
import static com.flickzz.desk.config.FlickzzDeskConstants.CALENDAR_TYPE;
import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.ALREADY_EXISTS;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DEFAULT_ERROR_CODE;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DOES_NOT_EXIST;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.INVALID_FIELD;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.BusinessService;
import com.flickzz.desk.model.CalendarHoliday;
import com.flickzz.desk.model.CalendarMaster;
import com.flickzz.desk.model.CalendarWorkday;
import com.flickzz.desk.model.ServiceOffering;
import com.flickzz.desk.repo.BusinessServiceRepository;
import com.flickzz.desk.repo.CalendarMasterRepository;
import com.flickzz.desk.repo.ServiceOfferingRepository;
import com.flickzz.desk.vo.BusinessServiceRequestVO;
import com.flickzz.desk.vo.BusinessServiceVO;
import com.flickzz.desk.vo.CalendarMasterRequestVO;
import com.flickzz.desk.vo.CalendarMasterVO;

@Service
public class SettingsService {

	private static final Logger log = LoggerFactory.getLogger(SettingsService.class);

	@Autowired
	private CalendarMasterRepository calendarMasterRepository;

	@Autowired
	private BusinessServiceRepository businessServiceRepository;

	@Autowired
	private ServiceOfferingRepository serviceOfferingRepository;

	@Autowired
	private CommonMapper mapper;

	public CalendarMasterVO createCalendar(CalendarMasterRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			if (request == null || request.getCalendarCode() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), CALENDAR_CODE));
			}

			if (request == null || request.getCalendarType() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), CALENDAR_TYPE));
			}

			calendarMasterRepository.findByCalendarCode(request.getCalendarCode()).ifPresent(c -> {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						getDescription(ALREADY_EXISTS.getDescription(), CALENDAR_CODE));
			});

			CalendarMaster entity = mapper.toCalendarMasterEntity(request);
			if (request.getHolidays() != null && request.getHolidays().size() > 0) {
				entity.setHolidays(
						mapper.toCalendarHolidayEntity(request.getHolidays(), request.getCreateBy(), entity));
			}
			if (request.getWorkingDays() != null) {
				entity.setWorkdays(mapper.toCalendarWorkDay(request.getWorkingDays(), request.getCreateBy(), entity));
			}

			CalendarMaster saved = calendarMasterRepository.save(entity);
			return mapper.toCalendarMasterVO(saved);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createCalendar method in SettingsService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public CalendarMasterVO getCalendarInfo(String calendarCode) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			if (calendarCode == null) {
				throw new FlickzzDeskException(INVALID_FIELD, CALENDAR_CODE);
			}

			CalendarMaster existing = calendarMasterRepository.findByCalendarCodeAndIsActive(calendarCode, true)
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), CALENDAR_CODE)));
			return mapper.toCalendarMasterVO(existing);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getCalendarInfo method in SettingsService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public CalendarMasterVO updateCalendar(CalendarMasterRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			if (request == null || request.getCalendarCode() == null) {
				throw new FlickzzDeskException(INVALID_FIELD, CALENDAR_CODE);
			}

			if (request == null || request.getCalendarType() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), CALENDAR_TYPE));
			}

			CalendarMaster existing = calendarMasterRepository
					.findByCalendarCodeAndIsActive(request.getCalendarCode(), true)
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), CALENDAR_CODE)));

			existing.setCalendarType(request.getCalendarType());
			existing.setValidFrom(request.getValidFrom());
			existing.setValidTo(request.getValidTo());
			existing.setWorkFrom(request.getWorkFrom());
			existing.setWorkTo(request.getWorkTo());
			existing.setTimezone(request.getTimezone());
			existing.setUpdatedBy(request.getUpdatedBy());

			existing.getHolidays().clear();
			if (request.getHolidays() != null) {
				existing.getHolidays()
						.addAll(request.getHolidays().stream()
								.map(h -> CalendarHoliday.builder().holidayDate(h.getHolidayDate())
										.description(h.getDescription()).calendarMaster(existing).build())
								.toList());
			}

			existing.getWorkdays().clear();
			if (request.getWorkingDays() != null) {
				existing.getWorkdays().addAll(request.getWorkingDays().stream().map(workingDay -> {
					CalendarWorkday workday = CalendarWorkday.builder().workday(workingDay).calendarMaster(existing)
							.createdBy(request.getCreateBy()).updatedBy(request.getCreateBy()).build();
					return workday;
				}).toList());
			}

			CalendarMaster saved = calendarMasterRepository.save(existing);
			return mapper.toCalendarMasterVO(saved);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in updateCalendar method in SettingsService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deleteCalendar(String calendarCode) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			CalendarMaster existing = calendarMasterRepository.findByCalendarCode(calendarCode)
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), "Calendar")));
			calendarMasterRepository.delete(existing);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in deleteCalendar method in SettingsService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<CalendarMasterVO> listCalendars() {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			return calendarMasterRepository.findAll().stream().map(mapper::toCalendarMasterVO).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in listCalendars method in SettingsService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BusinessServiceVO createBusinessService(BusinessServiceRequestVO businessServiceRequestVO) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			if (businessServiceRequestVO == null || businessServiceRequestVO.getServiceName() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), "Service Name"));
			} else if (businessServiceRequestVO.getServiceOfferings() == null
					|| businessServiceRequestVO.getServiceOfferings().size() == 0) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), "Service Offerings"));
			}

			businessServiceRepository.findByServiceName(businessServiceRequestVO.getServiceName()).ifPresent(s -> {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						getDescription(ALREADY_EXISTS.getDescription(), "Business Service"));
			});

			BusinessService saved = businessServiceRepository
					.save(mapper.toBusinessServiceEntity(businessServiceRequestVO));

			businessServiceRequestVO.getServiceOfferings().stream().forEach(so -> {
				ServiceOffering serviceOffering = mapper.toServiceOfferingEntity(so, saved);
				serviceOfferingRepository.save(serviceOffering);
			});

			return mapper.toBusinessServiceVO(saved);

		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createBusinessService method in SettingsService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BusinessServiceVO updateBusinessService(BusinessServiceRequestVO businessServiceRequestVO) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			if (businessServiceRequestVO == null || businessServiceRequestVO.getServiceId() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), "Service Id"));
			} else if (businessServiceRequestVO.getServiceName() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), "Service Name"));
			} else if (businessServiceRequestVO.getServiceOfferings() == null
					|| businessServiceRequestVO.getServiceOfferings().size() == 0) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), "Service Offerings"));
			}

			BusinessService existing = businessServiceRepository
					.findByServiceId(businessServiceRequestVO.getServiceId())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), "Business Service")));

			existing.getServiceOfferings().clear();
			existing.setUpdatedBy(businessServiceRequestVO.getUpdatedBy());
			BusinessService saved = businessServiceRepository.save(existing);

			businessServiceRequestVO.getServiceOfferings().stream().forEach(so -> {
				ServiceOffering serviceOffering = mapper.toServiceOfferingEntity(so, saved);
				serviceOfferingRepository.save(serviceOffering);
			});

			return mapper.toBusinessServiceVO(saved);

		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in updateBusinessService method in SettingsService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<BusinessServiceVO> listBusinessServices() {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			return businessServiceRepository.findAll().stream().map(mapper::toBusinessServiceVO).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in listBusinessServices method in SettingsService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public BusinessServiceVO getBusinessServiceInfo(String serviceId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			BusinessService existing = businessServiceRepository.findByServiceId(Long.valueOf(serviceId))
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), "Business Service")));
			return mapper.toBusinessServiceVO(existing);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getBusinessServiceInfo method in SettingsService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deleteBusinessService(String serviceId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			BusinessService existing = businessServiceRepository.findByServiceId(Long.valueOf(serviceId))
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), "Business Service")));
			businessServiceRepository.delete(existing);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in deleteBusinessService method in SettingsService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}
}
