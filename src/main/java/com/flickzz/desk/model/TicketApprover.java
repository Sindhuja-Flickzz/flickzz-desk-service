package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "FD_TICKET_APPROVER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketApprover {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ticket_approver_seq"
    )
    @SequenceGenerator(
            name = "ticket_approver_seq",
            sequenceName = "FD_TICKET_APPROVER_SEQ",
            allocationSize = 1
    )
    @Column(name = "TICKET_APPROVER_ID")
    private Long ticketApproverId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TICKET_ID", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TicketMaster ticket;

    @Column(name = "IS_GROUP_APPROVER", nullable = false)
    private Boolean isGroupApprover = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPROVER_CONFIG_ID")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private RequestApproverConfig approverConfig;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPROVER_AGENT_ID", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AgentMaster approverAgent;

    @Column(name = "APPROVER_SEQUENCE")
    private Integer approverSequence;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ticketApprover")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<TicketApproverRemark> remark;

    @Column(name = "IS_MAIN_APPROVER", nullable = false)
    private Boolean isMainApprover = false;

    @Column(name = "APPROVAL_STATUS", nullable = false, length = 30)
    private String approvalStatus = "Pending";

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