package com.flickzz.desk.controller;

import com.flickzz.desk.config.FlickzzDeskResponse;
import com.flickzz.desk.service.TicketService;
import com.flickzz.desk.vo.*;
import com.flickzz.desk.vo.request.RitmApproverRequestVO;
import com.flickzz.desk.vo.request.RitmAssignmentRequestVO;
import com.flickzz.desk.vo.request.RitmRequestTypeRequestVO;
import com.flickzz.desk.vo.request.RitmRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskResponseHandler.handleSuccessResponse;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;

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

    @PostMapping("/approver/assign")
    public ResponseEntity<FlickzzDeskResponse> createRitmApprovers(@RequestBody RitmApproverRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        List<TicketApproverVO> response = ticketService.createRitmApprovers(request);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), "RITM approver"), response);
    }

    @PutMapping("/approver/update")
    public ResponseEntity<FlickzzDeskResponse> updateRitmApprovers(@RequestBody RitmApproverRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        List<TicketApproverVO> response = ticketService.updateRitmApprovers(request);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), "RITM approver"), response);
    }

    @GetMapping("/approver/list/{ritmId}")
    public ResponseEntity<FlickzzDeskResponse> getRitmApprovers(@PathVariable Long ritmId,
                                                                @RequestParam Long companyId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        List<TicketApproverVO> response = ticketService.getRitmApprovers(ritmId, companyId);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "RITM approver"), response);
    }

    @GetMapping("/approver/{ritmApproverId}")
    public ResponseEntity<FlickzzDeskResponse> getRitmApproverById(@PathVariable Long ritmApproverId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        TicketApproverVO response = ticketService.getRitmApproverById(ritmApproverId);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS,
                getDescription(FETCH_SUCCESS.getDescription(), "RITM approver"), response);
    }

    @DeleteMapping("/approver/delete/{ritmId}")
    public ResponseEntity<FlickzzDeskResponse> deleteRitmApprovers(@PathVariable Long ritmId,
                                                                   @RequestParam Long companyId,
                                                                   @RequestParam Long deletedBy,
                                                                   @RequestParam Boolean isDeletedByAdmin) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        ticketService.deleteRitmApprovers(ritmId, companyId, deletedBy, isDeletedByAdmin);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), "RITM approver"));
    }

    @PostMapping("/request/type/create")
    public ResponseEntity<FlickzzDeskResponse> createRitmRequestTypes(@RequestBody List<RitmRequestTypeRequestVO> requests) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        List<RequestTypeMasterVO> response = ticketService.createRitmRequestTypes(requests);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), "RITM request type"), response);
    }

    @DeleteMapping("/request/type/{requestTypeId}")
    public ResponseEntity<FlickzzDeskResponse> deleteRitmRequestType(
            @PathVariable Long requestTypeId, @RequestParam Long deletedBy,
            @RequestParam Boolean isDeletedByAdmin) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        ticketService.deleteRitmRequestType(requestTypeId, deletedBy, isDeletedByAdmin);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(DELETE_SUCCESS,
                getDescription(DELETE_SUCCESS.getDescription(), "RITM request type"));
    }

    @GetMapping("/request/type/list/{companyId}")
    public ResponseEntity<FlickzzDeskResponse> getRitmRequestTypes(@PathVariable Long companyId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        List<RequestTypeMasterVO> response = ticketService.getRitmRequestTypes(companyId);
        return handleSuccessResponse(FETCH_SUCCESS,
                getDescription(FETCH_SUCCESS.getDescription(), "RITM request type"), response);
    }

    @PostMapping("/create")
    public ResponseEntity<FlickzzDeskResponse> createRitm(@RequestPart("ritm") RitmRequestVO ritmVO,
                                                          @RequestPart(value = "files", required = false)
                                                          List<MultipartFile> files) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        TicketMasterVO respVO = ticketService.createRitm(ritmVO, files);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), RITM), respVO);
    }

    @GetMapping("/list/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> getAllRitmList(@PathVariable("orgId") Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<TicketMasterVO> ritmList = ticketService.getAllRitmList(orgId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), RITM), ritmList);
    }

    @GetMapping({"/agent/{agentId}/{requestType}"})
    public ResponseEntity<FlickzzDeskResponse> getRitmByAgentAndRequestType(@PathVariable("agentId") Long agentId,
                                                                            @PathVariable("requestType") String requestType) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<TicketMasterVO> ritmList = ticketService.getRitmListByAgentAndRequestType(agentId, requestType);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), RITM), ritmList);
    }

    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.PUT}, consumes = "multipart/form-data")
    public ResponseEntity<FlickzzDeskResponse> updateRitm(@RequestPart("ritm") RitmRequestVO ritmVO,
                                                          @RequestPart(value = "files", required = false)
                                                          List<MultipartFile> files) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        TicketMasterVO response = ticketService.updateRitm(ritmVO, files);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), RITM), response);
    }

    @RequestMapping(value = "/update", method = {RequestMethod.POST, RequestMethod.PUT}, consumes = "application/json")
    public ResponseEntity<FlickzzDeskResponse> updateRitm(@RequestBody RitmRequestVO ritmVO) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        TicketMasterVO response = ticketService.updateRitm(ritmVO, null);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), RITM), response);
    }

    @PutMapping("/assign")
    public ResponseEntity<FlickzzDeskResponse> assignRitm(@RequestBody RitmRequestVO ritmVO) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        TicketMasterVO response = ticketService.assignRitm(ritmVO);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), RITM), response);
    }

    @PostMapping("/comment")
    public ResponseEntity<FlickzzDeskResponse> createRitmComment(@RequestBody TicketCommentVO commentVO) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        TicketCommentVO response = ticketService.saveRitmComment(commentVO);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), "RITM comment"), response);
    }

    @PutMapping("/comment")
    public ResponseEntity<FlickzzDeskResponse> updateRitmComment(@RequestBody TicketCommentVO commentVO) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        TicketCommentVO response = ticketService.saveRitmComment(commentVO);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), "RITM comment"), response);
    }

    @GetMapping("/{ritmId}")
    public ResponseEntity<FlickzzDeskResponse> getRitmDetails(@PathVariable("ritmId") Long ritmId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        TicketMasterVO ritmDetails = ticketService.getRitmDetailsById(ritmId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), RITM), ritmDetails);
    }

    @GetMapping("/{ritmId}/comments")
    public ResponseEntity<FlickzzDeskResponse> getRitmComments(@PathVariable("ritmId") Long ritmId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<TicketCommentVO> comments = ticketService.getRitmCommentsById(ritmId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "RITM comments"), comments);
    }

    @GetMapping("/{ritmId}/audits")
    public ResponseEntity<FlickzzDeskResponse> getRitmAuditHistory(@PathVariable("ritmId") Long ritmId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<TicketAuditVO> audits = ticketService.getRitmAuditHistoryById(ritmId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "RITM audit history"), audits);
    }

    @GetMapping("/unassigned/{supportGroupId}")
    public ResponseEntity<FlickzzDeskResponse> getUnassignedRitms(@PathVariable Long supportGroupId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        List<TicketMasterVO> ritms = ticketService.getUnassignedRitms(supportGroupId);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), RITM), ritms);
    }

    @PostMapping("/assigned")
    public ResponseEntity<FlickzzDeskResponse> getRitmByAssignment(@RequestBody RitmAssignmentRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<TicketMasterVO> ritms = ticketService.getRitmsByAssignment(request);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), RITM), ritms);
    }

    @GetMapping("/status/list/{statusId}/{supportGroupId}")
    public ResponseEntity<FlickzzDeskResponse> getRitmListByStatus(@PathVariable("statusId") Long statusId, @PathVariable("supportGroupId") Long supportGroupId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<TicketMasterVO> statuses = ticketService.getRitmListByStatus(statusId, supportGroupId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "RITM status List"), statuses);
    }

    @GetMapping("/resolution/date/{priorityId}")
    public ResponseEntity<FlickzzDeskResponse> getRitmResolutionDate(@PathVariable("priorityId") Long priorityId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        LocalDateTime response = ticketService.calculateCustomerResolutionDate(priorityId, LocalDateTime.now());

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "RITM resolution date"), response);
    }
}
