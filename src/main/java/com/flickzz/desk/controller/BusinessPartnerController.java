package com.flickzz.desk.controller;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskResponseHandler.*;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.*;

import java.util.*;

import com.flickzz.desk.vo.request.BpConfigRequestVO;
import com.flickzz.desk.vo.request.CompanyMasterRequestVO;
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

	@DeleteMapping("/priority/delete")
	public ResponseEntity<FlickzzDeskResponse> deleteBusinessPartnerPriorityConfiguration(
			@RequestBody BpConfigRequestVO request) throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		businessPartnerService.deleteBusinessPartnerPriorityConfiguration(request);

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
				.getBusinessPartnerPriorityConfiguration(Long.valueOf(businessPartnerId), INACTIVE);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), PRIORITY), response);
	}

	@GetMapping("/config/priority/active/{businessPartnerId}")
	public ResponseEntity<FlickzzDeskResponse> getBusinessPartnerActivePriorityConfiguration(
			@PathVariable String businessPartnerId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<BPPriorityVO> response = businessPartnerService
				.getBusinessPartnerPriorityConfiguration(Long.valueOf(businessPartnerId), ACTIVE);

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

	@DeleteMapping("/sla/delete")
	public ResponseEntity<FlickzzDeskResponse> deleteBusinessPartnerSLAConfiguration(
			@RequestBody BpConfigRequestVO request) throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		businessPartnerService.deleteBusinessPartnerSLAConfiguration(request);

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

	@DeleteMapping("/category/delete")
	public ResponseEntity<FlickzzDeskResponse> deleteBusinessPartnerCategoryConfiguration(
			@RequestBody BpConfigRequestVO request) throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		businessPartnerService.deleteBusinessPartnerCategoryConfiguration(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), CATEGORY), null);
	}

	@GetMapping("/category/{categoryId}")
	public ResponseEntity<FlickzzDeskResponse> getBusinessPartnerCategoryConfigurationById(
			@PathVariable String categoryId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		BPCategoryVO response = businessPartnerService
				.getBusinessPartnerCategoryConfigurationById(Long.valueOf(categoryId));

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

	@PostMapping("/support-group/create")
	public ResponseEntity<FlickzzDeskResponse> createBusinessPartnerSupportGroupConfiguration(
			@RequestBody BpConfigRequestVO request) throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		BPSupportGroupVO respVO = businessPartnerService.createBusinessPartnerSupportGroupConfiguration(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), "Support Group"),
				respVO);
	}

	@PostMapping("/support-group/update")
	public ResponseEntity<FlickzzDeskResponse> updateBusinessPartnerSupportGroupConfiguration(
			@RequestBody BpConfigRequestVO request) throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		BPSupportGroupVO respVO = businessPartnerService.updateBusinessPartnerSupportGroupConfiguration(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), "Support Group"),
				respVO);
	}

	@DeleteMapping("/support-group/delete")
	public ResponseEntity<FlickzzDeskResponse> deleteBusinessPartnerSupportGroupConfiguration(
			@RequestBody BpConfigRequestVO request) throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		businessPartnerService.deleteBusinessPartnerSupportGroupConfiguration(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), "Support Group"),
				null);
	}

	@GetMapping("/support-group/{supportGroupId}")
	public ResponseEntity<FlickzzDeskResponse> getBusinessPartnerSupportGroupConfigurationById(
			@PathVariable String supportGroupId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		BPSupportGroupVO response = businessPartnerService
				.getBusinessPartnerSupportGroupConfigurationById(Long.valueOf(supportGroupId));

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "Support Group"),
				response);
	}

	@GetMapping("/config/support-group/{businessPartnerId}")
	public ResponseEntity<FlickzzDeskResponse> getBusinessPartnerSupportGroupConfiguration(
			@PathVariable String businessPartnerId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<BPSupportGroupVO> response = businessPartnerService
				.getBusinessPartnerSupportGroupConfiguration(Long.valueOf(businessPartnerId));

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "Support Group"),
				response);
	}

	@GetMapping("/config/sub-category/{businessPartnerId}")
	public ResponseEntity<FlickzzDeskResponse> getBusinessPartnerSubCategoryConfiguration(
			@PathVariable String businessPartnerId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<BPSubCategoryVO> response = businessPartnerService
				.getBusinessPartnerSubCategoryConfiguration(Long.valueOf(businessPartnerId));

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "Sub Category"),
				response);
	}

	@PostMapping("/assignment/create")
	public ResponseEntity<FlickzzDeskResponse> createBusinessPartnerAssignmentConfiguration(
			@RequestBody BpConfigRequestVO request) throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		BPAssignmentVO respVO = businessPartnerService.createBusinessPartnerAssignmentConfiguration(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), "Assignment"), respVO);
	}

	@PostMapping("/assignment/update")
	public ResponseEntity<FlickzzDeskResponse> updateBusinessPartnerAssignmentConfiguration(
			@RequestBody BpConfigRequestVO request) throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		BPAssignmentVO respVO = businessPartnerService.updateBusinessPartnerAssignmentConfiguration(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), "Assignment"), respVO);
	}

	@DeleteMapping("/assignment/delete")
	public ResponseEntity<FlickzzDeskResponse> deleteBusinessPartnerAssignmentConfiguration(
			@RequestBody BpConfigRequestVO request) throws Exception {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		businessPartnerService.deleteBusinessPartnerAssignmentConfiguration(request);

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), "Assignment"), null);
	}

	@GetMapping("/assignment/{assignmentId}")
	public ResponseEntity<FlickzzDeskResponse> getBusinessPartnerAssignmentConfigurationBySupportGroupId(
			@PathVariable String assignmentId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		BPAssignmentVO response = businessPartnerService
				.getBusinessPartnerAssignmentConfigurationBySupportGroupId(Long.valueOf(assignmentId));

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "Assignment"), response);
	}

	@GetMapping("/config/assignment/{businessPartnerId}")
	public ResponseEntity<FlickzzDeskResponse> getBusinessPartnerAssignmentConfiguration(
			@PathVariable String businessPartnerId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<BPAssignmentVO> response = businessPartnerService
				.getBusinessPartnerAssignmentConfiguration(Long.valueOf(businessPartnerId));

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "Assignment"), response);
	}

	@GetMapping("/config/approval/list/{userId}")
	public ResponseEntity<FlickzzDeskResponse> getBusinessPartnerApprovalList(
			@PathVariable String userId) {
		log.info(generateLog(ENTRY, this.getClass().getName()));

		List<ConfigChangeApprovalVO> response = businessPartnerService
				.getBusinessPartnerApprovalList(Long.valueOf(userId));

		log.info(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "Approval"), response);
	}
}
