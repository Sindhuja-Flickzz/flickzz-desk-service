package com.flickzz.desk.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FD_CONFIG_CHANGE_NOTIFICATION")
public class    ConfigChangeNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fd_notification_seq")
    @SequenceGenerator(
            name = "fd_notification_seq",
            sequenceName = "FD_CONFIG_CHANGE_NOTIFICATION_SEQ",
            allocationSize = 1
    )
    @Column(name = "NOTIFICATION_ID")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHANGE_REQUEST_ID", referencedColumnName = "CCR_ID")
    private BPConfigurationChangeRequest changeRequest;

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
    private Boolean active = Boolean.TRUE;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "CREATED_ON", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;
}