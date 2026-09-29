package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FD_NOTIFICATION")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FD_NOTIFICATION_SEQ")
    @SequenceGenerator(
            name = "FD_NOTIFICATION_SEQ",
            sequenceName = "FD_NOTIFICATION_SEQ",
            allocationSize = 1
    )
    @Column(name = "NOTIFICATION_ID")
    private Long notificationId;

    @Column(name = "REQUEST_ID")
    private Long requestId;

    @Column(name = "REQUEST_TYPE", length = 20)
    private String requestType;

    @Column(name = "TITLE", nullable = false, length = 200)
    private String title;

    @Column(name = "MESSAGE", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "NOTIFICATION_TYPE", nullable = false, length = 50)
    private String notificationType;

    @Column(name = "NOTIFICATION_ACTION", length = 20)
    private String action;

    @Column(name = "REFERENCE_TYPE", length = 50)
    private String referenceType;

    @Column(name = "REFERENCE_ID")
    private Long referenceId;

    @Column(name = "TRIGGERED_BY_USER", length = 100)
    private String triggeredByUser;

    @Column(name = "TRIGGERED_USER_ORG", length = 100)
    private String triggeredUserOrg;

    @Column(name = "RECIPIENT_USER_ID", nullable = false)
    private Long recipientUserId;

    @Column(name = "RECIPIENT_USER_NAME", length = 100)
    private String recipientUserName;

    @Column(name = "RECIPIENT_ORG_ID", nullable = false)
    private Long recipientOrgId;

    @Column(name = "IS_READ")
    private Boolean isRead = Boolean.FALSE;

    @Column(name = "READ_ON")
    private LocalDateTime readOn;

    @Builder.Default
    @Column(name = "IS_ACTIVE")
    private Boolean isActive = Boolean.TRUE;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "CREATED_ON", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;
}