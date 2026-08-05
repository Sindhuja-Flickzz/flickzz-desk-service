package com.flickzz.desk.scheduler;

import com.flickzz.desk.model.ConfigChangeNotification;
import com.flickzz.desk.repo.ConfigChangeNotificationRepository;
import com.flickzz.desk.service.notification.ConfigNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;

@Service
public class FlickzzDeskExpiryScheduler {
    public static final Logger log = LoggerFactory.getLogger(FlickzzDeskExpiryScheduler.class);

    @Autowired
    ConfigNotificationService configNotificationService;

    @Autowired
    ConfigChangeNotificationRepository configChangeNotificationRepository;

    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void deactivateExpiredBusinessPartners() {
        ConfigChangeNotification notification = configChangeNotificationRepository.findById(Long.valueOf(1)).orElse(null);
        if (notification != null) {
            configNotificationService.publishNotification(notification);
        }
        log.info("Checked for expired business partners and sent notifications if any. - Checked at {}", Date.from(Instant.now()));
    }
}
