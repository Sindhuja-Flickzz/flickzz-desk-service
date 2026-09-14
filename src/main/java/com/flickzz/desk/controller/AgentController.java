package com.flickzz.desk.controller;

import com.flickzz.desk.config.FlickzzDeskResponse;
import com.flickzz.desk.service.AgentService;
import com.flickzz.desk.vo.AgentMasterVO;
import com.flickzz.desk.vo.AgentSkillsMappingVO;
import com.flickzz.desk.vo.request.AgentRequestVO;
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
@RequestMapping("/agent")
public class AgentController {

    private static final Logger log = LoggerFactory.getLogger(AgentController.class);

    @Autowired
    private AgentService agentService;

    @PostMapping("create")
    public ResponseEntity<FlickzzDeskResponse> createAgent(@RequestBody AgentRequestVO request) throws Exception {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        AgentMasterVO respVO = agentService.createAgent(request);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), AGENT), respVO);
    }

    @GetMapping("/{agentId}")
    public ResponseEntity<FlickzzDeskResponse> getAgentInfo(@PathVariable String agentId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        AgentMasterVO response = agentService.getAgentInfo(agentId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), AGENT), response);
    }

    @GetMapping("/get/{agentName}")
    public ResponseEntity<FlickzzDeskResponse> getAgentInfoByName(@PathVariable String agentName) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        AgentMasterVO response = agentService.getAgentInfoByName(agentName);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), AGENT), response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<FlickzzDeskResponse> getAgentInfoByEmail(@PathVariable String email) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        AgentMasterVO response = agentService.getAgentInfoByEmail(email);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), AGENT), response);
    }

    @PostMapping("/update")
    public ResponseEntity<FlickzzDeskResponse> updateAgent(@RequestBody AgentRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        AgentMasterVO response = agentService.updateAgent(request);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), AGENT), response);
    }

    @DeleteMapping("/delete/{agentId}")
    public ResponseEntity<FlickzzDeskResponse> deleteSkill(@PathVariable String agentId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        agentService.deleteAgent(agentId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), AGENT));
    }

    @GetMapping("/list/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> getAgentList(@PathVariable String orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<AgentMasterVO> response = agentService.getAgentList(orgId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), AGENT), response);
    }

    @GetMapping("/list/active/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> getActiveAgentList(@PathVariable String orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<AgentMasterVO> response = agentService.getActiveAgentList(orgId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), AGENT), response);
    }

    @GetMapping({"/support-group/list/{agentId}/{orgId}"})
    public ResponseEntity<FlickzzDeskResponse> getActiveSupportGroupIds(@PathVariable String agentId,
                                                                        @PathVariable String orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<Long> response = agentService.getActiveSupportGroupIds(agentId, orgId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), AGENT), response);
    }

    @GetMapping({"/user/{userId}"})
    public ResponseEntity<FlickzzDeskResponse> getAgentIdByUserId(@PathVariable String userId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        Long response = agentService.getAgentIdByUserId(userId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), AGENT), response);
    }

    @PostMapping({"/support-group/users"})
    public ResponseEntity<FlickzzDeskResponse> getDistinctUsersBySupportGroupIds(@RequestBody List<Long> supportGroupIds) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<AgentMasterVO> response = agentService.getDistinctUsersBySupportGroupIds(supportGroupIds);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), AGENT), response);
    }

    @GetMapping("/skills/{agentId}")
    public ResponseEntity<FlickzzDeskResponse> getAgentSkills(@PathVariable String agentId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<AgentSkillsMappingVO> response = agentService.getAgentSkills(agentId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), AGENT), response);
    }
}
