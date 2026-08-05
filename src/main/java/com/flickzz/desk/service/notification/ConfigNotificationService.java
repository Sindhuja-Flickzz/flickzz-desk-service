package com.flickzz.desk.service.notification;

import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.BPConfigurationChangeRequest;
import com.flickzz.desk.model.CompanyApprover;
import com.flickzz.desk.model.ConfigChangeNotification;
import com.flickzz.desk.model.User;
import com.flickzz.desk.repo.CompanyApproverRepository;
import com.flickzz.desk.repo.ConfigChangeNotificationRepository;
import com.flickzz.desk.repo.UserRepository;
import com.flickzz.desk.service.CommonService;
import com.flickzz.desk.vo.ConfigChangeNotificationVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import static com.flickzz.desk.config.FlickzzDeskConstants.UNREAD;

@Service
public class ConfigNotificationService {

    private static final Logger log = LoggerFactory.getLogger(ConfigNotificationService.class);

    private enum NotificationStage {
        INTERNAL,
        BP_APPROVAL
    }

    @Autowired
    ConfigChangeNotificationRepository configChangeNotificationRepository;

    @Autowired
    CompanyApproverRepository companyApproverRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommonService commonService;

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @Autowired
    private CommonMapper mapper;

    @Autowired
    private SimpUserRegistry simpUserRegistry;

    @Async
    @Transactional
    public void notifyConfigChange(BPConfigurationChangeRequest changeRequest, String changeType) {
        notifyForStage(changeRequest, changeType, NotificationStage.INTERNAL);
    }

    @Async
    @Transactional
    public void notifyBpApprovalConfigChange(BPConfigurationChangeRequest changeRequest, String changeType) {
        notifyForStage(changeRequest, changeType, NotificationStage.BP_APPROVAL);
    }

    private void notifyForStage(BPConfigurationChangeRequest changeRequest, String changeType, NotificationStage stage) {
        try {
            if (changeRequest == null) {
                log.info("Change request is null, skipping notification");
                return;
            }

            List<CompanyApprover> approvers = resolveApprovers(changeRequest, stage);
            if (approvers == null || approvers.isEmpty()) {
                log.info("No approvers found for {} notification", stage.name().toLowerCase(Locale.ROOT));
                return;
            }

            for (CompanyApprover approver : approvers) {
                ConfigChangeNotification notification = buildNotification(changeRequest, approver, changeType, stage);
                ConfigChangeNotification savedNotification = configChangeNotificationRepository.saveAndFlush(notification);
                publishNotification(savedNotification);
            }
        } catch (Exception e) {
            log.error("Error notifying configuration change for stage {}", stage, e);
        }
    }

    private List<CompanyApprover> resolveApprovers(BPConfigurationChangeRequest changeRequest, NotificationStage stage) {
        if (changeRequest == null) {
            return List.of();
        }

        if (stage == NotificationStage.INTERNAL) {
            if (changeRequest.getRequestedByOrg() == null || changeRequest.getRequestedByOrg().getCompanyId() == null) {
                return List.of();
            }
            return companyApproverRepository.findByCompany_CompanyIdAndIsActiveTrue(changeRequest.getRequestedByOrg().getCompanyId());
        }

        if (changeRequest.getApprovalOrg() == null || changeRequest.getApprovalOrg().getCompanyId() == null) {
            return List.of();
        }
        return companyApproverRepository.findByCompany_CompanyIdAndIsActiveTrue(changeRequest.getApprovalOrg().getCompanyId());
    }

    private ConfigChangeNotification buildNotification(BPConfigurationChangeRequest changeRequest,
                                                      CompanyApprover approver,
                                                      String changeType,
                                                      NotificationStage stage) {
        return ConfigChangeNotification.builder()
                .changeRequest(changeRequest)
                .title(buildNotificationTitle(changeRequest, stage))
                .message(buildNotificationMessage(changeRequest, stage))
                .notificationType(changeType)
                .referenceType(changeRequest.getOperation())
                .referenceId(changeRequest.getChangedRequestId())
                .recipientUserId(approver.getAgent().getAgentId())
                .recipientUserName(resolveRecipientUserName(approver))
                .recipientOrgId(approver.getCompany().getCompanyId())
                .isRead(UNREAD)
                .createdBy(changeRequest.getRequestedByUserId())
                .createdOn(LocalDateTime.now())
                .build();
    }

    private String buildNotificationTitle(BPConfigurationChangeRequest changeRequest, NotificationStage stage) {
        String stageLabel = stage == NotificationStage.INTERNAL ? "Internal Approval" : "BP Approval";
        String configurationType = resolveConfigurationType(changeRequest);
        String operation = normalizeOperation(changeRequest.getOperation());
        return stageLabel + " - " + configurationType + " " + operation;
    }

    private String buildNotificationMessage(BPConfigurationChangeRequest changeRequest, NotificationStage stage) {
        String stageLabel = stage == NotificationStage.INTERNAL ? "internal approval" : "BP approval";
        String configurationType = resolveConfigurationType(changeRequest);
        String operation = normalizeOperation(changeRequest.getOperation());
        return "A " + configurationType + " configuration change request (" + operation + ") requires your " + stageLabel + " review.";
    }

    private String resolveConfigurationType(BPConfigurationChangeRequest changeRequest) {
        if (Boolean.TRUE.equals(changeRequest.getBpPriority())) {
            return "Priority";
        }
        if (Boolean.TRUE.equals(changeRequest.getBpSla())) {
            return "SLA";
        }
        if (Boolean.TRUE.equals(changeRequest.getCategory())) {
            return "Category";
        }
        if (Boolean.TRUE.equals(changeRequest.getSupportGroup())) {
            return "Support Group";
        }
        if (Boolean.TRUE.equals(changeRequest.getAssignment())) {
            return "Assignment";
        }
        return "Configuration";
    }

    private String normalizeOperation(String operation) {
        if (operation == null || operation.isBlank()) {
            return "Update";
        }
        return operation.trim().toLowerCase(Locale.ROOT);
    }

    private String resolveRecipientUserName(CompanyApprover approver) {
        if (approver == null || approver.getAgent() == null || approver.getAgent().getAgentId() == null) {
            return null;
        }

        try {
            return commonService.loadUserNameByUserId(approver.getAgent().getAgentId(), Boolean.FALSE);
        } catch (Exception e) {
            log.warn("Unable to resolve recipient user name for agent {}", approver.getAgent().getAgentId(), e);
            return null;
        }
    }

    public void publishNotification(ConfigChangeNotification notification) {
        if (notification == null || notification.getRecipientUserId() == null) {
            return;
        }

        ConfigChangeNotificationVO notificationVO = mapper.toNotificationVO(notification);
//        String userDest = notification.getRecipientUserName() != null && !notification.getRecipientUserName().isBlank()
//                ? notification.getRecipientUserName()
//                : String.valueOf(notification.getRecipientUserId());
        User user = userRepository.findById(notification.getRecipientUserId()).orElse(null);

        String userDest = user.getEmail();

        log.info("Publishing notification to user {} [id={}]: {}", userDest, notification.getRecipientUserId(), notificationVO);
        messagingTemplate.convertAndSendToUser(userDest, "/queue/notifications", notificationVO);
        log.info("Notification published to destination user {} [id={}]: {}", userDest, notification.getRecipientUserId(), notificationVO);
    }
}