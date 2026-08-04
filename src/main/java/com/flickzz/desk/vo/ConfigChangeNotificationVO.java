package com.flickzz.desk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigChangeNotificationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long notificationId;

    private BPConfigurationChangeRequestVO changeRequestId;

    private String title;

    private String message;

    private String notificationType;

    private String referenceType;

    private Long referenceId;

    private Long recipientUserId;

    private Long recipientOrgId;

    private String actionUrl;

    private Boolean isRead;

    private LocalDateTime readOn;

    private Boolean active;

    private Long createdBy;

    private LocalDateTime createdOn;

    private Long updatedBy;

    private LocalDateTime updatedOn;
}
