package com.flickzz.desk.controller;

import com.flickzz.desk.config.FlickzzDeskResponse;
import com.flickzz.desk.service.SettingsService;
import com.flickzz.desk.vo.BusinessServiceVO;
import com.flickzz.desk.vo.FieldTypeVO;
import com.flickzz.desk.vo.WorkItemVO;
import com.flickzz.desk.vo.request.BusinessServiceRequestVO;
import com.flickzz.desk.vo.request.WorkItemDeleteRequestVO;
import com.flickzz.desk.vo.request.WorkItemRequestVO;
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
@RequestMapping("/settings")
public class SettingsController {

    private static final Logger log = LoggerFactory.getLogger(SettingsController.class);

    @Autowired
    private SettingsService settingsService;

    @PostMapping("/business/service/create")
    public ResponseEntity<FlickzzDeskResponse> createBusinessService(
            @RequestBody BusinessServiceRequestVO businessServiceRequestVO) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        BusinessServiceVO response = settingsService.createBusinessService(businessServiceRequestVO);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), BUSINESS_SERVICE));
    }

    @PostMapping("/business/service/update")
    public ResponseEntity<FlickzzDeskResponse> updateBusinessService(
            @RequestBody BusinessServiceRequestVO businessServiceRequestVO) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        BusinessServiceVO response = settingsService.updateBusinessService(businessServiceRequestVO);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), BUSINESS_SERVICE));
    }

    @GetMapping("/business/service/list")
    public ResponseEntity<FlickzzDeskResponse> listBusinessServices() {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<BusinessServiceVO> response = settingsService.listBusinessServices();

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), BUSINESS_SERVICE),
                response);
    }

    @GetMapping("/business/service/{serviceId}")
    public ResponseEntity<FlickzzDeskResponse> getBusinessServiceInfo(@PathVariable String serviceId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        BusinessServiceVO response = settingsService.getBusinessServiceInfo(serviceId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), BUSINESS_SERVICE),
                response);
    }

    @DeleteMapping("/business/service/delete/{serviceId}")
    public ResponseEntity<FlickzzDeskResponse> deleteBusinessService(@PathVariable String serviceId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        settingsService.deleteBusinessService(serviceId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), BUSINESS_SERVICE));
    }

    @PostMapping("work/item/create")
    public ResponseEntity<FlickzzDeskResponse> createWorkItem(@RequestBody WorkItemRequestVO workItemRequestVO) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        WorkItemVO response = settingsService.createWorkItem(workItemRequestVO);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(CREATE_SUCCESS, getDescription(CREATE_SUCCESS.getDescription(), WORK_ITEM),
                response);
    }

    @DeleteMapping("work/item/delete")
    public ResponseEntity<FlickzzDeskResponse> deleteWorkItem(
            @RequestBody WorkItemDeleteRequestVO workItemDeleteRequestVO) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        settingsService.deleteWorkItem(workItemDeleteRequestVO);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(DELETE_SUCCESS, getDescription(DELETE_SUCCESS.getDescription(), WORK_ITEM));
    }

    @GetMapping("work/item/list/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> listWorkItems(@PathVariable String orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<WorkItemVO> response = settingsService.listWorkItems(orgId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), WORK_ITEM),
                response);
    }

    @GetMapping("field/type/list/{orgId}")
    public ResponseEntity<FlickzzDeskResponse> listFieldTypes(@PathVariable String orgId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));

        List<FieldTypeVO> response = settingsService.listFieldTypes(orgId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), FIELD_TYPE),
                response);
    }
}
