package com.flickzz.desk.controller;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskResponseHandler.*;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.*;

import java.util.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.flickzz.desk.config.*;
import com.flickzz.desk.service.*;
import com.flickzz.desk.vo.*;

@CrossOrigin
@RestController
@RequestMapping("/settings")
public class SettingsController {

	private static final Logger log = LoggerFactory.getLogger(SettingsController.class);

	@Autowired
	private SettingsService settingsService;

	@PostMapping("/business/service/create")
	public ResponseEntity<FlickzzDeskResponse> createBusinessService(
			@RequestBody BusinessServiceRequestVO businessServiceRequestVO) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		BusinessServiceVO response = settingsService.createBusinessService(businessServiceRequestVO);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), BUSINESS_SERVICE));
	}

	@PostMapping("/business/service/update")
	public ResponseEntity<FlickzzDeskResponse> updateBusinessService(
			@RequestBody BusinessServiceRequestVO businessServiceRequestVO) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		BusinessServiceVO response = settingsService.updateBusinessService(businessServiceRequestVO);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), BUSINESS_SERVICE));
	}

	@GetMapping("/business/service/list")
	public ResponseEntity<FlickzzDeskResponse> listBusinessServices() {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		List<BusinessServiceVO> response = settingsService.listBusinessServices();

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), BUSINESS_SERVICE),
				response);
	}

	@GetMapping("/business/service/{serviceId}")
	public ResponseEntity<FlickzzDeskResponse> getBusinessServiceInfo(@PathVariable String serviceId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		BusinessServiceVO response = settingsService.getBusinessServiceInfo(serviceId);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), BUSINESS_SERVICE),
				response);
	}

	@DeleteMapping("/business/service/delete/{serviceId}")
	public ResponseEntity<FlickzzDeskResponse> deleteBusinessService(@PathVariable String serviceId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		settingsService.deleteBusinessService(serviceId);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), BUSINESS_SERVICE));
	}

	@GetMapping("work/item/list/{orgId}")
	public ResponseEntity<FlickzzDeskResponse> listWorkItems(@PathVariable String orgId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		List<WorkItemVO> response = settingsService.listWorkItems(orgId);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), WORK_ITEM),
				response);
	}

	@GetMapping("field/type/list/{orgId}")
	public ResponseEntity<FlickzzDeskResponse> listFieldTypes(@PathVariable String orgId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		List<FieldTypeVO> response = settingsService.listFieldTypes(orgId);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), FIELD_TYPE),
				response);
	}
}
