package com.flickzz.desk.controller;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskResponseHandler.handleSuccessResponse;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.CREATE_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.DELETE_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.FETCH_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.UPDATE_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;

import java.util.List;

import com.flickzz.desk.vo.AgentPlantMappingVO;
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
import com.flickzz.desk.vo.request.PlantMasterRequestVO;
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
		log.info(generateLog(ENTRY, this.getClass().getName()));

		PlantMasterVO respVO = plantService.createPlant(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), PLANT), respVO);
	}

	@GetMapping("/{plantId}")
	public ResponseEntity<FlickzzDeskResponse> getPlantInfo(@PathVariable String plantId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		PlantMasterVO response = plantService.getPlantInfo(plantId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), PLANT), response);
	}

	@PostMapping("/update")
	public ResponseEntity<FlickzzDeskResponse> updatePlant(@RequestBody PlantMasterRequestVO request) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		PlantMasterVO response = plantService.updatePlant(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), PLANT), response);
	}

	@DeleteMapping("/delete/{plantId}")
	public ResponseEntity<FlickzzDeskResponse> deletePlant(@PathVariable String plantId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		plantService.deletePlant(plantId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), PLANT));
	}

	@GetMapping("/list/{orgId}")
	public ResponseEntity<FlickzzDeskResponse> getPlantList(@PathVariable String orgId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<PlantMasterVO> response = plantService.getPlantList(orgId, Boolean.FALSE);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), PLANT), response);
	}

	@GetMapping("/list/active/{orgId}")
	public ResponseEntity<FlickzzDeskResponse> getActivePlantList(@PathVariable String orgId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<PlantMasterVO> response = plantService.getPlantList(orgId, ACTIVE);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), PLANT), response);
	}

	@PostMapping("/agent/mapping/create")
	public ResponseEntity<FlickzzDeskResponse> createAgentPlantMapping(@RequestBody PlantMasterRequestVO request) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		AgentPlantMappingVO response = plantService.createAgentPlantMapping(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), "Plant and Agent mapping"), response);
	}

	@GetMapping("/agent/mappings/{orgId}")
	public ResponseEntity<FlickzzDeskResponse> getAgentPlantMappings(@PathVariable String orgId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<AgentPlantMappingVO> response = plantService.getAgentPlantMappings(orgId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "Agent and Plant mappings"), response);
	}

	@DeleteMapping("/agent/mapping/delete/{mappingId}")
	public ResponseEntity<FlickzzDeskResponse> deleteAgentPlantMapping(@PathVariable String mappingId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));
		plantService.deleteAgentPlantMapping(mappingId);
		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), "Agent and Plant mapping"));
	}
}
