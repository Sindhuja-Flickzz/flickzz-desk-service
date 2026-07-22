package com.flickzz.desk.controller;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

import com.flickzz.desk.service.*;

@CrossOrigin
@RestController
@RequestMapping("/priority")
public class PriorityController {

	private static final Logger log = LoggerFactory.getLogger(PriorityController.class);

	@Autowired
	private PriorityService priorityService;

//	@PostMapping("create")
//	public ResponseEntity<FlickzzDeskResponse> createPriority(@RequestBody BpConfigRequestVO request) throws Exception {
//		log.info(generateLog(ENTRY, this.getClass().getName()));
//
//		BPPriorityVO respVO = priorityService.createPriority(request);
//
//		log.info(generateLog(EXIT, this.getClass().getName()));
//		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), PRIORITY), respVO);
//	}
//
//	@GetMapping("/{priorityId}")
//	public ResponseEntity<FlickzzDeskResponse> getPriorityInfo(@PathVariable String priorityId) {
//		log.info(generateLog(ENTRY, this.getClass().getName()));
//
//		BPPriorityVO response = priorityService.getPriorityInfo(priorityId);
//
//		log.info(generateLog(EXIT, this.getClass().getName()));
//		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), PRIORITY), response);
//	}
//
//	@PostMapping("/update")
//	public ResponseEntity<FlickzzDeskResponse> updatePriority(@RequestBody BpConfigRequestVO request) {
//		log.info(generateLog(ENTRY, this.getClass().getName()));
//
//		BPPriorityVO response = priorityService.updatePriority(request);
//
//		log.info(generateLog(EXIT, this.getClass().getName()));
//		return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), PRIORITY),
//				response);
//	}
//
//	@DeleteMapping("/delete/{priorityId}")
//	public ResponseEntity<FlickzzDeskResponse> deletePriority(@PathVariable String priorityId) {
//		log.info(generateLog(ENTRY, this.getClass().getName()));
//
//		priorityService.deletePriority(priorityId);
//
//		log.info(generateLog(EXIT, this.getClass().getName()));
//		return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), PRIORITY));
//	}
//
//	@GetMapping("/list")
//	public ResponseEntity<FlickzzDeskResponse> getPriorityList() {
//		log.info(generateLog(ENTRY, this.getClass().getName()));
//
//		List<BPPriorityVO> response = priorityService.getPriorityList();
//
//		log.info(generateLog(EXIT, this.getClass().getName()));
//		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), PRIORITY), response);
//	}
}
