package com.flickzz.desk.service.notification;

import com.flickzz.desk.model.BPConfigurationChangeRequest;
import com.flickzz.desk.model.CompanyApprover;
import com.flickzz.desk.model.ConfigChangeNotification;
import com.flickzz.desk.repo.CompanyApproverRepository;
import com.flickzz.desk.repo.ConfigChangeNotificationRepository;
import com.flickzz.desk.service.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;

@Service
public class ConfigNotificationService {

    private static final Logger log = LoggerFactory.getLogger(ConfigNotificationService.class);
    
    @Autowired
    ConfigChangeNotificationRepository configChangeNotificationRepository;

    @Autowired
    CompanyApproverRepository companyApproverRepository;

    @Autowired
    CommonService commonService;

    @Async
    public void notifyConfigChange(BPConfigurationChangeRequest changeRequest, String changeType) {
        try {
            if (changeRequest == null) {
                log.info("Change request is null, skipping notification");
                return;
            }

             ConfigChangeNotification notification = ConfigChangeNotification.builder()
                    .changeRequest(changeRequest)
                    .title("")
                    .message("")
                    .notificationType(changeType)
                    .referenceType(changeRequest.getOperation())
                    .referenceId(changeRequest.getChangedRequestId())
                    .isRead(UNREAD)
                    .createdBy(changeRequest.getRequestedByUserId())
                    .createdOn(LocalDateTime.now())
                    .build();

            List<CompanyApprover> companyApprover = companyApproverRepository.findByCompany_CompanyIdAndIsActiveTrue(changeRequest.getRequestedByOrg());
            companyApprover.stream().forEach(approver -> {
                notification.setRecipientUserId(approver.getAgent().getAgentId());
                notification.setRecipientUserName(commonService.loadUserNameByUserId(approver.getAgent().getAgentId()));
                notification.setRecipientOrgId(approver.getCompany().getCompanyId());
                configChangeNotificationRepository.save(notification);
            });

            configChangeNotificationRepository.save(notification);
        } catch (Exception e) {
            log.error("Error notifying configuration change", e);
        }
    }
}
