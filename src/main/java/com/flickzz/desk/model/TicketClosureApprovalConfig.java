package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "FD_TICKET_CLOSURE_APPROVAL_CONFIG")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketClosureApprovalConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticketClosureApprovalConfigSeq")
    @SequenceGenerator(name = "ticketClosureApprovalConfigSeq",
            sequenceName = "FD_TICKET_CLOSE_APPR_CONFIG_SEQ", allocationSize = 1)
    @Column(name = "CONFIG_ID")
    private Long configId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    private CompanyMaster company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORK_ITEM_ID", nullable = false)
    private WorkItem workItem;

    @Column(name = "IS_APPROVAL_REQUIRED", nullable = false)
    private Boolean isApprovalRequired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPROVAL_PENDING_STATUS_ID", nullable = false)
    private StatusMaster approvalPendingStatus;

    @Column(name = "AUTO_ACTION_ENABLED", nullable = false)
    private Boolean autoActionEnabled;

    @Column(name = "AUTO_ACTION_AFTER")
    private Integer autoActionAfter;

    @Column(name = "AUTO_ACTION_UNIT", length = 10)
    private String autoActionUnit;

    @Column(name = "AUTO_ACTION", length = 10)
    private String autoAction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AUTO_APPROVE_STATUS_ID")
    private StatusMaster autoApproveStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AUTO_DECLINE_STATUS_ID")
    private StatusMaster autoDeclineStatus;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "IS_CREATOR_ADMIN", nullable = false)
    private Boolean isCreatorAdmin;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "IS_UPDATER_ADMIN", nullable = false)
    private Boolean isUpdaterAdmin;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
    }
}
