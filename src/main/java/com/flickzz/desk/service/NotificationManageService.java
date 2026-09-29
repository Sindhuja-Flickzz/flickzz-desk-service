package com.flickzz.desk.service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.Notification;
import com.flickzz.desk.repo.NotificationRepository;
import com.flickzz.desk.vo.NotificationVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.UNREAD_ERROR;

@Service
public class NotificationManageService {

    private static final Logger log = LoggerFactory.getLogger(NotificationManageService.class);

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    private CommonMapper mapper;

    public List<NotificationVO> getNotificationsByRecipientId(Long recipientId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            List<Notification> notifications = notificationRepository.findAllByRecipientUserIdAndIsActiveTrueOrderByNotificationIdDesc(recipientId);
            if (notifications == null) {
                return List.of();
            }
            return notifications.stream().map(mapper::toNotificationVO).toList();
        } catch (Exception e) {
            log.error(generateLog(ENTRY, this.getClass().getName()), e);
            throw e;
        }
    }

    public void markNotificationAsRead(Long notificationId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (notificationId != null) {
                Notification notification = notificationRepository.findById(notificationId).orElse(null);
                if (notification != null) {
                    notification.setIsRead(READ);
                    notification.setReadOn(LocalDateTime.now());
                    notification.setUpdatedBy(notification.getRecipientOrgId());
                    notification.setUpdatedOn(LocalDateTime.now());
                    notificationRepository.save(notification);
                }
            }
        } catch (Exception e) {
            log.error(generateLog(ENTRY, this.getClass().getName()), e);
            throw e;
        }
    }

    public void markAllNotificationsAsRead(Long recipientId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            List<Notification> notifications = notificationRepository.findAllByRecipientUserIdAndIsActiveTrueOrderByNotificationIdDesc(recipientId);
            for (Notification notification : notifications) {
                notification.setIsRead(READ);
                notification.setReadOn(LocalDateTime.now());
                notification.setUpdatedBy(notification.getRecipientOrgId());
                notification.setUpdatedOn(LocalDateTime.now());
            }
            notificationRepository.saveAll(notifications);
        } catch (Exception e) {
            log.error(generateLog(ENTRY, this.getClass().getName()), e);
            throw e;
        }
    }

    public void clearNotification(Long notificationId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if (notificationId != null) {
                Notification notification = notificationRepository.findById(notificationId).orElse(null);
                if (notification != null) {
                    if (notification.getIsRead()) {
                        notification.setIsActive(INACTIVE);
                        notification.setUpdatedBy(notification.getRecipientOrgId());
                        notification.setUpdatedOn(LocalDateTime.now());
                        notificationRepository.save(notification);
                    } else {
                        throw new FlickzzDeskException(UNREAD_ERROR, UNREAD_ERROR.getDescription());
                    }
                }
            }
        } catch (Exception e) {
            log.error(generateLog(ENTRY, this.getClass().getName()), e);
            throw e;
        }
    }

    public void clearAllNotifications(Long recipientId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            List<Notification> notifications = notificationRepository.findAllByRecipientUserIdAndIsActiveTrueAndIsReadTrueOrderByNotificationIdDesc(recipientId);
            for (Notification notification : notifications) {
                notification.setIsActive(INACTIVE);
                notification.setUpdatedBy(notification.getRecipientOrgId());
                notification.setUpdatedOn(LocalDateTime.now());
            }
            notificationRepository.saveAll(notifications);
        } catch (Exception e) {
            log.error(generateLog(ENTRY, this.getClass().getName()), e);
            throw e;
        }
    }
}
