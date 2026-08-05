package com.flickzz.desk.service;

import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.ConfigChangeNotification;
import com.flickzz.desk.repo.ConfigChangeNotificationRepository;
import com.flickzz.desk.vo.ConfigChangeNotificationVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.flickzz.desk.config.FlickzzDeskConstants.ENTRY;
import static com.flickzz.desk.config.FlickzzDeskConstants.READ;
import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    ConfigChangeNotificationRepository configChangeNotificationRepository;

    @Autowired
    private CommonMapper mapper;

    public List<ConfigChangeNotificationVO> getNotificationsByRecipientId(Long recipientId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            List<ConfigChangeNotification> notifications = configChangeNotificationRepository.findAllByRecipientUserIdOrderByNotificationIdDesc(recipientId);
            if (notifications == null) {
                return List.of();
            }
            return notifications.stream().map(mapper :: toNotificationVO).toList();
        } catch (Exception e) {
            log.error(generateLog(ENTRY, this.getClass().getName()), e);
            throw e;
        }
    }

    public void markNotificationAsRead(Long notificationId) {
        log.info(generateLog(ENTRY, this.getClass().getName()));
        try {
            if(notificationId != null) {
                ConfigChangeNotification notification = configChangeNotificationRepository.findById(notificationId).orElse(null);
                if (notification != null) {
                    notification.setIsRead(READ);
                    notification.setReadOn(LocalDateTime.now());
                    notification.setUpdatedBy(notification.getRecipientOrgId());
                    notification.setUpdatedOn(LocalDateTime.now());
                    configChangeNotificationRepository.save(notification);
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
            List<ConfigChangeNotification> notifications = configChangeNotificationRepository.findAllByRecipientUserIdOrderByNotificationIdDesc(recipientId);
            for (ConfigChangeNotification notification : notifications) {
                notification.setIsRead(READ);
                notification.setReadOn(LocalDateTime.now());
                notification.setUpdatedBy(notification.getRecipientOrgId());
                notification.setUpdatedOn(LocalDateTime.now());
            }
            configChangeNotificationRepository.saveAll(notifications);
        } catch (Exception e) {
            log.error(generateLog(ENTRY, this.getClass().getName()), e);
            throw e;
        }
    }
}
