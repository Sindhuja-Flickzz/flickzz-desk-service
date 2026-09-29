package com.flickzz.desk.controller;

import com.flickzz.desk.config.FlickzzDeskResponse;
import com.flickzz.desk.service.ApprovalService;
import com.flickzz.desk.vo.ApprovalMasterVO;
import com.flickzz.desk.vo.BPConfigurationChangeRequestRemarkVO;
import com.flickzz.desk.vo.request.BpConfigRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.EXIT;
import static com.flickzz.desk.config.FlickzzDeskResponseHandler.handleSuccessResponse;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.FETCH_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.UPDATE_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;

@CrossOrigin
@RestController
@RequestMapping("/approval")
public class ApprovalController {

    private static final Logger log = LoggerFactory.getLogger(ApprovalController.class);

    @Autowired
    private ApprovalService approvalService;

    @GetMapping("/list/{userId}")
    public ResponseEntity<FlickzzDeskResponse> getBusinessPartnerApprovalList(
            @PathVariable String userId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<ApprovalMasterVO> response = approvalService
                .getBusinessPartnerApprovalList(Long.valueOf(userId));

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "Approval"), response);
    }

    @PostMapping("/action")
    public ResponseEntity<FlickzzDeskResponse> actionOnConfigApproval(@RequestBody BpConfigRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        ApprovalMasterVO response = approvalService.actionOnConfigApproval(request);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), request.getAction()), response);
    }

    @GetMapping("/remark/{approvalId}")
    public ResponseEntity<FlickzzDeskResponse> getApprovalRemarks(
            @PathVariable String approvalId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<BPConfigurationChangeRequestRemarkVO> response = approvalService
                .getApprovalRemarks(Long.valueOf(approvalId));

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "Approval Progress Remark"), response);
    }
}
