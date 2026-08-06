package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_CONFIG_CHANGE_APPROVAL", uniqueConstraints = {
        @UniqueConstraint(name = "UK_CCA_APPROVER", columnNames = {"CCR_ID", "APPROVAL_TYPE", "APPROVER_LEVEL", "APPROVER_USER_ID"})})
public class ConfigChangeApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fd_config_change_approval_seq")
    @SequenceGenerator(
            name = "fd_config_change_approval_seq",
            sequenceName = "FD_CONFIG_CHANGE_APPROVAL_SEQ",
            allocationSize = 1)
    @Column(name = "APPROVAL_ID")
    private Long approvalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CCR_ID", nullable = false)
    private BPConfigurationChangeRequest configChangeRequest;

    @Column(name = "APPROVAL_TYPE", nullable = false, length = 20)
    private String approvalType;

    @Column(name = "APPROVER_LEVEL", nullable = false)
    private Integer approverLevel;

    @Column(name = "APPROVER_USER_ID", nullable = false)
    private Long approverUserId;

    @Column(name = "APPROVER_ORG_ID", nullable = false)
    private Long approverOrgId;

    @Column(name = "STATUS", nullable = false, length = 30)
    private String status;

    @Column(name = "IS_MANDATORY")
    private Boolean mandatory;

    @Column(name = "APPROVED_ON")
    private LocalDateTime approvedOn;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "REMARK_ID")
    private List<BPConfigurationChangeRequestRemark> remark;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "CREATED_ON", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;
}
