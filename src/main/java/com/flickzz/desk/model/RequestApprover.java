package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "FD_REQUEST_APPROVER",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UQ_APPROVER_CONFIG_AGENT",
                        columnNames = {
                                "APPROVER_CONFIG_ID",
                                "AGENT_ID"
                        }
                ),
                @UniqueConstraint(
                        name = "UQ_APPROVER_CONFIG_SEQUENCE",
                        columnNames = {
                                "APPROVER_CONFIG_ID",
                                "APPROVER_SEQUENCE"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestApprover {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "request_approver_seq"
    )
    @SequenceGenerator(
            name = "request_approver_seq",
            sequenceName = "REQUEST_APPROVER_SEQ",
            allocationSize = 1
    )
    @Column(name = "APPROVER_ID")
    private Long approverId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPROVER_CONFIG_ID", nullable = false)
    private RequestApproverConfig approverConfig;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AGENT_ID", nullable = false)
    private AgentMaster agent;

    @Column(
            name = "APPROVER_SEQUENCE",
            nullable = false
    )
    private Integer approverSequence;

    @Builder.Default
    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}