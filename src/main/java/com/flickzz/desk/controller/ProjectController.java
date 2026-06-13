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
@RequestMapping("/project")
public class ProjectController {

	private static final Logger log = LoggerFactory.getLogger(ProjectController.class);

	@Autowired
	private ProjectService projectService;

	@PostMapping("/create")
	public ResponseEntity<FlickzzDeskResponse> createProject(@RequestBody ProjectRequestVO request) throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		ProjectVO respVO = projectService.createProject(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), PROJECT), respVO);
	}

	@GetMapping("/{projectId}")
	public ResponseEntity<FlickzzDeskResponse> getProjectInfo(@PathVariable String projectId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		ProjectVO response = projectService.getProjectInfo(projectId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), AGENT), response);
	}

	@PutMapping("/update")
	public ResponseEntity<FlickzzDeskResponse> updateProject(@RequestBody ProjectRequestVO request) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		ProjectVO response = projectService.updateProject(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), PROJECT),
				response);
	}

	@DeleteMapping("/delete/{projectId}")
	public ResponseEntity<FlickzzDeskResponse> deleteProject(@PathVariable String projectId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		projectService.deleteProject(projectId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), PROJECT));
	}

	@GetMapping("/list/{orgId}")
	public ResponseEntity<FlickzzDeskResponse> getProjectList(@PathVariable String orgId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<ProjectVO> response = projectService.getProjectList(orgId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), PROJECT), response);
	}

	@GetMapping("status/list/{orgId}")
	public ResponseEntity<FlickzzDeskResponse> getProjectStatusList(@PathVariable String orgId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<ProgressStatusVO> response = projectService.getProgressStatusList(orgId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), PROJECT), response);
	}

	@PostMapping("/status/create")
	public ResponseEntity<FlickzzDeskResponse> createProgresstatus(@RequestBody ProgressStatusRequestVO request)
			throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		ProgressStatusVO respVO = projectService.createProgresstatus(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), PROJECT), respVO);
	}
}
