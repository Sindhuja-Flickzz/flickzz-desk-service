package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.*;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

import java.util.*;

import com.flickzz.desk.vo.request.CalendarMasterRequestVO;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.*;

import com.flickzz.desk.exception.*;
import com.flickzz.desk.mapper.*;
import com.flickzz.desk.model.*;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.vo.*;
import tools.jackson.databind.ObjectMapper;

@Service
public class CalendarService {

	private static final Logger log = LoggerFactory.getLogger(CalendarService.class);

	@Autowired
	private CalendarMasterRepository calendarMasterRepository;

	@Autowired
	private CalendarTypeRepository calendarTypeRepository;

	@Autowired
	private CompanyMasterRepository companyMasterRepository;

	@Autowired
	private CommonMapper mapper;

	@Autowired
	private AuditService auditService;

	@Autowired
    ObjectMapper objectMapper;
    @Autowired
    private PlantMasterRepository plantMasterRepository;
    @Autowired
    private AgentMasterRepository agentMasterRepository;

	public CalendarMasterVO createCalendar(CalendarMasterRequestVO request) {
		log.info(generateLog("createCalendar", this.getClass().getName()));
		try {
			if (request == null || request.getCalendarCode() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), CALENDAR_CODE));
			}

			if (request == null || request.getCalendarType() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), CALENDAR_TYPE));
			}

			calendarMasterRepository.findByCalendarCodeAndCompany_CompanyId(request.getCalendarCode(),
					request.getCompany()).ifPresent(calendar -> {
						if (calendar.getIsActive()) {
							throw new FlickzzDeskException(ALREADY_EXISTS,
									getDescription(ALREADY_EXISTS.getDescription(), CALENDAR_CODE));
						} else {
							throw new FlickzzDeskException(DELETED_ERROR,
									getDescription(DELETED_ERROR.getDescription(), CALENDAR_CODE));
						}
					});

			CalendarType calendarType = calendarTypeRepository
					.findByCalendarTypeIdAndCompany_CompanyId(request.getCalendarType(), request.getCompany())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), CALENDAR_TYPE)));

			CompanyMaster company = companyMasterRepository.findByCompanyIdAndIsActive(request.getCompany(), ACTIVE)
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), COMPANY)));

			CalendarMaster entity = mapper.toCalendarMasterEntity(request, calendarType, company);
			if (request.getHolidays() != null && request.getHolidays().size() > 0) {
				entity.setHolidays(
						mapper.toCalendarHolidayEntity(request.getHolidays(), request.getCreatedBy(), entity));
			}
			if (request.getWorkingDays() != null) {
				entity.setWorkdays(mapper.toCalendarWorkDay(request.getWorkingDays(), request.getCreatedBy(), entity));
			}

			CalendarMaster saved = calendarMasterRepository.save(entity);

			Map<String, Object> snapshot = new HashMap<>();
			snapshot.put("calendarId", saved.getCalendarId());
			snapshot.put("calendarCode", saved.getCalendarCode());
			snapshot.put("companyId", saved.getCompany() != null ? saved.getCompany().getCompanyId() : null);
			snapshot.put("calendarTypeId", saved.getCalendarType() != null ? saved.getCalendarType().getCalendarTypeId() : null);
			snapshot.put("isActive", saved.getIsActive());
			String newValue = serialize(snapshot);
			auditService.recordAudit(mapper.toSystemAuditRequest("Calendar", "Calendar", "CalendarMaster",
					saved.getCalendarId(),
					"CREATE",
					newValue,
					null,
					null,
					request != null ? request.getCreatedBy() : null,
					"SYSTEM",
					request != null ? request.getCompany() : null,
					"SUCCESS",
					null));

			return mapper.toCalendarMasterVO(saved);
		} catch (FlickzzDeskException e) {
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Calendar", "Calendar", "CalendarMaster",
					null, "CREATE", null, null, null, request != null ? request.getCreatedBy() : null, "SYSTEM",
					request != null ? request.getCompany() : null, "FAILED", e.getMessage()), e);
			throw e;
		} catch (Exception e) {
			log.error("Exception in createCalendar method in CalendarService");
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Calendar", "Calendar", "CalendarMaster",
					null, "CREATE", null, null, null, request != null ? request.getCreatedBy() : null, "SYSTEM",
					request != null ? request.getCompany() : null, "FAILED", e.getMessage()), e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public CalendarMasterVO getCalendarInfo(String calendarCode) {
		log.info(generateLog("getCalendarInfo", this.getClass().getName()));
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
			log.error("Exception in getCalendarInfo method in CalendarService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public CalendarMasterVO updateCalendar(CalendarMasterRequestVO request) {
		log.info(generateLog("updateCalendar", this.getClass().getName()));
		try {
			if (request == null || request.getCalendarCode() == null) {
				throw new FlickzzDeskException(INVALID_FIELD, CALENDAR_CODE);
			}

			if (request == null || request.getCalendarType() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), CALENDAR_TYPE));
			}

			CalendarMaster existing = calendarMasterRepository
					.findByCalendarCodeAndCompany_CompanyId(request.getCalendarCode(), request.getCompany())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), CALENDAR_CODE)));

			CalendarType calendarType = calendarTypeRepository
					.findByCalendarTypeIdAndCompany_CompanyId(request.getCalendarType(), request.getCompany())
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), CALENDAR_TYPE)));

			existing.setIsActive(ACTIVE);
			existing.setCalendarType(calendarType);
			existing.setValidFrom(request.getValidFrom());
			existing.setValidTo(request.getValidTo());
			existing.setWorkFrom(request.getWorkFrom());
			existing.setWorkTo(request.getWorkTo());
			existing.setTimezone(request.getTimezone());
			existing.setUpdatedBy(request.getUpdatedBy());
			existing.setIsUpdaterAdmin(request.getIsUpdatedByAdmin());

			existing.getHolidays().clear();
			if (request.getHolidays() != null) {
				existing.getHolidays()
						.addAll(request.getHolidays().stream()
								.map(h -> CalendarHoliday.builder().holidayDate(h.getHolidayDate())
										.description(h.getDescription()).calendarMaster(existing)
										.createdBy(request.getCreatedBy()).updatedBy(request.getUpdatedBy())
										.isCreatorAdmin(request.getIsCreatedByAdmin())
										.isUpdaterAdmin(request.getIsUpdatedByAdmin()).build())
								.toList());
			}

			existing.getWorkdays().clear();
			if (request.getWorkingDays() != null) {
				existing.getWorkdays().addAll(request.getWorkingDays().stream().map(workingDay -> {
					CalendarWorkday workday = CalendarWorkday.builder().workday(workingDay).calendarMaster(existing)
							.createdBy(request.getCreatedBy()).isCreatorAdmin(request.getIsCreatedByAdmin())
							.updatedBy(request.getCreatedBy()).isUpdaterAdmin(request.getIsUpdatedByAdmin()).build();
					return workday;
				}).toList());
			}

			CalendarMaster saved = calendarMasterRepository.save(existing);

			Map<String, Object> oldSnapshot = new HashMap<>();
			oldSnapshot.put("calendarId", existing.getCalendarId());
			oldSnapshot.put("calendarCode", existing.getCalendarCode());
			oldSnapshot.put("companyId", existing.getCompany() != null ? existing.getCompany().getCompanyId() : null);
			oldSnapshot.put("calendarTypeId", existing.getCalendarType() != null ? existing.getCalendarType().getCalendarTypeId() : null);
			oldSnapshot.put("isActive", existing.getIsActive());
			oldSnapshot.put("validFrom", existing.getValidFrom());
			oldSnapshot.put("validTo", existing.getValidTo());
			Map<String, Object> newSnapshot = new HashMap<>();
			newSnapshot.put("calendarId", saved.getCalendarId());
			newSnapshot.put("calendarCode", saved.getCalendarCode());
			newSnapshot.put("companyId", saved.getCompany() != null ? saved.getCompany().getCompanyId() : null);
			newSnapshot.put("calendarTypeId", saved.getCalendarType() != null ? saved.getCalendarType().getCalendarTypeId() : null);
			newSnapshot.put("isActive", saved.getIsActive());
			newSnapshot.put("validFrom", saved.getValidFrom());
			newSnapshot.put("validTo", saved.getValidTo());
			String oldValue = serialize(oldSnapshot);
			String newValue = serialize(newSnapshot);
			auditService.recordAudit(mapper.toSystemAuditRequest("Calendar", "Calendar", "CalendarMaster",
					saved.getCalendarId(),
					"UPDATE",
					newValue,
					oldValue,
					null,
					request != null ? request.getUpdatedBy() : null,
					"SYSTEM",
					request != null ? request.getCompany() : null,
					"SUCCESS",
					null));

			return mapper.toCalendarMasterVO(saved);
		} catch (FlickzzDeskException e) {
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Calendar", "Calendar", "CalendarMaster",
					null, "UPDATE", null, null, null, request != null ? request.getUpdatedBy() : null, "SYSTEM",
					request != null ? request.getCompany() : null, "FAILED", e.getMessage()), e);
			throw e;
		} catch (Exception e) {
			log.error("Exception in updateCalendar method in CalendarService");
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Calendar", "Calendar", "CalendarMaster",
					null, "UPDATE", null, null, null, request != null ? request.getUpdatedBy() : null, "SYSTEM",
					request != null ? request.getCompany() : null, "FAILED", e.getMessage()), e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deleteCalendar(String calendarCode, Long userId) {
		log.info(generateLog("deleteCalendar", this.getClass().getName()));
		try {
			CalendarMaster existing = calendarMasterRepository.findByCalendarCode(calendarCode)
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), "Calendar")));

			if(plantMasterRepository.existsByCalendar_CalendarIdAndIsActiveTrue(existing.getCalendarId())) {
				throw new FlickzzDeskException(DB_SAVE_ERROR,
						"Calendar referenced by existing plant. Please remove all associations and try again.");
			}
			if(agentMasterRepository.existsByCalendarMaster_CalendarIdAndIsActiveTrue(existing.getCalendarId())) {
				throw new FlickzzDeskException(DB_SAVE_ERROR,
						"Calendar referenced by existing agent. Please remove all associations and try again.");
			}
			existing.setIsActive(DEACTIVATE);
			CalendarMaster saved = calendarMasterRepository.save(existing);

			Map<String, Object> snapshot = new HashMap<>();
			snapshot.put("calendarId", saved.getCalendarId());
			snapshot.put("calendarCode", saved.getCalendarCode());
			snapshot.put("companyId", saved.getCompany() != null ? saved.getCompany().getCompanyId() : null);
			snapshot.put("isActive", saved.getIsActive());
			String newValue = serialize(snapshot);
			auditService.recordAudit(mapper.toSystemAuditRequest("Calendar", "Calendar", "CalendarMaster",
					saved.getCalendarId(),
					"DELETE",
					newValue,
					null,
					null,
					existing.getCreatedBy(),
					"SYSTEM",
					saved.getCompany() != null ? saved.getCompany().getCompanyId() : null,
					"SUCCESS",
					null));
		} catch (DataIntegrityViolationException e) {
			log.error("Unable to delete calendar due to a dependent entity reference", e);
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Calendar", "Calendar", "CalendarMaster",
					null, "DELETE", null, null, null, userId, "SYSTEM", null, "FAILED", "Unable to delete the calendar because it is referenced by existing records. Please remove all associations and try again."), e);
			throw new FlickzzDeskException(DB_SAVE_ERROR,
					"Unable to delete the calendar because it is referenced by existing records. Please remove all associations and try again.");
		} catch (FlickzzDeskException e) {
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Calendar", "Calendar", "CalendarMaster",
					null, "DELETE", null, null, null, userId, "SYSTEM", null, "FAILED", e.getDescription()), e);
			throw e;
		} catch (Exception e) {
			log.error("Exception in deleteCalendar method in CalendarService", e);
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Calendar", "Calendar", "CalendarMaster",
					null, "DELETE", null, null, null, userId, "SYSTEM", null, "FAILED", e.getCause().getMessage()), e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	private String serialize(Object value) {
		try {
			return value == null ? null : objectMapper.writeValueAsString(value);
		} catch (Exception e) {
			log.error("Exception while serializing calendar audit payload", e);
			return null;
		}
	}

	public List<CalendarMasterVO> listCalendars(String orgId) {
		log.info(generateLog("listCalendars", this.getClass().getName()));
		try {
			return calendarMasterRepository.findAllByCompany_CompanyId(Long.valueOf(orgId)).stream()
					.map(mapper::toCalendarMasterVO).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in listCalendars method in CalendarService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<CalendarTypeVO> createCalendarType(CalendarMasterRequestVO request) {
		log.info(generateLog("createCalendarType", this.getClass().getName()));
		try {
			if (request == null || request.getCalendarTypeList() == null || request.getCalendarTypeList().size() == 0) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), "Calendar Type List"));
			}

			CompanyMaster company = companyMasterRepository.findByCompanyIdAndIsActive(request.getCompany(), ACTIVE)
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), COMPANY)));

			List<CalendarTypeVO> calendarTypeVOs = new ArrayList<CalendarTypeVO>();

			request.getCalendarTypeList().forEach(type -> {
				calendarTypeRepository.findByTypeNameAndCompany_CompanyId(type, request.getCompany()).ifPresent(calendarType -> {
					if (calendarType.getIsActive()) {
						throw new FlickzzDeskException(ALREADY_EXISTS,
								getDescription(ALREADY_EXISTS.getDescription(), CALENDAR_TYPE));
					} else {
						throw new FlickzzDeskException(DELETED_ERROR,
								getDescription(DELETED_ERROR.getDescription(), CALENDAR_TYPE));
					}
				});
				calendarTypeVOs.add(mapper.toCalendarTypeVO(calendarTypeRepository
						.save(mapper.toCalendarTypeEntity(type, company, request.getCreatedBy()))));
			});

			// Prepare new snapshot for audit
			String newSnapshot = null;
			try {
				newSnapshot = objectMapper.writeValueAsString(buildCategoryTypeSnapshot(calendarTypeVOs));
			} catch (Exception e) {
				log.error("Exception while creating snapshot for audit", e);
			}

			auditService.recordAudit(mapper.toSystemAuditRequest("Calendar", "CalendarType", "CalendarType",
					null,
					"CREATE",
					newSnapshot,
					null,
					null,
					request != null ? request.getCreatedBy() : null,
					"SYSTEM",
					request != null ? request.getCompany() : null,
					"SUCCESS",
					null));

			return calendarTypeVOs;
		} catch (FlickzzDeskException e) {
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Calendar", "CalendarType", "CalendarType",
					null, "CREATE", null, null, null, request != null ? request.getCreatedBy() : null, "SYSTEM",
					request != null ? request.getCompany() : null, "FAILED", e.getMessage()), e);
			throw e;
		} catch (Exception e) {
			log.error("Exception in createCalendarType method in CalendarService");
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Calendar", "CalendarType", "CalendarType",
					null, "CREATE", null, null, null, request != null ? request.getCreatedBy() : null, "SYSTEM",
					request != null ? request.getCompany() : null, "FAILED", e.getMessage()), e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	private Object buildCategoryTypeSnapshot(List<CalendarTypeVO> calendarTypeVOs) {
		Map<String, Object> snapshot = new HashMap<>();
		if (calendarTypeVOs == null) {
			return snapshot;
		}
		try {
			calendarTypeVOs.stream().map(calendarTypeVO -> {
				Map<String, Object> calendarTypeMap = new HashMap<>();
				calendarTypeMap.put("calendarTypeId", calendarTypeVO.getCalendarTypeId());
				calendarTypeMap.put("typeName", calendarTypeVO.getTypeName());
				calendarTypeMap.put("isActive", calendarTypeVO.getIsActive());
				calendarTypeMap.put("createdBy", calendarTypeVO.getCreatedBy());
				calendarTypeMap.put("updatedBy", calendarTypeVO.getUpdatedBy());
				calendarTypeMap.put("isCreatedByAdmin", calendarTypeVO.getIsCreatedByAdmin());
				calendarTypeMap.put("isUpdatedByAdmin", calendarTypeVO.getIsUpdatedByAdmin());
				return calendarTypeMap;
			}).forEach(calendarTypeMap -> snapshot.put(String.valueOf(calendarTypeMap.get("calendarTypeId")), calendarTypeMap));
		} catch (Exception e) {
			log.error("Exception while building snapshot for audit", e);
		}
		return snapshot;
	}

	public void deleteCalendarType(String calendarTypeId, Long userId) {
		log.info(generateLog("deleteCalendarType", this.getClass().getName()));
		try {
			CalendarType calendarType = calendarTypeRepository.findById(Long.valueOf(calendarTypeId))
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), "Calendar Type")));

			if(calendarMasterRepository.existsByCalendarType_CalendarTypeIdAndIsActiveTrue(calendarType.getCalendarTypeId())) {
				log.info("Cannot delete calendar type as it is associated with existing calendars.");
				throw new FlickzzDeskException(DB_SAVE_ERROR, "Cannot delete calendar type as it is associated with existing calendars.");
			}
			calendarType.setIsActive(DEACTIVATE);
			CalendarType saved = calendarTypeRepository.save(calendarType);

			Map<String, Object> snapshot = new HashMap<>();
			snapshot.put("calendarTypeId", saved.getCalendarTypeId());
			snapshot.put("typeName", saved.getTypeName());
			snapshot.put("companyId", saved.getCompany() != null ? saved.getCompany().getCompanyId() : null);
			snapshot.put("isActive", saved.getIsActive());
			String newValue = serialize(snapshot);
			auditService.recordAudit(mapper.toSystemAuditRequest("Calendar", "CalendarType", "CalendarType",
					saved.getCalendarTypeId(),
					"DELETE",
					newValue,
					null,
					null,
					calendarType.getCreatedBy(),
					"SYSTEM",
					calendarType.getCompany() != null ? calendarType.getCompany().getCompanyId() : null,
					"SUCCESS",
					null));
		} catch (FlickzzDeskException e) {
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Calendar", "CalendarType", "CalendarType",
					null, "DELETE", null, null, null, userId, "SYSTEM", null, "FAILED", e.getDescription()), e);
			throw e;
		} catch (Exception e) {
			log.error("Exception in deleteCalendarType method in CalendarService");
			auditService.recordExceptionAudit(mapper.toSystemAuditRequest("Calendar", "CalendarType", "CalendarType",
					null, "DELETE", null, null, null, userId, "SYSTEM", null, "FAILED", e.getCause().getMessage()), e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<CalendarTypeVO> listCalendarTypes(String orgId) {
		log.info(generateLog("listCalendarTypes", this.getClass().getName()));
		try {
			return calendarTypeRepository.findAllByCompany_CompanyId(Long.valueOf(orgId)).stream()
					.map(mapper::toCalendarTypeVO).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in listCalendarTypes method in CalendarService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}
}
