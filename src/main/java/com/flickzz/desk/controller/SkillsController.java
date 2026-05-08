package com.flickzz.desk.controller;

import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.EXIT;
import static com.flickzz.desk.config.FlickzzDeskConstants.SKILL;
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
import com.flickzz.desk.service.SkillsService;
import com.flickzz.desk.vo.SkillMasterVO;
import com.flickzz.desk.vo.SkillRequestVO;

@CrossOrigin
@RestController
@RequestMapping("/skills")
public class SkillsController {

	private static final Logger log = LoggerFactory.getLogger(SkillsController.class);

	@Autowired
	private SkillsService skillsService;

	@PostMapping("create")
	public ResponseEntity<FlickzzDeskResponse> createSkills(@RequestBody List<SkillRequestVO> request)
			throws Exception {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		List<SkillMasterVO> respVO = skillsService.createSkills(request);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), SKILL), respVO);
	}

	@GetMapping("/{skillId}")
	public ResponseEntity<FlickzzDeskResponse> getSkillInfo(@PathVariable String skillId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		SkillMasterVO response = skillsService.getSkillInfo(skillId);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), SKILL), response);
	}

	@PostMapping("/update")
	public ResponseEntity<FlickzzDeskResponse> updateSkill(@RequestBody SkillRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		SkillMasterVO response = skillsService.updateSkill(request);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), SKILL), response);
	}

	@DeleteMapping("/delete/{skillId}")
	public ResponseEntity<FlickzzDeskResponse> deleteSkill(@PathVariable String skillId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		skillsService.deleteSkill(skillId);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), SKILL));
	}

	@GetMapping("/list/{orgId}")
	public ResponseEntity<FlickzzDeskResponse> getSkillList(@PathVariable String orgId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		List<SkillMasterVO> response = skillsService.getSkillList(orgId);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), SKILL), response);
	}
}
