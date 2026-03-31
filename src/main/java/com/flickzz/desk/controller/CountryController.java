package com.flickzz.desk.controller;

import static com.flickzz.desk.config.FlickzzDeskConstants.COUNTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.EXIT;
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
import org.springframework.web.bind.annotation.RestController;

import com.flickzz.desk.config.FlickzzDeskResponse;
import com.flickzz.desk.service.CountryService;
import com.flickzz.desk.vo.CountryMasterVO;

@CrossOrigin
@RestController
@RequestMapping("/country")
public class CountryController {
	
	private static final Logger log = LoggerFactory.getLogger(CountryController.class);
	
	@Autowired
	private CountryService countryService;
	
	@GetMapping("/{countryId}")
    public ResponseEntity<FlickzzDeskResponse> getCountyInfo(@PathVariable String countryId) {
        log.debug(generateLog(ENTRY, this.getClass().getName()));
        
        CountryMasterVO response = countryService.getCountyInfo(countryId);
        
        log.debug(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), COUNTRY), response);
    }

    @GetMapping("/list")
    public ResponseEntity<FlickzzDeskResponse> getAgentList() {
        log.debug(generateLog(ENTRY, this.getClass().getName()));
        
        List<CountryMasterVO> response = countryService.getCountyList();
        
        log.debug(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), COUNTRY), response);
    }
}
