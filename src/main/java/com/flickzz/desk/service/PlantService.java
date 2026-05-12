package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.ACTIVE;
import static com.flickzz.desk.config.FlickzzDeskConstants.CALENDAR_CODE;
import static com.flickzz.desk.config.FlickzzDeskConstants.COUNTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.PLANT;
import static com.flickzz.desk.config.FlickzzDeskConstants.PLANT_NAME;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.ALREADY_EXISTS;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DEFAULT_ERROR_CODE;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DOES_NOT_EXIST;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.INVALID_FIELD;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.CalendarMaster;
import com.flickzz.desk.model.CompanyMaster;
import com.flickzz.desk.model.CountryMaster;
import com.flickzz.desk.model.PlantMaster;
import com.flickzz.desk.repo.CalendarMasterRepository;
import com.flickzz.desk.repo.CompanyMasterRepository;
import com.flickzz.desk.repo.CountryMasterRepository;
import com.flickzz.desk.repo.PlantMasterRepository;
import com.flickzz.desk.vo.PlantMasterRequestVO;
import com.flickzz.desk.vo.PlantMasterVO;

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
		log.debug(generateLog("createPlant", this.getClass().getName()));
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
					.company(company).calendar(calendarMaster.get()).createdBy(request.getCreatedBy()).build();
			return mapper.toPlantMasterVO(plantMasterRepository.save(plant));
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createCompany method in PlantService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public PlantMasterVO getPlantInfo(String plantId) {
		log.debug(generateLog("getPlantInfo", this.getClass().getName()));
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
		log.debug(generateLog("updatePlant", this.getClass().getName()));
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
			return mapper.toPlantMasterVO(plantMasterRepository.save(existing.get()));
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in updatePlant method in PlantService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deletePlant(String plantId) {
		log.debug(generateLog("deletePlant", this.getClass().getName()));
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
		log.debug(generateLog("getPlantList", this.getClass().getName()));
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
