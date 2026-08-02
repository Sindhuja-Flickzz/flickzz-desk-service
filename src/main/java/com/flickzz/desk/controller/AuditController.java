package com.flickzz.desk.controller;

import com.flickzz.desk.config.FlickzzDeskResponse;
import com.flickzz.desk.service.AuditService;
import com.flickzz.desk.vo.SystemAuditVO;
import com.flickzz.desk.vo.request.SystemAuditFilterRequestVO;
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
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;

@CrossOrigin
@RestController
@RequestMapping("/audit")
public class AuditController {

    private static final Logger log = LoggerFactory.getLogger(AuditController.class);

    @Autowired
    private AuditService auditService;

    @GetMapping("audit/list/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> getAuditListByOrg(@PathVariable String orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<SystemAuditVO> response = auditService.getAuditListByOrg(Long.valueOf(orgId));

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "Audit"), response);
    }

    @GetMapping("audit/list")
    public ResponseEntity<FlickzzDeskResponse> getAuditListByFilter(@ModelAttribute SystemAuditFilterRequestVO filter) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<SystemAuditVO> response = auditService.getAuditListByFilter(filter);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "Audit"), response);
    }
}
