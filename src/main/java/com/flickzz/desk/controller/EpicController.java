package com.flickzz.desk.controller;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskResponseHandler.*;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.flickzz.desk.config.*;
import com.flickzz.desk.service.*;
import com.flickzz.desk.vo.*;

@CrossOrigin
@RestController
@RequestMapping("/epic")
public class EpicController {

	private static final Logger log = LoggerFactory.getLogger(EpicController.class);

	@Autowired
	private EpicService epicService;

	@GetMapping("/{epicId}")
	public ResponseEntity<FlickzzDeskResponse> getProjectInfo(@PathVariable String epicId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		EpicVO response = epicService.getEpicInfo(epicId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), EPIC), response);
	}
}
