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
@RequestMapping("/template")
public class TemplateController {

	private static final Logger log = LoggerFactory.getLogger(TemplateController.class);

	@Autowired
	private TemplateDetailsService templateDetailsService;

	@PostMapping("/create")
	public ResponseEntity<FlickzzDeskResponse> createTemplateDetails(@RequestBody TemplateDetailsRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		TemplateDetailsVO templateVO = templateDetailsService.createTemplateDetails(request);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), "Template"),
				templateVO);
	}

	@PutMapping("/update")
	public ResponseEntity<FlickzzDeskResponse> updateTemplateDetails(@RequestBody TemplateDetailsRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		TemplateDetailsVO templateVO = templateDetailsService.updateTemplateDetails(request);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), "Template"),
				templateVO);
	}

	@GetMapping("/list/{orgId}")
	public ResponseEntity<FlickzzDeskResponse> listTemplateDetails(@PathVariable Long orgId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		List<TemplateDetailsVO> templates = templateDetailsService.listTemplateDetails(orgId);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "Templates"),
				templates);
	}

	@GetMapping("/get/{templateId}")
	public ResponseEntity<FlickzzDeskResponse> getTemplateDetails(@PathVariable Long templateId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		TemplateDetailsVO templateVO = templateDetailsService.getTemplateDetail(templateId);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "Template"),
				templateVO);
	}

	@DeleteMapping("/{templateId}")
	public ResponseEntity<FlickzzDeskResponse> deleteTemplateDetails(@PathVariable Long templateId,
			@RequestParam Long companyId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		templateDetailsService.deleteTemplateDetails(templateId, companyId);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), "Template"));
	}
}
