package com.flickzz.desk.controller;

import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.EXIT;
import static com.flickzz.desk.config.FlickzzDeskConstants.IMPACT;
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
import com.flickzz.desk.service.ImpactService;
import com.flickzz.desk.vo.ImpactMasterVO;
import com.flickzz.desk.vo.ImpactRequestVO;

@CrossOrigin
@RestController
@RequestMapping("/impact")
public class ImpactController {

	private static final Logger log = LoggerFactory.getLogger(ImpactController.class);

	@Autowired
	private ImpactService impactService;

	@PostMapping("create")
	public ResponseEntity<FlickzzDeskResponse> createImpact(@RequestBody ImpactRequestVO request) throws Exception {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		ImpactMasterVO respVO = impactService.createImpact(request);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), IMPACT), respVO);
	}

	@GetMapping("/{impactId}")
	public ResponseEntity<FlickzzDeskResponse> getImpactInfo(@PathVariable String impactId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		ImpactMasterVO response = impactService.getImpactInfo(impactId);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), IMPACT), response);
	}

	@PostMapping("/update")
	public ResponseEntity<FlickzzDeskResponse> updateImpact(@RequestBody ImpactRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		ImpactMasterVO response = impactService.updateImpact(request);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), IMPACT), response);
	}

	@DeleteMapping("/delete/{impactId}")
	public ResponseEntity<FlickzzDeskResponse> deleteImpact(@PathVariable String impactId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		impactService.deleteImpact(impactId);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), PRIORITY));
	}

	@GetMapping("/list/{orgId}")
	public ResponseEntity<FlickzzDeskResponse> getImpactList(@PathVariable String orgId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		List<ImpactMasterVO> response = impactService.getImpactList(orgId);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), PRIORITY), response);
	}
}
