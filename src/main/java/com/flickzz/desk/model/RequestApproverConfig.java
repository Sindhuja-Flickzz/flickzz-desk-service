package com.flickzz.desk.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "FD_REQUEST_APPROVER_CONFIG",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UQ_APPROVER_CODE_COMPANY",
                        columnNames = {
                                "APPROVER_CODE",
                                "COMPANY_ID"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestApproverConfig {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "request_approver_config_seq"
    )
    @SequenceGenerator(
            name = "request_approver_config_seq",
            sequenceName = "REQUEST_APPROVER_CONFIG_SEQ",
            allocationSize = 1
    )
    @Column(name = "APPROVER_CONFIG_ID")
    private Long approverConfigId;

    @Column(
            name = "APPROVER_CODE",
            nullable = false,
            length = 50
    )
    private String approverCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    private CompanyMaster company;

    @Column(name = "FOLLOW_SEQUENCE")
    private Boolean followSequence = false;

    @Column(name = "IS_ANY_APPROVAL_SUFFICIENT")
    private Boolean isAnyApprovalSufficient = false;

    @OneToMany(mappedBy = "approverConfig", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<RequestApprover> approvers;

    @Builder.Default
    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "IS_CREATOR_ADMIN", nullable = false)
    private Boolean isCreatorAdmin;

    @Column(name = "IS_UPDATER_ADMIN")
    private Boolean isUpdaterAdmin = false;

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