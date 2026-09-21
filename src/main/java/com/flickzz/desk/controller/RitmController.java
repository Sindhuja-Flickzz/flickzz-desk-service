package com.flickzz.desk.controller;

import com.flickzz.desk.config.FlickzzDeskResponse;
import com.flickzz.desk.service.RitmService;
import com.flickzz.desk.vo.RitmAuditVO;
import com.flickzz.desk.vo.RitmCommentVO;
import com.flickzz.desk.vo.RitmMasterVO;
import com.flickzz.desk.vo.RitmStatusVO;
import com.flickzz.desk.vo.request.RitmAssignmentRequestVO;
import com.flickzz.desk.vo.request.RitmRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskResponseHandler.handleSuccessResponse;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;

@CrossOrigin
@RestController
@RequestMapping("/ritm")
public class RitmController {

    private static final Logger log = LoggerFactory.getLogger(RitmController.class);

    @Autowired
    private RitmService ritmService;

    @PostMapping("/create")
    public ResponseEntity<FlickzzDeskResponse> createRitm(@RequestPart("ritm") RitmRequestVO ritmVO,
                                                          @RequestPart(value = "files", required = false)
                                                          List<MultipartFile> files) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        RitmMasterVO respVO = ritmService.createRitm(ritmVO, files);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), RITM), respVO);
    }

    @GetMapping("/list/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> getAllRitmList(@PathVariable("orgId") Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<RitmMasterVO> ritmList = ritmService.getAllRitmList(orgId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), RITM), ritmList);
    }

    @GetMapping({"/agent/{agentId}/{requestType}"})
    public ResponseEntity<FlickzzDeskResponse> getRitmByAgentAndRequestType(@PathVariable("agentId") Long agentId,
                                                                            @PathVariable("requestType") String requestType) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<RitmMasterVO> ritmList = ritmService.getRitmListByAgentAndRequestType(agentId, requestType);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), RITM), ritmList);
    }

    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.PUT}, consumes = "multipart/form-data")
    public ResponseEntity<FlickzzDeskResponse> updateRitm(@RequestPart("ritm") RitmRequestVO ritmVO,
                                                          @RequestPart(value = "files", required = false)
                                                          List<MultipartFile> files) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        RitmMasterVO response = ritmService.updateRitm(ritmVO, files);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), RITM), response);
    }

    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.PUT}, consumes = "application/json")
    public ResponseEntity<FlickzzDeskResponse> updateRitm(@RequestBody RitmRequestVO ritmVO) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        RitmMasterVO response = ritmService.updateRitm(ritmVO, null);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), RITM), response);
    }

    @PutMapping("/assign")
    public ResponseEntity<FlickzzDeskResponse> assignRitm(@RequestBody RitmRequestVO ritmVO) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        RitmMasterVO response = ritmService.assignRitm(ritmVO);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), RITM), response);
    }

    @PostMapping("/comment")
    public ResponseEntity<FlickzzDeskResponse> createRitmComment(@RequestBody RitmCommentVO commentVO) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        RitmCommentVO response = ritmService.saveRitmComment(commentVO);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), "RITM comment"), response);
    }

    @PutMapping("/comment")
    public ResponseEntity<FlickzzDeskResponse> updateRitmComment(@RequestBody RitmCommentVO commentVO) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        RitmCommentVO response = ritmService.saveRitmComment(commentVO);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), "RITM comment"), response);
    }

    @GetMapping("/{ritmId}")
    public ResponseEntity<FlickzzDeskResponse> getRitmDetails(@PathVariable("ritmId") Long ritmId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        RitmMasterVO ritmDetails = ritmService.getRitmDetailsById(ritmId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), RITM), ritmDetails);
    }

    @GetMapping("/{ritmId}/comments")
    public ResponseEntity<FlickzzDeskResponse> getRitmComments(@PathVariable("ritmId") Long ritmId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<RitmCommentVO> comments = ritmService.getRitmCommentsById(ritmId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "RITM comments"), comments);
    }

    @GetMapping("/{ritmId}/audits")
    public ResponseEntity<FlickzzDeskResponse> getRitmAuditHistory(@PathVariable("ritmId") Long ritmId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<RitmAuditVO> audits = ritmService.getRitmAuditHistoryById(ritmId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "RITM audit history"), audits);
    }

    @GetMapping("/unassigned/{supportGroupId}")
    public ResponseEntity<FlickzzDeskResponse> getUnassignedRitms(@PathVariable Long supportGroupId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        List<RitmMasterVO> ritms = ritmService.getUnassignedRitms(supportGroupId);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), RITM), ritms);
    }

    @PostMapping("/assigned")
    public ResponseEntity<FlickzzDeskResponse> getRitmByAssignment(@RequestBody RitmAssignmentRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<RitmMasterVO> ritms = ritmService.getRitmsByAssignment(request);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), RITM), ritms);
    }

    @PostMapping("/status/create")
    public ResponseEntity<FlickzzDeskResponse> createRitmStatus(@RequestBody List<RitmStatusVO> statusVOS) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        ritmService.createRitmStatus(statusVOS);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), "RITM status"));
    }

    @GetMapping("/get/status/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> getRitmStatus(@PathVariable("orgId") Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<RitmStatusVO> statuses = ritmService.getRitmStatusByOrgId(orgId, INACTIVE);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "RITM status"), statuses);
    }

    @GetMapping("/get/status/active/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> getRitmActiveStatus(@PathVariable("orgId") Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<RitmStatusVO> statuses = ritmService.getRitmStatusByOrgId(orgId, ACTIVE);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "RITM status"), statuses);
    }

    @DeleteMapping("/status/{statusId}")
    public ResponseEntity<FlickzzDeskResponse> deleteRitmStatus(@PathVariable("statusId") Long statusId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        ritmService.deleteRitmStatus(statusId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), "RITM status"));
    }

    @GetMapping("/status/list/{statusId}/{supportGroupId}")
    public ResponseEntity<FlickzzDeskResponse> getRitmListByStatus(@PathVariable("statusId") Long statusId, @PathVariable("supportGroupId") Long supportGroupId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<RitmMasterVO> statuses = ritmService.getRitmListByStatus(statusId, supportGroupId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "RITM status List"), statuses);
    }

    @PostMapping("/status/update")
    public ResponseEntity<FlickzzDeskResponse> updateRitmStatus(@RequestBody RitmStatusVO status) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        ritmService.updateRitmStatus(status);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS, "RITM status " + (status.getIsActive() ? "Activated" : "Deactivated") + " Successfully");
    }
}
