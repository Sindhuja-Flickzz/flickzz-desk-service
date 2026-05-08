package com.flickzz.desk.controller;

import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.EXIT;
import static com.flickzz.desk.config.FlickzzDeskConstants.REQUEST_CONFIG;
import static com.flickzz.desk.config.FlickzzDeskConstants.REQUEST_NUMBER;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flickzz.desk.config.FlickzzDeskResponse;
import com.flickzz.desk.service.RequestService;
import com.flickzz.desk.vo.RequestConfigVO;

@CrossOrigin
@RestController
@RequestMapping("/request")
public class RequestController {

	private static final Logger log = LoggerFactory.getLogger(RequestController.class);

	@Autowired
	private RequestService requestService;

	@GetMapping("/number/{requestType}")
	public ResponseEntity<FlickzzDeskResponse> getRequestNumber(@PathVariable String requestType) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		String requestNumber = requestService.getRequestNumber(requestType);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), REQUEST_NUMBER),
				requestNumber);
	}

//	@PostMapping("/config/create")
//	public ResponseEntity<FlickzzDeskResponse> createRequestConfig(
//			@RequestBody RequestConfigRequestVO requestConfigVO) {
//		log.debug(generateLog(ENTRY, this.getClass().getName()));
//
//		RequestConfigVO createdConfig = requestService.createRequestConfig(requestConfigVO);
//
//		log.debug(generateLog(EXIT, this.getClass().getName()));
//		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), REQUEST_CONFIG),
//				createdConfig);
//	}
//
//	@PostMapping("/config/update")
//	public ResponseEntity<FlickzzDeskResponse> updateRequestConfig(
//			@RequestBody RequestConfigRequestVO requestConfigVO) {
//		log.debug(generateLog(ENTRY, this.getClass().getName()));
//
//		RequestConfigVO updatedConfig = requestService.updateRequestConfig(requestConfigVO);
//
//		log.debug(generateLog(EXIT, this.getClass().getName()));
//		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), REQUEST_CONFIG),
//				updatedConfig);
//	}
//
//	@GetMapping("/config/{requestType}/{plantId}")
//	public ResponseEntity<FlickzzDeskResponse> getRequestConfig(@PathVariable String requestType,
//			@PathVariable Long plantId) {
//		log.debug(generateLog(ENTRY, this.getClass().getName()));
//
//		RequestConfigVO requestConfig = requestService.getRequestConfig(requestType, plantId);
//
//		log.debug(generateLog(EXIT, this.getClass().getName()));
//		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), REQUEST_CONFIG),
//				requestConfig);
//	}

	@GetMapping("/config/list")
	public ResponseEntity<FlickzzDeskResponse> getAllRequestConfigs() {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		List<RequestConfigVO> requestConfigs = requestService.getAllRequestConfigs();

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), REQUEST_CONFIG),
				requestConfigs);
	}

	@DeleteMapping("/config/delete/{configId}")
	public ResponseEntity<FlickzzDeskResponse> deleteRequestConfig(@PathVariable Long configId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		requestService.deleteRequestConfig(configId);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), REQUEST_CONFIG));
	}
}
