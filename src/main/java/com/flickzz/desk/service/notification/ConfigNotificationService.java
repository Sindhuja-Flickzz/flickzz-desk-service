package com.flickzz.desk.service.notification;

import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.*;
import com.flickzz.desk.repo.CompanyApproverRepository;
import com.flickzz.desk.repo.ConfigChangeApprovalRepository;
import com.flickzz.desk.repo.ConfigChangeNotificationRepository;
import com.flickzz.desk.repo.UserRepository;
import com.flickzz.desk.service.CommonService;
import com.flickzz.desk.vo.ConfigChangeNotificationVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.flickzz.desk.config.FlickzzDeskConstants.PENDING;
import static com.flickzz.desk.config.FlickzzDeskConstants.UNREAD;

@Service
public class ConfigNotificationService {

    private static final Logger log = LoggerFactory.getLogger(ConfigNotificationService.class);
    @Autowired
    ConfigChangeNotificationRepository configChangeNotificationRepository;
    @Autowired
    ConfigChangeApprovalRepository configChangeApprovalRepository;
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

    @Transactional
    public void notifyRitmCreated(RitmMaster ritm, Long createdBy, AgentMaster openedBy,
                                  List<AgentMaster> recipients) {
        if (ritm == null || recipients == null || recipients.isEmpty()) return;
        for (AgentMaster recipient : recipients) {
            saveRitmNotification(ritm, recipient, createdBy, openedBy != null ? openedBy.getAgentName() : "System",
                    "New RITM created",
                    "A new RITM " + ritm.getRitmNumber() + " has been created and requires attention.",
                    "CREATE", false);
        }
    }

    private void saveRitmNotification(RitmMaster ritm, AgentMaster recipient, Long actorId, String actorName,
                                      String title, String message, String action, boolean publish) {
        if (recipient == null || recipient.getAgentId() == null) return;
        CompanyMaster company = ritm.getCompany();
        ConfigChangeNotification notification = ConfigChangeNotification.builder()
                .title(title)
                .message(message)
                .notificationType("RITM")
                .action(action)
                .referenceType("RITM")
                .referenceId(ritm.getRitmId())
                .triggeredByUser(actorName)
                .triggeredUserOrg(company != null ? company.getCompanyName() : null)
                .recipientUserId(recipient.getAgentId())
                .recipientUserName(recipient.getAgentName())
                .recipientOrgId(company != null ? company.getCompanyId() : 0L)
                .isRead(false)
                .createdBy(actorId)
                .createdOn(LocalDateTime.now())
                .build();
        ConfigChangeNotification saved = configChangeNotificationRepository.saveAndFlush(notification);
        if (publish) publishNotification(saved);
    }

    public void publishNotification(ConfigChangeNotification notification) {
        if (notification == null || notification.getRecipientUserId() == null) {
            return;
        }

        ConfigChangeNotificationVO notificationVO = mapper.toNotificationVO(notification);
        User user = userRepository.findById(notification.getRecipientUserId()).orElse(null);

        String userDest = user.getEmail();

        log.info("Publishing notification to user {} [id={}]: {}", userDest, notification.getRecipientUserId(), notificationVO);
        messagingTemplate.convertAndSendToUser(userDest, "/queue/notifications", notificationVO);
        log.info("Notification published to destination user {} [id={}]: {}", userDest, notification.getRecipientUserId(), notificationVO);
    }

    @Transactional
    public void notifyRitmUpdated(RitmMaster ritm, Long actorId) {
        if (ritm == null || actorId == null) return;
        boolean requestedForUpdated = ritm.getRequestedFor() != null
                && actorId.equals(ritm.getRequestedFor().getAgentId());
        AgentMaster recipient = requestedForUpdated ? ritm.getAssignedTo()
                : ritm.getAssignedTo() != null && actorId.equals(ritm.getAssignedTo().getAgentId())
                ? ritm.getRequestedFor() : null;
        if (recipient == null || recipient.getAgentId() == null || actorId.equals(recipient.getAgentId())) return;
        String actorName = requestedForUpdated
                ? ritm.getRequestedFor().getAgentName() : ritm.getAssignedTo().getAgentName();
        saveRitmNotification(ritm, recipient, actorId, actorName, "RITM updated",
                "RITM " + ritm.getRitmNumber() + " has been updated and requires your attention.",
                "UPDATE", false);
    }

    @Transactional
    public void notifyRitmAssigned(RitmMaster ritm, AgentMaster actor) {
        if (ritm == null || actor == null || ritm.getAssignedTo() == null) return;
        saveRitmNotification(ritm, ritm.getAssignedTo(), actor.getAgentId(), actor.getAgentName(),
                "RITM assigned", "RITM " + ritm.getRitmNumber() + " has been assigned to you.",
                "ASSIGN", false);
    }

