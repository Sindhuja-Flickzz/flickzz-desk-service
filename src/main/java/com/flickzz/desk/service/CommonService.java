package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.ACTIVE;
import static com.flickzz.desk.config.FlickzzDeskConstants.CITY;
import static com.flickzz.desk.config.FlickzzDeskConstants.COUNTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
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
import com.flickzz.desk.vo.CityMasterVO;
import com.flickzz.desk.vo.CountryMasterVO;
import com.flickzz.desk.vo.LanguageMasterVO;

@Service
public class CommonService {

	private static final Logger log = LoggerFactory.getLogger(CommonService.class);

	@Autowired
	private CountryMasterRepository countryMasterRepository;

	@Autowired
	private CityMasterRepository cityMasterRepository;

	@Autowired
	private LanguageMasterRepository languageMasterRepository;

	@Autowired
	private CommonMapper mapper;

	public CountryMasterVO getCountyInfo(String countryId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
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
		log.debug(generateLog(ENTRY, this.getClass().getName()));
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
		log.debug(generateLog(ENTRY, this.getClass().getName()));
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
		log.debug(generateLog(ENTRY, this.getClass().getName()));
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
		log.debug(generateLog(ENTRY, this.getClass().getName()));
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
		log.debug(generateLog(ENTRY, this.getClass().getName()));
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
		log.debug(generateLog(ENTRY, this.getClass().getName()));
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
		log.debug(generateLog(ENTRY, this.getClass().getName()));
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
}
