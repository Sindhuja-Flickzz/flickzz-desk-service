package com.flickzz.desk.controller;

import static com.flickzz.desk.config.FlickzzDeskConstants.AGENT;
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
import com.flickzz.desk.service.AgentService;
import com.flickzz.desk.vo.AgentMasterVO;
import com.flickzz.desk.vo.AgentRequestVO;
import com.flickzz.desk.vo.AgentSkillsMappingVO;

@CrossOrigin
@RestController
@RequestMapping("/agent")
public class AgentController {

	private static final Logger log = LoggerFactory.getLogger(AgentController.class);

	@Autowired
	private AgentService agentService;

	@PostMapping("create")
	public ResponseEntity<FlickzzDeskResponse> createAgent(@RequestBody AgentRequestVO request) throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		AgentMasterVO respVO = agentService.createAgent(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), AGENT), respVO);
	}

	@GetMapping("/{agentId}")
	public ResponseEntity<FlickzzDeskResponse> getAgentInfo(@PathVariable String agentId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		AgentMasterVO response = agentService.getAgentInfo(agentId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), AGENT), response);
	}

	@GetMapping("/get/{agentName}")
	public ResponseEntity<FlickzzDeskResponse> getAgentInfoByName(@PathVariable String agentName) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		AgentMasterVO response = agentService.getAgentInfoByName(agentName);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), AGENT), response);
	}

	@GetMapping("/email/{agentName}")
	public ResponseEntity<FlickzzDeskResponse> getAgentInfoByEmail(@PathVariable String agentName) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		AgentMasterVO response = agentService.getAgentInfoByEmail(agentName);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), AGENT), response);
	}

	@PostMapping("/update")
	public ResponseEntity<FlickzzDeskResponse> updateAgent(@RequestBody AgentRequestVO request) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		AgentMasterVO response = agentService.updateAgent(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), AGENT), response);
	}

	@DeleteMapping("/delete/{agentId}")
	public ResponseEntity<FlickzzDeskResponse> deleteSkill(@PathVariable String agentId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		agentService.deleteAgent(agentId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), AGENT));
	}

	@GetMapping("/list/{orgId}")
	public ResponseEntity<FlickzzDeskResponse> getAgentList(@PathVariable String orgId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<AgentMasterVO> response = agentService.getAgentList(orgId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), AGENT), response);
	}

	@GetMapping("/skills/{agentId}")
	public ResponseEntity<FlickzzDeskResponse> getAgentSkills(@PathVariable String agentId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<AgentSkillsMappingVO> response = agentService.getAgentSkills(agentId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), AGENT), response);
	}
}