    @Transactional
    public void notifyRitmApproverAssignment(RitmMaster ritm, Long actorId, Boolean isCreatorAdmin,
                                             List<AgentMaster> recipients, String action) {
        if (ritm == null || actorId == null || recipients == null || recipients.isEmpty()) return;
        String operation = switch (action) {
            case "CREATE" -> "created";
            case "UPDATE" -> "updated";
            case "DELETE" -> "removed";
            default -> "changed";
        };
        String title = "RITM " + action.toLowerCase(Locale.ROOT);
        String message = "RITM " + ritm.getRitmNumber() + " approver assignment was " + operation
                + " and requires your attention.";
        String actorName = commonService.loadUserNameByUserId(actorId, isCreatorAdmin);
        Set<Long> sent = new HashSet<>();
        for (AgentMaster recipient : recipients) {
            if (recipient == null || recipient.getAgentId() == null || !sent.add(recipient.getAgentId())) continue;
            saveRitmNotification(ritm, recipient, actorId, actorName, title, message, action, true);
        }
    }

    @Async
    @Transactional
    public void notifyConfigChange(BPConfigurationChangeRequest changeRequest, String changeType) {
        notifyForStage(changeRequest, changeType, NotificationStage.INTERNAL);
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
                ConfigChangeApproval approval = buildChangeApproval(changeRequest, approver, changeType, stage);
                configChangeApprovalRepository.saveAndFlush(approval);
                ConfigChangeNotification notification = buildNotification(changeRequest, approver, stage);
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
            return resolveApprovers(changeRequest.getRequestedByOrg());
        }

        if (changeRequest.getApprovalOrg() == null || changeRequest.getApprovalOrg().getCompanyId() == null) {
            return List.of();
        }
        return resolveApprovers(changeRequest.getApprovalOrg());
    }

    private ConfigChangeApproval buildChangeApproval(BPConfigurationChangeRequest changeRequest, CompanyApprover approver, String changeType, NotificationStage stage) {
        return ConfigChangeApproval.builder()
                .configChangeRequest(changeRequest)
                .approvalType(changeType)
                .approverLevel(approver.getLevel())
                .approverUserId(approver.getAgent().getUser().getUserId())
                .approverOrgId(approver.getCompany().getCompanyId())
                .status(PENDING)
                .approverType(stage == NotificationStage.INTERNAL ? "Internal" : "BP")
                .mandatory(approver.getLevel() == 1)
                .createdBy(changeRequest.getRequestedByUserId())
                .createdOn(LocalDateTime.now())
                .createdOn(LocalDateTime.now())
                .build();
    }

    private ConfigChangeNotification buildNotification(BPConfigurationChangeRequest changeRequest,
                                                       CompanyApprover approver,
                                                       NotificationStage stage) {
        return ConfigChangeNotification.builder()
                .changeRequest(changeRequest)
                .title(buildNotificationTitle(changeRequest))
                .message(buildNotificationMessage(changeRequest, stage))
                .notificationType(resolveConfigurationType(changeRequest))
                .action(changeRequest.getOperation())
                .triggeredByUser(changeRequest.getRequestedByUserId() != null ? commonService.loadUserNameByUserId(changeRequest.getRequestedByUserId(), changeRequest.getIsCreatorAdmin()) : null)
                .triggeredUserOrg(changeRequest.getRequestedByOrg() != null ? changeRequest.getRequestedByOrg().getCompanyName() : null)
                .referenceType(stage == NotificationStage.INTERNAL ? "Internal" : "BP")
                .referenceId(changeRequest.getChangedRequestId())
                .recipientUserId(approver.getAgent().getAgentId())
                .recipientUserName(resolveRecipientUserName(approver))
                .recipientOrgId(approver.getCompany().getCompanyId())
                .isRead(UNREAD)
                .createdBy(changeRequest.getRequestedByUserId())
                .createdOn(LocalDateTime.now())
                .build();
    }

    private List<CompanyApprover> resolveApprovers(CompanyMaster company) {
        if (company == null || company.getCompanyId() == null) {
            return List.of();
        }

        List<CompanyApprover> approvers = companyApproverRepository.findByCompany_CompanyIdAndIsActiveTrue(company.getCompanyId());
        if (approvers.isEmpty()) {
            return approvers;
        }

        if (Boolean.TRUE.equals(company.getEnforceApprovalHierarchy())) {
            Integer topLevel = approvers.stream()
                    .map(CompanyApprover::getLevel)
                    .filter(level -> level != null && level > 0)
                    .min(Integer::compareTo)
                    .orElse(null);
            if (topLevel == null) {
                return List.of();
            }
            return approvers.stream()
                    .filter(approver -> topLevel.equals(approver.getLevel()))
                    .toList();
        }

        return approvers;
    }

    private String buildNotificationTitle(BPConfigurationChangeRequest changeRequest) {

        String configurationType = resolveConfigurationType(changeRequest);
        String operation = normalizeOperation(changeRequest.getOperation());
        return configurationType + " " + operation;
    }

    private String buildNotificationMessage(BPConfigurationChangeRequest changeRequest, NotificationStage stage) {
        String configurationType = resolveConfigurationType(changeRequest);
        return "A " + configurationType + " configuration change request requires your review.";
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

    private String normalizeOperation(String operation) {
        if (operation == null || operation.isBlank()) {
            return "Update";
        }
        return operation.trim().toLowerCase(Locale.ROOT);
    }

    @Async
    @Transactional
    public void notifyBpApprovalConfigChange(BPConfigurationChangeRequest changeRequest, String changeType) {
        notifyForStage(changeRequest, changeType, NotificationStage.BP_APPROVAL);
    }

    private enum NotificationStage {
        INTERNAL,
        BP_APPROVAL
    }
}