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
@RequestMapping("/bp")
public class BusinessPartnerController {

	private static final Logger log = LoggerFactory.getLogger(BusinessPartnerController.class);

	@Autowired
	private BusinessPartnerService businessPartnerService;

	@PostMapping("/create")
	public ResponseEntity<FlickzzDeskResponse> createBusinessPartner(@RequestBody CompanyMasterRequestVO request)
			throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		BusinessPartnerVO respVO = businessPartnerService.createBusinessPartner(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), BUSINESS_PARTNER),
				respVO);
	}

	@GetMapping("/config/list/{businessPartnerId}")
	public ResponseEntity<FlickzzDeskResponse> getBusinessPartnerConfigurationList(
			@PathVariable String businessPartnerId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		BPConfigurationVO response = businessPartnerService.getBusinessPartnerConfigurationList(businessPartnerId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS,
				getDescription(FETCH_SUCCESS.getDescription(), BUSINESS_PARTNER_CONFIG), response);
	}

	@PostMapping("/priority/create")
	public ResponseEntity<FlickzzDeskResponse> createBusinessPartnerPriorityConfiguration(
			@RequestBody BpConfigRequestVO request) throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		BPPriorityVO respVO = businessPartnerService.createBusinessPartnerPriorityConfiguration(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), PRIORITY), respVO);
	}

	@PostMapping("/priority/update")
	public ResponseEntity<FlickzzDeskResponse> updateBusinessPartnerPriorityConfiguration(
			@RequestBody BpConfigRequestVO request) throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		BPPriorityVO respVO = businessPartnerService.updateBusinessPartnerPriorityConfiguration(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), PRIORITY), respVO);
	}

	@DeleteMapping("/priority/delete/{priorityId}")
	public ResponseEntity<FlickzzDeskResponse> deleteBusinessPartnerPriorityConfiguration(
			@PathVariable String priorityId) throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		businessPartnerService.deleteBusinessPartnerPriorityConfiguration(priorityId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), PRIORITY), null);
	}

	@GetMapping("/priority/{priorityId}")
	public ResponseEntity<FlickzzDeskResponse> getBusinessPartnerPriorityConfigurationById(
			@PathVariable String priorityId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		BPPriorityVO response = businessPartnerService
				.getBusinessPartnerPriorityConfigurationById(Long.valueOf(priorityId));

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), PRIORITY), response);
	}

	@GetMapping("/config/priority/{businessPartnerId}")
	public ResponseEntity<FlickzzDeskResponse> getBusinessPartnerPriorityConfiguration(
			@PathVariable String businessPartnerId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<BPPriorityVO> response = businessPartnerService
				.getBusinessPartnerPriorityConfiguration(Long.valueOf(businessPartnerId));

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), PRIORITY), response);
	}

	@PostMapping("/sla/create")
	public ResponseEntity<FlickzzDeskResponse> createBusinessPartnerSLAConfiguration(
			@RequestBody BpConfigRequestVO request) throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		BPSlaVO respVO = businessPartnerService.createBusinessPartnerSLAConfiguration(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), SLA), respVO);
	}

	@PostMapping("/sla/update")
	public ResponseEntity<FlickzzDeskResponse> updateBusinessPartnerSLAConfiguration(
			@RequestBody BpConfigRequestVO request) throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		BPSlaVO respVO = businessPartnerService.updateBusinessPartnerSLAConfiguration(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), SLA), respVO);
	}

	@DeleteMapping("/sla/delete/{slaId}")
	public ResponseEntity<FlickzzDeskResponse> deleteBusinessPartnerSLAConfiguration(@PathVariable String slaId)
			throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		businessPartnerService.deleteBusinessPartnerSLAConfiguration(slaId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), SLA), null);
	}

	@GetMapping("/sla/{slaId}")
	public ResponseEntity<FlickzzDeskResponse> getBusinessPartnerSLAConfigurationById(@PathVariable String slaId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		BPSlaVO response = businessPartnerService.getBusinessPartnerSLAConfigurationById(Long.valueOf(slaId));

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), SLA), response);
	}

	@GetMapping("/config/sla/{businessPartnerId}")
	public ResponseEntity<FlickzzDeskResponse> getBusinessPartnerSLAConfiguration(
			@PathVariable String businessPartnerId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<BPSlaVO> response = businessPartnerService
				.getBusinessPartnerSLAConfiguration(Long.valueOf(businessPartnerId));

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), SLA), response);
	}

	@PostMapping("/category/create")
	public ResponseEntity<FlickzzDeskResponse> createBusinessPartnerCategoryConfiguration(
			@RequestBody BpConfigRequestVO request) throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		BPCategoryVO respVO = businessPartnerService.createBusinessPartnerCategoryConfiguration(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), CATEGORY), respVO);
	}

	@PostMapping("/category/update")
	public ResponseEntity<FlickzzDeskResponse> updateBusinessPartnerCategoryConfiguration(
			@RequestBody BpConfigRequestVO request) throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		BPCategoryVO respVO = businessPartnerService.updateBusinessPartnerCategoryConfiguration(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), CATEGORY), respVO);
	}

	@DeleteMapping("/category/delete/{categoryId}")
	public ResponseEntity<FlickzzDeskResponse> deleteBusinessPartnerCategoryConfiguration(
			@PathVariable String categoryId) throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		businessPartnerService.deleteBusinessPartnerCategoryConfiguration(categoryId);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), CATEGORY), null);
	}

	@GetMapping("/category/{categoryId}")
	public ResponseEntity<FlickzzDeskResponse> getBusinessPartnerCategoryConfigurationById(
			@PathVariable String categoryId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		BPCategoryVO response = businessPartnerService.getBusinessPartnerCategoryConfigurationById(Long.valueOf(categoryId));

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), CATEGORY), response);
	}

	@GetMapping("/config/category/{businessPartnerId}")
	public ResponseEntity<FlickzzDeskResponse> getBusinessPartnerCategoryConfiguration(
			@PathVariable String businessPartnerId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<BPCategoryVO> response = businessPartnerService
				.getBusinessPartnerCategoryConfiguration(Long.valueOf(businessPartnerId));

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), CATEGORY), response);
	}
}
