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
@RequestMapping("/company")
public class CompanyController {

	private static final Logger log = LoggerFactory.getLogger(CompanyController.class);

	@Autowired
	private CompanyService companyService;

	@PostMapping("/create")
	public ResponseEntity<FlickzzDeskResponse> createCompany(@RequestBody CompanyMasterRequestVO request)
			throws Exception {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		CompanyMasterVO respVO = companyService.createCompany(request);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), COMPANY), respVO);
	}

	@GetMapping("/{companyId}")
	public ResponseEntity<FlickzzDeskResponse> getCompanyInfo(@PathVariable String companyId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		CompanyMasterVO response = companyService.getCompanyInfo(companyId);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), COMPANY), response);
	}

	@PostMapping("/update")
	public ResponseEntity<FlickzzDeskResponse> updateCompany(@RequestBody CompanyMasterRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		CompanyMasterVO response = companyService.updateCompany(request);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), COMPANY),
				response);
	}

	@DeleteMapping("/delete/{companyId}")
	public ResponseEntity<FlickzzDeskResponse> deleteCompany(@PathVariable String companyId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		companyService.deleteCompany(companyId);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), COMPANY));
	}

	@GetMapping("/list")
	public ResponseEntity<FlickzzDeskResponse> listCompanies() {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		List<CompanyMasterVO> response = companyService.listCompanies();

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), COMPANY), response);
	}

	@GetMapping("/provider/list/{orgId}")
	public ResponseEntity<FlickzzDeskResponse> listServiceProviderList(@PathVariable String orgId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));

		List<CompanyRoleVO> response = companyService.listServiceProviderList(orgId);

		log.debug(generateLog(EXIT, this.getClass().getName()));
		return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), COMPANY_ROLE),
				response);

	}
}
