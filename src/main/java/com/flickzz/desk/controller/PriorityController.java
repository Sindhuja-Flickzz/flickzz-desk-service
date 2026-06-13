package com.flickzz.desk.controller;

import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.EXIT;
import static com.flickzz.desk.config.FlickzzDeskConstants.PRIORITY;
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
import com.flickzz.desk.service.PriorityService;
import com.flickzz.desk.vo.PriorityMasterVO;
import com.flickzz.desk.vo.PriorityRequestVO;

@CrossOrigin
@RestController
@RequestMapping("/priority")
public class PriorityController {

	private static final Logger log = LoggerFactory.getLogger(PriorityController.class);

	@Autowired
	private PriorityService priorityService;

	@PostMapping("create")
	public ResponseEntity<FlickzzDeskResponse> createPriority(@RequestBody PriorityRequestVO request) throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		PriorityMasterVO respVO = priorityService.createPriority(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), PRIORITY), respVO);
	}

	@GetMapping("/{priorityId}")
	public ResponseEntity<FlickzzDeskResponse> getPriorityInfo(@PathVariable String priorityId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		PriorityMasterVO response = priorityService.getPriorityInfo(priorityId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), PRIORITY), response);
	}

	@PostMapping("/update")
	public ResponseEntity<FlickzzDeskResponse> updatePriority(@RequestBody PriorityRequestVO request) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		PriorityMasterVO response = priorityService.updatePriority(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), PRIORITY),
				response);
	}

	@DeleteMapping("/delete/{priorityId}")
	public ResponseEntity<FlickzzDeskResponse> deletePriority(@PathVariable String priorityId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		priorityService.deletePriority(priorityId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), PRIORITY));
	}

	@GetMapping("/list")
	public ResponseEntity<FlickzzDeskResponse> getPriorityList() {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<PriorityMasterVO> response = priorityService.getPriorityList();

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), PRIORITY), response);
	}
}
