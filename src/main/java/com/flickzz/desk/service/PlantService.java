package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.*;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

import java.util.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.flickzz.desk.exception.*;
import com.flickzz.desk.mapper.*;
import com.flickzz.desk.model.*;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.vo.*;

@Service
public class PlantService {

	private static final Logger log = LoggerFactory.getLogger(PlantService.class);

	@Autowired
	private CountryMasterRepository countryMasterRepository;

	@Autowired
	private CompanyMasterRepository companyMasterRepository;

	@Autowired
	private CalendarMasterRepository calendarMasterRepository;

	@Autowired
	private PlantMasterRepository plantMasterRepository;

	@Autowired
	private CommonMapper mapper;

	public PlantMasterVO createPlant(PlantMasterRequestVO request) {
		log.info(generateLog("createPlant", this.getClass().getName()));
		try {
			if (request == null || request.getPlantName() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), PLANT_NAME));
			}

			Optional<CountryMaster> countryMaster = countryMasterRepository.findById(request.getCountryId());
			if (countryMaster == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), COUNTRY));
			}

			CompanyMaster company = companyMasterRepository.findByCompanyIdAndIsActive(request.getCompanyId(), ACTIVE)
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), "Company")));

			Optional<CalendarMaster> calendarMaster = calendarMasterRepository.findById(request.getCalendarId());
			if (calendarMaster == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), CALENDAR_CODE));
			}

			plantMasterRepository.findByPlantNameAndCompany_CompanyIdAndIsActive(request.getPlantName(),
					request.getCompanyId(), ACTIVE).ifPresent(c -> {
						throw new FlickzzDeskException(ALREADY_EXISTS,
								getDescription(ALREADY_EXISTS.getDescription(), PLANT));
					});

			PlantMaster plant = PlantMaster.builder().plantName(request.getPlantName()).region(countryMaster.get())
					.company(company).calendar(calendarMaster.get()).createdBy(request.getCreatedBy())
					.isCreatorAdmin(request.getIsCreatedByAdmin()).build();
			return mapper.toPlantMasterVO(plantMasterRepository.save(plant));
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createCompany method in PlantService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public PlantMasterVO getPlantInfo(String plantId) {
		log.info(generateLog("getPlantInfo", this.getClass().getName()));
		try {
			Optional<PlantMaster> plantMaster = plantMasterRepository.findById(Long.valueOf(plantId));
			if (plantMaster == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), PLANT));
			}
			return mapper.toPlantMasterVO(plantMaster.get());
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getPlantInfo method in PlantService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public PlantMasterVO updatePlant(PlantMasterRequestVO request) {
		log.info(generateLog("updatePlant", this.getClass().getName()));
		try {
			Optional<PlantMaster> existing = plantMasterRepository.findById(request.getPlantId());
			if (existing == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), PLANT));
			}

			Optional<CountryMaster> countryMaster = countryMasterRepository.findById(request.getCountryId());
			if (countryMaster == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), COUNTRY));
			}

			Optional<CalendarMaster> calendarMaster = calendarMasterRepository.findById(request.getCalendarId());
			if (calendarMaster == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), CALENDAR_CODE));
			}

			PlantMaster entity = existing.get();

			entity.setIsActive(ACTIVE);
			entity.setRegion(countryMaster.get());
			entity.setCalendar(calendarMaster.get());
			entity.setUpdatedBy(request.getUpdatedBy());
			entity.setIsUpdaterAdmin(request.getIsUpdatedByAdmin());
			return mapper.toPlantMasterVO(plantMasterRepository.save(existing.get()));
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in updatePlant method in PlantService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deletePlant(String plantId) {
		log.info(generateLog("deletePlant", this.getClass().getName()));
		try {
			Optional<PlantMaster> existing = plantMasterRepository.findByPlantId(Long.valueOf(plantId));
			if (existing == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), PLANT));
			}
			PlantMaster plantMaster = existing.get();
			plantMaster.setIsActive(false);
			plantMasterRepository.save(plantMaster);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in deletePlant method in PlantService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<PlantMasterVO> getPlantList(String orgId) {
		log.info(generateLog("getPlantList", this.getClass().getName()));
		try {
			return plantMasterRepository.findAllByCompany_CompanyIdAndIsActive(Long.valueOf(orgId), ACTIVE).stream()
					.map(plant -> mapper.toPlantMasterVO(plant)).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getPlantList method in PlantService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}
}
