package com.flickzz.desk.controller;

import static com.flickzz.desk.config.FlickzzDeskConstants.CALENDAR;
import static com.flickzz.desk.config.FlickzzDeskConstants.CALENDAR_TYPE;
import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.EXIT;
import static com.flickzz.desk.config.FlickzzDeskResponseHandler.handleSuccessResponse;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.CREATE_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.DELETE_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.FETCH_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.UPDATE_SUCCESS;
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
import org.springframework.web.bind.annotation.RestController;

import com.flickzz.desk.config.FlickzzDeskResponse;
import com.flickzz.desk.service.CalendarService;
import com.flickzz.desk.vo.CalendarMasterRequestVO;
import com.flickzz.desk.vo.CalendarMasterVO;
import com.flickzz.desk.vo.CalendarTypeVO;

@CrossOrigin
@RestController
@RequestMapping("/calendar")
public class CalendarController {

	private static final Logger log = LoggerFactory.getLogger(CalendarController.class);

	@Autowired
	private CalendarService calendarService;

	@PostMapping("/create")
	public ResponseEntity<FlickzzDeskResponse> createCalendar(@RequestBody CalendarMasterRequestVO request) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		CalendarMasterVO response = calendarService.createCalendar(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), CALENDAR),
				response);
	}

	@GetMapping("/{calendarCode}")
	public ResponseEntity<FlickzzDeskResponse> getCalendarInfo(@PathVariable String calendarCode) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		CalendarMasterVO response = calendarService.getCalendarInfo(calendarCode);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), CALENDAR), response);
	}

	@PostMapping("/update/{calendarCode}")
	public ResponseEntity<FlickzzDeskResponse> updateCalendar(@RequestBody CalendarMasterRequestVO request) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		CalendarMasterVO response = calendarService.updateCalendar(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), CALENDAR),
				response);
	}

	@DeleteMapping("/delete/{calendarCode}")
	public ResponseEntity<FlickzzDeskResponse> deleteCalendar(@PathVariable String calendarCode) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		calendarService.deleteCalendar(calendarCode);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), CALENDAR));
	}

	@GetMapping("/list/{orgId}")
	public ResponseEntity<FlickzzDeskResponse> listCalendars(@PathVariable String orgId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<CalendarMasterVO> response = calendarService.listCalendars(orgId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), CALENDAR), response);
	}

	@PostMapping("/type/create")
	public ResponseEntity<FlickzzDeskResponse> createCalendarType(@RequestBody CalendarMasterRequestVO request) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<CalendarTypeVO> response = calendarService.createCalendarType(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), CALENDAR_TYPE),
				response);
	}

	@DeleteMapping("/type/delete/{calendarTypeId}")
	public ResponseEntity<FlickzzDeskResponse> deleteCalendarType(@PathVariable String calendarTypeId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		calendarService.deleteCalendarType(calendarTypeId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), CALENDAR_TYPE));
	}

	@GetMapping("/type/list/{orgId}")
	public ResponseEntity<FlickzzDeskResponse> listCalendarTypes(@PathVariable String orgId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<CalendarTypeVO> response = calendarService.listCalendarTypes(orgId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), CALENDAR_TYPE),
				response);
	}
}
