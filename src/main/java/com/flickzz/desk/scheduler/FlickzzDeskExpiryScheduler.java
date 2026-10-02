package com.flickzz.desk.scheduler;

import com.flickzz.desk.repo.NotificationRepository;
import com.flickzz.desk.service.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlickzzDeskExpiryScheduler {
    public static final Logger log = LoggerFactory.getLogger(FlickzzDeskExpiryScheduler.class);

    @Autowired
    NotificationService notificationService;

    @Autowired
    NotificationRepository notificationRepository;

//    @Scheduled(cron = "0/15 * * * * ?")
//    @Transactional
//    public void deactivateExpiredBusinessPartners() {
//        ConfigChangeNotification notification = configChangeNotificationRepository.findById(Long.valueOf(8)).orElse(null);
//        if (notification != null) {
//            configNotificationService.publishNotification(notification);
//        }
//        log.info("Checked for expired business partners and sent notifications if any. - Checked at {}", Date.from(Instant.now()));
//    }
}
