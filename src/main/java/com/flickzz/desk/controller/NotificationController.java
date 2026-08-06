package com.flickzz.desk.controller;

import com.flickzz.desk.config.FlickzzDeskResponse;
import com.flickzz.desk.service.NotificationService;
import com.flickzz.desk.vo.ConfigChangeNotificationVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskResponseHandler.handleSuccessResponse;
import static com.flickzz.desk.config.FlickzzDeskSuccessCodes.FETCH_SUCCESS;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;

@CrossOrigin
@RestController
@RequestMapping("/notification")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    NotificationService notificationService;

    @GetMapping("/list/{recipientId}")
    public ResponseEntity<FlickzzDeskResponse> getNotificationsByRecipientId(@PathVariable Long recipientId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        List<ConfigChangeNotificationVO> response = notificationService.getNotificationsByRecipientId(recipientId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), NOTIFICATION), response);
    }

    @PutMapping("/read/{notificationId}")
    public ResponseEntity<FlickzzDeskResponse> markNotificationAsRead(@PathVariable Long notificationId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        notificationService.markNotificationAsRead(notificationId);

        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), NOTIFICATION), null);
    }

    @PutMapping("/read/all/{recipientId}")
    public ResponseEntity<FlickzzDeskResponse> markAllNotificationsAsRead(@PathVariable Long recipientId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        notificationService.markAllNotificationsAsRead(recipientId);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), NOTIFICATION), null);
    }

    @DeleteMapping("/clear/{notificationId}")
    public ResponseEntity<FlickzzDeskResponse> clearNotification(@PathVariable Long notificationId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        notificationService.clearNotification(notificationId);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), NOTIFICATION), null);
    }

    @DeleteMapping("/clear/all/{recipientId}")
    public ResponseEntity<FlickzzDeskResponse> clearAllNotifications(@PathVariable Long recipientId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        notificationService.clearAllNotifications(recipientId);
        log.info(generateLog(EXIT, this.getClass().getName()));
        return handleSuccessResponse(FETCH_SUCCESS, getDescription(FETCH_SUCCESS.getDescription(), NOTIFICATION), null);
    }
}
