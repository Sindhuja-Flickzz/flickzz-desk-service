package com.flickzz.desk.controller;

import com.flickzz.desk.config.FlickzzDeskResponse;
import com.flickzz.desk.service.EffortLogService;
import com.flickzz.desk.service.EffortTypeService;
import com.flickzz.desk.service.TicketClosureApprovalConfigService;
import com.flickzz.desk.service.TicketService;
import com.flickzz.desk.vo.*;
import com.flickzz.desk.vo.request.*;
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

    @Autowired
    private EffortTypeService effortTypeService;

    @Autowired
    private EffortLogService effortLogService;

    @Autowired
    private TicketClosureApprovalConfigService ticketClosureApprovalConfigService;

    @PostMapping("/approval/workflow/create")
    public ResponseEntity<FlickzzDeskResponse> createTicketClosureApprovalWorkflow(
            @RequestBody TicketClosureApprovalConfigRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        TicketClosureApprovalConfigVO response = ticketClosureApprovalConfigService.create(request);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CREATE_SUCCESS,
                getDescription(CREATE_SUCCESS.getDescription(), "ticket approval workflow"), response);
    }

    @GetMapping("/approval/workflow/list/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> listTicketClosureApprovalWorkflows(@PathVariable Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        List<TicketClosureApprovalConfigVO> response = ticketClosureApprovalConfigService.list(orgId);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS,
                getDescription(FETCH_SUCCESS.getDescription(), "ticket approval workflow"), response);
    }

    @GetMapping("/approval/workflow/list/{workItemId}/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> listTicketClosureApprovalWorkflows(
            @PathVariable Long workItemId, @PathVariable Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        List<TicketClosureApprovalConfigVO> response =
                ticketClosureApprovalConfigService.list(workItemId, orgId);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS,
                getDescription(FETCH_SUCCESS.getDescription(), "ticket approval workflow"), response);
    }

    @PostMapping("/approval/workflow/update")
    public ResponseEntity<FlickzzDeskResponse> updateTicketClosureApprovalWorkflow(
            @RequestBody TicketClosureApprovalConfigRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        TicketClosureApprovalConfigVO response = ticketClosureApprovalConfigService.update(request);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS,
                getDescription(UPDATE_SUCCESS.getDescription(), "ticket approval workflow"), response);
    }

    @DeleteMapping("/approval/workflow/delete")
    public ResponseEntity<FlickzzDeskResponse> deleteTicketClosureApprovalWorkflow(
            @RequestBody TicketClosureApprovalConfigRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        ticketClosureApprovalConfigService.delete(request);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(DELETE_SUCCESS,
                getDescription(DELETE_SUCCESS.getDescription(), "ticket approval workflow"));
    }

    @PostMapping("/update/subtask/status")
    public ResponseEntity<FlickzzDeskResponse> updateSubtaskStatus(
            @RequestBody SubtaskStatusUpdateRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        TicketMasterVO response = ticketService.updateSubtaskStatus(request);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS,
                getDescription(UPDATE_SUCCESS.getDescription(), "subtask status"), response);
    }

    @PostMapping("/create/effort")
    public ResponseEntity<FlickzzDeskResponse> createEffortLog(@RequestBody EffortLogRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        EffortLogVO response = effortLogService.create(request);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CREATE_SUCCESS,
                getDescription(CREATE_SUCCESS.getDescription(), "effort"), response);
    }

    @PostMapping("/update/effort")
    public ResponseEntity<FlickzzDeskResponse> saveEffortLog(@RequestBody EffortLogRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        boolean updating = request != null && request.getEffortId() != null;
        EffortLogVO response = updating
                ? effortLogService.update(request)
                : effortLogService.create(request);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(updating ? UPDATE_SUCCESS : CREATE_SUCCESS,
                getDescription((updating ? UPDATE_SUCCESS : CREATE_SUCCESS).getDescription(), "effort"), response);
    }

    @PutMapping("/update/effort")
    public ResponseEntity<FlickzzDeskResponse> updateEffortLog(@RequestBody EffortLogRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        EffortLogVO response = effortLogService.update(request);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS,
                getDescription(UPDATE_SUCCESS.getDescription(), "effort"), response);
    }

    @GetMapping("/effort/{ticketId}/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> getEffortLogs(@PathVariable Long ticketId,
                                                             @PathVariable Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        List<EffortLogVO> response = effortLogService.list(ticketId, orgId, INACTIVE);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS,
                getDescription(FETCH_SUCCESS.getDescription(), "effort"), response);
    }

    @GetMapping("/effort/active/{ticketId}/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> getActiveEffortLogs(@PathVariable Long ticketId,
                                                                   @PathVariable Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        List<EffortLogVO> response = effortLogService.list(ticketId, orgId, ACTIVE);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS,
                getDescription(FETCH_SUCCESS.getDescription(), "effort"), response);
    }

    @GetMapping("/effort/agent/{agentId}/{ticketId}/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> getAgentEffortLogs(@PathVariable Long agentId,
                                                                  @PathVariable Long ticketId,
                                                                  @PathVariable Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        List<EffortLogVO> response = effortLogService.listByAgent(agentId, ticketId, orgId);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS,
                getDescription(FETCH_SUCCESS.getDescription(), "agent effort"), response);
    }

    @GetMapping("/effort/summary/{ticketId}/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> getTicketEffortSummary(@PathVariable Long ticketId,
                                                                      @PathVariable Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        TicketEffortSummaryVO response = effortLogService.getTicketEffortSummary(ticketId, orgId);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS,
                getDescription(FETCH_SUCCESS.getDescription(), "ticket effort summary"), response);
    }

    @DeleteMapping("/delete/effort")
    public ResponseEntity<FlickzzDeskResponse> deleteEffortLog(@RequestParam Long effortId,
                                                               @RequestParam Long ticketId,
                                                               @RequestParam Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        effortLogService.delete(effortId, ticketId, orgId);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(DELETE_SUCCESS,
                getDescription(DELETE_SUCCESS.getDescription(), "effort"));
    }

    @PostMapping("/effort/type/create")
    public ResponseEntity<FlickzzDeskResponse> createEffortTypes(
            @RequestBody List<EffortTypeRequestVO> requests) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        List<EffortTypeVO> response = effortTypeService.create(requests);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CREATE_SUCCESS,
                getDescription(CREATE_SUCCESS.getDescription(), "effort type"), response);
    }

    @GetMapping("/effort/type/active/list/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> getActiveEffortTypes(@PathVariable Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        List<EffortTypeVO> response = effortTypeService.list(orgId, ACTIVE);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS,
                getDescription(FETCH_SUCCESS.getDescription(), "effort type"), response);
    }

    @GetMapping("/effort/type/list/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> getEffortTypes(@PathVariable Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        List<EffortTypeVO> response = effortTypeService.list(orgId, INACTIVE);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS,
                getDescription(FETCH_SUCCESS.getDescription(), "effort type"), response);
    }

    @PostMapping("/effort/type/status/change")
    public ResponseEntity<FlickzzDeskResponse> changeEffortTypeStatus(
            @RequestBody EffortTypeStatusRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        EffortTypeVO response = effortTypeService.changeStatus(request);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS,
                getDescription(UPDATE_SUCCESS.getDescription(), "effort type status"), response);
    }

    @DeleteMapping("/effort/type/delete")
    public ResponseEntity<FlickzzDeskResponse> deleteEffortType(@RequestParam Long effortId,
                                                                @RequestParam Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        effortTypeService.delete(effortId, orgId);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(DELETE_SUCCESS,
                getDescription(DELETE_SUCCESS.getDescription(), "effort type"));
    }

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

    @RequestMapping(value = "/update/hours/planned",
            method = {RequestMethod.POST, RequestMethod.PUT}, consumes = "application/json")
    public ResponseEntity<FlickzzDeskResponse> updatePlannedHours(@RequestBody PlannedHoursRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        PlannedHoursVO response = ticketService.updatePlannedHours(request);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS,
                getDescription(UPDATE_SUCCESS.getDescription(), "planned hours"), response);
    }

    @GetMapping("/list/hours/planned/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> getPlannedHours(@PathVariable Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        List<PlannedHoursVO> response = ticketService.getPlannedHours(orgId);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS,
                getDescription(FETCH_SUCCESS.getDescription(), "planned hours"), response);
    }

    @GetMapping("/reference/{ticketReferenceId}")
    public ResponseEntity<FlickzzDeskResponse> getTicketsByReferenceId(
            @PathVariable("ticketReferenceId") Long ticketReferenceId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<TicketMasterVO> tickets = ticketService.getTicketsByReferenceId(ticketReferenceId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS,
                getDescription(FETCH_SUCCESS.getDescription(), "tickets"), tickets);
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

    @GetMapping("/audit/history/{companyId}")
    public ResponseEntity<FlickzzDeskResponse> getTicketAuditHistoryByCompanyId(
            @PathVariable Long companyId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<TicketAuditHistoryVO> response = ticketService.getTicketAuditHistoryByCompanyId(companyId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS,
                getDescription(FETCH_SUCCESS.getDescription(), "ticket audit history"), response);
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
