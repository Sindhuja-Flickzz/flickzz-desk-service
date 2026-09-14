package com.flickzz.desk.controller;

import com.flickzz.desk.config.FlickzzDeskResponse;
import com.flickzz.desk.service.TemplateDetailsService;
import com.flickzz.desk.vo.TemplateFieldVO;
import com.flickzz.desk.vo.TemplateVO;
import com.flickzz.desk.vo.request.TemplateDetailsRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.EXIT;
import static com.flickzz.desk.config.FlickzzDeskResponseHandler.handleSuccessResponse;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;

@CrossOrigin
@RestController
@RequestMapping("/template")
public class TemplateController {

    private static final Logger log = LoggerFactory.getLogger(TemplateController.class);

    @Autowired
    private TemplateDetailsService templateDetailsService;

    @PostMapping("/create")
    public ResponseEntity<FlickzzDeskResponse> createTemplateDetails(@RequestBody TemplateDetailsRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        TemplateVO templateVO = templateDetailsService.createTemplateDetails(request);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), "Template"),
                templateVO);
    }

    @PutMapping("/update")
    public ResponseEntity<FlickzzDeskResponse> updateTemplateDetails(@RequestBody TemplateDetailsRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        TemplateVO templateVO = templateDetailsService.updateTemplateDetails(request);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), "Template"),
                templateVO);
    }

    @PostMapping("/details/create")
    public ResponseEntity<FlickzzDeskResponse> createTemplateFieldDetails(
            @RequestBody TemplateDetailsRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        TemplateFieldVO field = templateDetailsService.createTemplateFieldDetails(request);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), "Template Field"),
                field);
    }

    @PutMapping("/details/update")
    public ResponseEntity<FlickzzDeskResponse> updateTemplateFieldDetails(
            @RequestBody TemplateDetailsRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        TemplateFieldVO field = templateDetailsService.updateTemplateFieldDetails(request);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), "Template Field"),
                field);
    }

    @GetMapping("/list/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> listTemplateDetails(@PathVariable Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<TemplateVO> templates = templateDetailsService.listTemplateDetails(orgId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "Templates"),
                templates);
    }

    @GetMapping("/get/{templateId}")
    public ResponseEntity<FlickzzDeskResponse> getTemplateDetails(@PathVariable Long templateId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        TemplateVO templateVO = templateDetailsService.getTemplateDetail(templateId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), "Template"),
                templateVO);
    }

    @GetMapping("/details/default/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> listTemplateFieldsWithDefaultValue(@PathVariable Long orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<TemplateFieldVO> fields = templateDetailsService.listTemplateFieldsWithDefaultValue(orgId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS,
                getDescription(FETCH_SUCCESS.getDescription(), "Template Fields"), fields);
    }

    @DeleteMapping("/details/default")
    public ResponseEntity<FlickzzDeskResponse> deleteTemplateFieldDefaultValue(@RequestBody TemplateDetailsRequestVO request) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        templateDetailsService.deleteTemplateFieldDefaultValue(request.getFieldId(), request.getCompanyId(), request.getUpdatedBy());

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS,
                getDescription(UPDATE_SUCCESS.getDescription(), "Template Field Default Value"));
    }

    @DeleteMapping("/{templateId}")
    public ResponseEntity<FlickzzDeskResponse> deleteTemplateDetails(@PathVariable Long templateId,
                                                                     @RequestParam Long companyId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        templateDetailsService.deleteTemplateDetails(templateId, companyId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(UPDATE_SUCCESS, getDescription(UPDATE_SUCCESS.getDescription(), "Template"));
    }

}
