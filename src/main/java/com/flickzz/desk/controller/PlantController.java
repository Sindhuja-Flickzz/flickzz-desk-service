package com.flickzz.desk.controller;

import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.EXIT;
import static com.flickzz.desk.config.FlickzzDeskConstants.PLANT;
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
import com.flickzz.desk.service.PlantService;
import com.flickzz.desk.vo.PlantMasterRequestVO;
import com.flickzz.desk.vo.PlantMasterVO;

@CrossOrigin
@RestController
@RequestMapping("/plant")
public class PlantController {

	private static final Logger log = LoggerFactory.getLogger(PlantController.class);

	@Autowired
	private PlantService plantService;

	@PostMapping("create")
	public ResponseEntity<FlickzzDeskResponse> createPlant(@RequestBody PlantMasterRequestVO request) throws Exception {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		PlantMasterVO respVO = plantService.createPlant(request);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), PLANT), respVO);
	}

	@GetMapping("/{plantId}")
	public ResponseEntity<FlickzzDeskResponse> getPlantInfo(@PathVariable String plantId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		PlantMasterVO response = plantService.getPlantInfo(plantId);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), PLANT), response);
	}

	@PostMapping("/update")
	public ResponseEntity<FlickzzDeskResponse> updatePlant(@RequestBody PlantMasterRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		PlantMasterVO response = plantService.updatePlant(request);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), PLANT), response);
	}

	@DeleteMapping("/delete/{plantId}")
	public ResponseEntity<FlickzzDeskResponse> deletePlant(@PathVariable String plantId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		plantService.deletePlant(plantId);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), PLANT));
	}

	@GetMapping("/list/{orgId}")
	public ResponseEntity<FlickzzDeskResponse> getPlantList(@PathVariable String orgId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		List<PlantMasterVO> response = plantService.getPlantList(orgId);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), PLANT), response);
	}
}
