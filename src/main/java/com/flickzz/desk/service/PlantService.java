package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.CALENDAR_CODE;
import static com.flickzz.desk.config.FlickzzDeskConstants.COUNTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
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
import com.flickzz.desk.model.CountryMaster;
import com.flickzz.desk.model.PlantMaster;
import com.flickzz.desk.repo.CalendarMasterRepository;
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
	private CalendarMasterRepository calendarMasterRepository;
	
	@Autowired
	private PlantMasterRepository plantMasterRepository;
	
	@Autowired
	private CommonMapper mapper;
	

	public PlantMasterVO createPlant(PlantMasterRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			if (request == null || request.getPlantName() == null) {
		 		throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), PLANT_NAME));
		 	}
					
			Optional<CountryMaster> countryMaster = countryMasterRepository.findById(request.getCountryId());
			if (countryMaster == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), COUNTRY));
			}
			
			Optional<CalendarMaster> calendarMaster = calendarMasterRepository.findById(request.getCalendarId());
			if (calendarMaster == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), CALENDAR_CODE));
			}
			
		 	plantMasterRepository.findByPlantName(request.getPlantName()).ifPresent(c -> {
		 		throw new FlickzzDeskException(ALREADY_EXISTS, getDescription(ALREADY_EXISTS.getDescription(), PLANT));
		 	});
		 	
			PlantMaster plant = PlantMaster.builder()
					.plantName(request.getPlantName())
					.region(countryMaster.get())
					.calendar(calendarMaster.get())
					.createdBy(request.getCreatedBy()).build();
			return mapper.toPlantMasterVO(plantMasterRepository.save(plant));
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createCompany method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public PlantMasterVO getPlantInfo(String plantId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			Optional<PlantMaster> plantMaster = plantMasterRepository.findById(Long.valueOf(plantId));
			if (plantMaster == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), PLANT));
			}
			return mapper.toPlantMasterVO(plantMaster.get());
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getPlantInfo method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public PlantMasterVO updatePlant(PlantMasterRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			Optional<PlantMaster> existing = plantMasterRepository.findById(request.getPlantId());
			if (existing == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), PLANT));
			}
			
			Optional<CountryMaster> countryMaster = countryMasterRepository.findById(request.getCountryId());
			if (countryMaster == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), COUNTRY));
			}
			
			Optional<CalendarMaster> calendarMaster = calendarMasterRepository.findById(request.getCalendarId());
			if (calendarMaster == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), CALENDAR_CODE));
			}
					
			PlantMaster entity = existing.get();
			
			entity.setRegion(countryMaster.get());
			entity.setCalendar(calendarMaster.get());
			entity.setUpdatedBy(request.getUpdatedBy());
			return mapper.toPlantMasterVO(plantMasterRepository.save(existing.get()));
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in updatePlant method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public void deletePlant(String plantId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			Optional<PlantMaster> existing = plantMasterRepository.findByPlantId(plantId);
			if (existing == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), PLANT));
			}
			plantMasterRepository.delete(existing.get());
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in deletePlant method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<PlantMasterVO> getPlantList() {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			return plantMasterRepository.findAll().stream().map(plant -> mapper.toPlantMasterVO(plant)).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getPlantList method in FlickzzDeskService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}
}
