package com.flickzz.desk.controller;

import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.EXIT;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.CALENDAR_CREATE_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.CALENDAR_DELETE_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.CALENDAR_UPDATE_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.FETCH_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskSuccessResponseHandler.handleSuccessResponse;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.flickzz.desk.service.SettingsService;
import com.flickzz.desk.vo.CalendarMasterRequestVO;
import com.flickzz.desk.vo.CalendarMasterVO;
import com.flickzz.desk.vo.GeneralRespVO;

@CrossOrigin(origins = "http://localhost:4200",
	allowedHeaders = {"Content-Type", "Authorization", "X-Requested-With"},
	methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/settings")
public class SettingsController {
	
	private static final Logger log = LoggerFactory.getLogger(SettingsController.class);
	
	@Autowired
	private SettingsService settingsService;
	
    @PostMapping("/calendar/create")
    public ResponseEntity<GeneralRespVO> createCalendar(@RequestBody CalendarMasterRequestVO request) {
        log.debug(generateLog(ENTRY, this.getClass().getName()));
        
        CalendarMasterVO response = settingsService.createCalendar(request);
        
        log.debug(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CALENDAR_CREATE_SUCCESS, CALENDAR_CREATE_SUCCESS.getDescription(), response);
    }

    @PostMapping("/calendar/update/{calendarCode}")
    public ResponseEntity<GeneralRespVO> updateCalendar(@RequestBody CalendarMasterRequestVO request) {
        log.debug(generateLog(ENTRY, this.getClass().getName()));
        
        CalendarMasterVO response = settingsService.updateCalendar(request);
        
        log.debug(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CALENDAR_UPDATE_SUCCESS, CALENDAR_UPDATE_SUCCESS.getDescription(), response);
    }

    @DeleteMapping("/calendar/{calendarCode}")
    public ResponseEntity<GeneralRespVO> deleteCalendar(@PathVariable String calendarCode) {
        log.debug(generateLog(ENTRY, this.getClass().getName()));
        
        settingsService.deleteCalendar(calendarCode);
        
        log.debug(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CALENDAR_DELETE_SUCCESS, CALENDAR_DELETE_SUCCESS.getDescription());
    }

    @GetMapping("/calendar/list")
    public ResponseEntity<GeneralRespVO> listCalendars() {
        log.debug(generateLog(ENTRY, this.getClass().getName()));
        
        List<CalendarMasterVO> response = settingsService.listCalendars();
        
        log.debug(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "Calendar"), response);
    }
}
