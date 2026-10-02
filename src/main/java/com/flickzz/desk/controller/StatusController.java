package com.flickzz.desk.controller;

import com.flickzz.desk.config.FlickzzDeskResponse;
import com.flickzz.desk.service.StatusService;
import com.flickzz.desk.vo.StatusMasterVO;
import com.flickzz.desk.vo.request.StatusVisibilityUpdateRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskResponseHandler.handleSuccessResponse;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;

@CrossOrigin
@RestController
@RequestMapping("/status")
public class StatusController {

    private static final Logger log = LoggerFactory.getLogger(StatusController.class);

    @Autowired
    private StatusService statusService;

    @PostMapping("/create")
    public ResponseEntity<FlickzzDeskResponse> createStatus(@RequestBody List<StatusMasterVO> statusVOS) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        statusService.createStatus(statusVOS);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), "RITM status"));
    }

    @GetMapping("/get/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> getStatus(@PathVariable("orgId") Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<StatusMasterVO> statuses = statusService.getStatusByOrgId(orgId, INACTIVE);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "RITM status"), statuses);
    }

    @GetMapping("/get/active/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> getActiveStatus(@PathVariable("orgId") Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<StatusMasterVO> statuses = statusService.getStatusByOrgId(orgId, ACTIVE);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "RITM status"), statuses);
    }

    @DeleteMapping("/delete/{statusId}")
    public ResponseEntity<FlickzzDeskResponse> deleteStatus(@PathVariable("statusId") Long statusId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        statusService.deleteStatus(statusId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), "RITM status"));
    }

    @PostMapping("/update")
    public ResponseEntity<FlickzzDeskResponse> updateStatus(@RequestBody StatusVisibilityUpdateRequestVO status) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        statusService.updateStatus(status);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS, "RITM status visibility updated successfully");
    }

    @PostMapping("/change")
    public ResponseEntity<FlickzzDeskResponse> changeStatus(@RequestBody StatusMasterVO status) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        statusService.changeStatus(status);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS, "RITM status changed successfully");
    }
}
