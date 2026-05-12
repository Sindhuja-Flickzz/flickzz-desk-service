package com.flickzz.desk.controller;

import static com.flickzz.desk.config.FlickzzDeskConstants.COMPANY;
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
import com.flickzz.desk.service.CompanyService;
import com.flickzz.desk.vo.CompanyMasterRequestVO;
import com.flickzz.desk.vo.CompanyMasterVO;

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

}
