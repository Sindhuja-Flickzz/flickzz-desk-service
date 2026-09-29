package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "FD_RITM_APPROVER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RitmApprover {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ritm_approver_seq"
    )
    @SequenceGenerator(
            name = "ritm_approver_seq",
            sequenceName = "FD_RITM_APPROVER_SEQ",
            allocationSize = 1
    )
    @Column(name = "RITM_APPROVER_ID")
    private Long ritmApproverId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RITM_ID", nullable = false)
    private RitmMaster ritmId;

    @Column(name = "IS_GROUP_APPROVER", nullable = false)
    private Boolean isGroupApprover = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPROVER_CONFIG_ID")
    private RequestApproverConfig approverConfig;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPROVER_AGENT_ID", nullable = false)
    private AgentMaster approverAgent;

    @Column(name = "APPROVER_SEQUENCE")
    private Integer approverSequence;

    @Column(name = "IS_MAIN_APPROVER", nullable = false)
    private Boolean isMainApprover = false;

    @Column(name = "APPROVAL_STATUS", nullable = false, length = 30)
    private String approvalStatus = "Pending";

    @Column(name = "APPROVAL_REMARK", length = 2000)
    private String approvalRemark;

    @Column(name = "APPROVED_ON")
    private LocalDateTime approvedOn;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "CREATED_ON", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        this.createdOn = LocalDateTime.now();
        this.updatedOn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedOn = LocalDateTime.now();
    }
}