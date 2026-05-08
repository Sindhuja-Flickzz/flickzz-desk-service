package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.ACTIVE;
import static com.flickzz.desk.config.FlickzzDeskConstants.CITY;
import static com.flickzz.desk.config.FlickzzDeskConstants.COUNTRY;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DEFAULT_ERROR_CODE;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DOES_NOT_EXIST;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.CityMaster;
import com.flickzz.desk.model.CountryMaster;
import com.flickzz.desk.repo.CityMasterRepository;
import com.flickzz.desk.repo.CountryMasterRepository;
import com.flickzz.desk.repo.LanguageMasterRepository;
import com.flickzz.desk.repo.StateMasterRepository;
import com.flickzz.desk.vo.CityMasterVO;
import com.flickzz.desk.vo.CountryMasterVO;
import com.flickzz.desk.vo.LanguageMasterVO;
import com.flickzz.desk.vo.StateMasterVO;

@Service
public class CommonService {

	private static final Logger log = LoggerFactory.getLogger(CommonService.class);

	@Autowired
	private CountryMasterRepository countryMasterRepository;

	@Autowired
	private StateMasterRepository stateMasterRepository;

	@Autowired
	private CityMasterRepository cityMasterRepository;

	@Autowired
	private LanguageMasterRepository languageMasterRepository;

	@Autowired
	private CommonMapper mapper;

	public CountryMasterVO getCountyInfo(String countryId) {
		log.debug(generateLog("getCountyInfo", this.getClass().getName()));
		try {
			Optional<CountryMaster> countryMaster = countryMasterRepository.findById(Long.valueOf(countryId));
			if (countryMaster == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), COUNTRY));
			}
			return mapper.toCountryMasterVO(countryMaster.get());
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getCountyInfo method in CommonService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<CountryMasterVO> getCountyList() {
		log.debug(generateLog("getCountyList", this.getClass().getName()));
		try {
			return countryMasterRepository.findAll().stream().map(mapper::toCountryMasterVO).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getCountyList method in CommonService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public CityMasterVO getCityInfo(String cityId) {
		log.debug(generateLog("getCityInfo", this.getClass().getName()));
		try {
			Optional<CityMaster> cityMaster = cityMasterRepository.findById(Long.valueOf(cityId));
			if (cityMaster == null) {
				throw new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), CITY));
			}
			return mapper.toCityMasterVO(cityMaster.get());
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getCityInfo method in CommonService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<CityMasterVO> getCityListOfCountry(String countryId) {
		log.debug(generateLog("getCityListOfCountry", this.getClass().getName()));
		try {
			return cityMasterRepository.findByCountryCountryIdAndIsActive(Long.valueOf(countryId), ACTIVE).stream()
					.map(mapper::toCityMasterVO).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getCityListOfCountry method in CommonService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<CityMasterVO> getCityList() {
		log.debug(generateLog("getCityList", this.getClass().getName()));
		try {
			return cityMasterRepository.findAll().stream().map(mapper::toCityMasterVO).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getCityList method in CommonService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public LanguageMasterVO getLanguageInfo(String languageId) {
		log.debug(generateLog("getLanguageInfo", this.getClass().getName()));
		try {
			return mapper.toLanguageMasterVO(languageMasterRepository.findById(Long.valueOf(languageId)).get());
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getLanguageInfo method in CommonService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<LanguageMasterVO> getLanguageList() {
		log.debug(generateLog("getLanguageList", this.getClass().getName()));
		try {
			return languageMasterRepository.findAll().stream().map(mapper::toLanguageMasterVO).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getLanguageList method in CommonService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public String getCurrentTimeOfTimezone(String timezone) {
		log.debug(generateLog("getCurrentTimeOfTimezone", this.getClass().getName()));
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

			return ZonedDateTime.now(ZoneId.of(timezone)).format(formatter);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getCurrentTimeOfTimezone method in CommonService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<StateMasterVO> getStateListOfCountry(String countryId) {
		log.debug(generateLog("getStateListOfCountry", this.getClass().getName()));
		try {
			return stateMasterRepository.findByCountry_CountryIdAndIsActive(Long.valueOf(countryId), ACTIVE).stream()
					.map(state -> mapper.toStateMasterVO(state)).distinct().toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getStateListOfCountry method in CommonService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<CityMasterVO> getCityListOfState(String stateId) {
		log.debug(generateLog("getCityListOfState", this.getClass().getName()));
		try {
			return cityMasterRepository.findByStateStateIdAndIsActive(Long.valueOf(stateId), ACTIVE).stream()
					.map(city -> mapper.toCityMasterVO(city)).distinct().toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getCityListOfState method in CommonService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public List<StateMasterVO> getAllStateList() {
		log.debug(generateLog("getAllStateList", this.getClass().getName()));
		try {
			return stateMasterRepository.findAllByIsActive(ACTIVE).stream().map(state -> mapper.toStateMasterVO(state))
					.distinct().toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getAllStateList method in CommonService");
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}
}
