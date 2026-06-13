package com.flickzz.desk.controller;

import static com.flickzz.desk.config.FlickzzDeskConstants.CITY;
import static com.flickzz.desk.config.FlickzzDeskConstants.COUNTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.EXIT;
import static com.flickzz.desk.config.FlickzzDeskConstants.LANGUAGE;
import static com.flickzz.desk.config.FlickzzDeskConstants.TIME_ZONE;
import static com.flickzz.desk.config.FlickzzDeskResponseHandler.handleSuccessResponse;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.FETCH_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flickzz.desk.config.FlickzzDeskResponse;
import com.flickzz.desk.service.CommonService;
import com.flickzz.desk.vo.CityMasterVO;
import com.flickzz.desk.vo.CountryMasterVO;
import com.flickzz.desk.vo.LanguageMasterVO;
import com.flickzz.desk.vo.StateMasterVO;

@CrossOrigin
@RestController
@RequestMapping("/")
public class CommonController {

	private static final Logger log = LoggerFactory.getLogger(CommonController.class);

	@Autowired
	private CommonService commonService;

	@GetMapping("country/{countryId}")
	public ResponseEntity<FlickzzDeskResponse> getCountyInfo(@PathVariable String countryId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		CountryMasterVO response = commonService.getCountyInfo(countryId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), COUNTRY), response);
	}

	@GetMapping("country/list")
	public ResponseEntity<FlickzzDeskResponse> getCountyList() {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<CountryMasterVO> response = commonService.getCountyList();

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), COUNTRY), response);
	}

	@GetMapping("state/{countryId}")
	public ResponseEntity<FlickzzDeskResponse> getStateListOfCountry(@PathVariable String countryId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<StateMasterVO> response = commonService.getStateListOfCountry(countryId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), COUNTRY), response);
	}

	@GetMapping("city/{cityId}")
	public ResponseEntity<FlickzzDeskResponse> getCityInfo(@PathVariable String cityId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		CityMasterVO response = commonService.getCityInfo(cityId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), CITY), response);
	}

	@GetMapping("city/country/list/{countryId}")
	public ResponseEntity<FlickzzDeskResponse> getCityListOfCountry(@PathVariable String countryId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<CityMasterVO> response = commonService.getCityListOfCountry(countryId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), CITY), response);
	}

	@GetMapping("city/state/list/{stateId}")
	public ResponseEntity<FlickzzDeskResponse> getCityListOfState(@PathVariable String stateId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<CityMasterVO> response = commonService.getCityListOfState(stateId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), CITY), response);
	}

	@GetMapping("city/list")
	public ResponseEntity<FlickzzDeskResponse> getCityList() {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<CityMasterVO> response = commonService.getCityList();

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), CITY), response);
	}

	@GetMapping("language/{languageId}")
	public ResponseEntity<FlickzzDeskResponse> getLanguageInfo(@PathVariable String languageId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		LanguageMasterVO response = commonService.getLanguageInfo(languageId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), LANGUAGE), response);
	}

	@GetMapping("language/list")
	public ResponseEntity<FlickzzDeskResponse> getLanguageList() {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<LanguageMasterVO> response = commonService.getLanguageList();

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), LANGUAGE), response);
	}

	@GetMapping("getTime")
	public ResponseEntity<FlickzzDeskResponse> getCurrentTimeOfTimezone(@RequestParam String timezone) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		String response = commonService.getCurrentTimeOfTimezone(timezone);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), TIME_ZONE),
				response);
	}

	@GetMapping("state/list")
	public ResponseEntity<FlickzzDeskResponse> getAllStateList() {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<StateMasterVO> response = commonService.getAllStateList();

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), COUNTRY), response);
	}
}
