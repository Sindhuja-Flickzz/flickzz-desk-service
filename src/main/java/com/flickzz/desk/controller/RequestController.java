package com.flickzz.desk.controller;

import com.flickzz.desk.config.FlickzzDeskResponse;
import com.flickzz.desk.service.RequestApproverService;
import com.flickzz.desk.service.RequestService;
import com.flickzz.desk.vo.RequestApproverConfigVO;
import com.flickzz.desk.vo.RequestConfigVO;
import com.flickzz.desk.vo.request.RequestApproverRequestVO;
import com.flickzz.desk.vo.request.RequestConfigRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskResponseHandler.handleSuccessResponse;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.FETCH_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;

@CrossOrigin
@RestController
@RequestMapping("/request")
public class RequestController {

    private static final Logger log = LoggerFactory.getLogger(RequestController.class);

    @Autowired
    private RequestService requestService;

    @Autowired
    private RequestApproverService requestApproverService;

    @GetMapping("/number/{requestType}")
    public ResponseEntity<FlickzzDeskResponse> getRequestNumber(@PathVariable String requestType) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        String requestNumber = requestService.getRequestNumber(requestType);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), REQUEST_NUMBER),
                requestNumber);
    }

    @PostMapping("/config/create")
    public ResponseEntity<FlickzzDeskResponse> createRequestConfig(
            @RequestBody RequestConfigRequestVO requestConfigVO) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        RequestConfigVO createdConfig = requestService.createRequestConfig(requestConfigVO);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), REQUEST_CONFIG),
                createdConfig);
    }

    @PostMapping("/config/update")
    public ResponseEntity<FlickzzDeskResponse> updateRequestConfig(
            @RequestBody RequestConfigRequestVO requestConfigVO) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        RequestConfigVO updatedConfig = requestService.updateRequestConfig(requestConfigVO);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), REQUEST_CONFIG),
                updatedConfig);
    }

    @GetMapping("/config/{requestType}/{plantId}")
    public ResponseEntity<FlickzzDeskResponse> getRequestConfig(@PathVariable String requestType,
                                                                @PathVariable Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        RequestConfigVO requestConfig = requestService.getRequestConfig(requestType, orgId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), REQUEST_CONFIG),
                requestConfig);
    }

    @GetMapping("/config/list/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> getAllRequestConfigs(@PathVariable Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<RequestConfigVO> requestConfigs = requestService.getAllRequestConfigs(orgId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), REQUEST_CONFIG),
                requestConfigs);
    }

    @DeleteMapping("/config/delete/{configId}")
    public ResponseEntity<FlickzzDeskResponse> deleteRequestConfig(@PathVariable Long configId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        requestService.deleteRequestConfig(configId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), REQUEST_CONFIG));
    }

    @PostMapping("/approver/config/create")
    public ResponseEntity<FlickzzDeskResponse> createRequestApprover(
            @RequestBody RequestApproverRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        RequestApproverConfigVO created = requestApproverService.createRequestApprover(request);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "Request Approver"), created);
    }

    @PostMapping("/approver/config/update")
    public ResponseEntity<FlickzzDeskResponse> updateRequestApprover(
            @RequestBody RequestApproverRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        RequestApproverConfigVO updated = requestApproverService.updateRequestApprover(request);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "Request Approver"), updated);
    }

    @GetMapping("/approver/config/list/{companyId}")
    public ResponseEntity<FlickzzDeskResponse> listRequestApprovers(@PathVariable Long companyId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        List<RequestApproverConfigVO> approvers = requestApproverService.listRequestApprovers(companyId);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "Request Approvers"), approvers);
    }

    @DeleteMapping("/approver/config/delete/{configId}")
    public ResponseEntity<FlickzzDeskResponse> deleteRequestApprover(@PathVariable Long configId,
                                                                     @RequestParam(required = false) Long deletedBy,
                                                                     @RequestParam(required = false) Boolean isDeletedByAdmin) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        requestApproverService.deleteRequestApprover(configId, deletedBy, isDeletedByAdmin);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "Request Approver"));
    }
}
