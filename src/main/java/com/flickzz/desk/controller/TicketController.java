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
@RequestMapping("/ticket")
public class TicketController {

	private static final Logger log = LoggerFactory.getLogger(TicketController.class);

	@Autowired
	private TicketService ticketService;

	@GetMapping("/type/list")
	public ResponseEntity<FlickzzDeskResponse> getTicketTypeMasterList() {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<TicketTypeMasterVO> response = ticketService.getTicketTypeMasterList();

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), PRIORITY), response);
	}
}
