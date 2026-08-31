package com.flickzz.desk.controller;

import com.flickzz.desk.config.FlickzzDeskResponse;
import com.flickzz.desk.service.RitmService;
import com.flickzz.desk.vo.RitmMasterVO;
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
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.CREATE_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.FETCH_SUCCESS;
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
}
