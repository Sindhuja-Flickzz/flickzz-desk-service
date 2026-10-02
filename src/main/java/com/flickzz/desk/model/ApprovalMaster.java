package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_APPROVAL_MASTER")
public class ApprovalMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FD_APPROVAL_SEQ")
    @SequenceGenerator(
            name = "FD_APPROVAL_SEQ",
            sequenceName = "FD_APPROVAL_SEQ",
            allocationSize = 1)
    @Column(name = "APPROVAL_ID")
    private Long approvalId;

    @Column(name = "REQUEST_ID", nullable = false)
    private Long requestId;

    @Column(name = "REQUEST_TYPE", nullable = false, length = 20)
    private String requestType;

    @Column(name = "APPROVAL_TYPE", nullable = false, length = 20)
    private String approvalType;

    @Column(name = "DESCRIPTION", length = 200)
    private String description;

    @Column(name = "APPROVER_TYPE", nullable = false, length = 20)
    private String approverType;

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

    @Builder.Default
    @Column(name = "IS_ACTIVE")
    private Boolean active = true;

    @Column(name = "APPROVED_ON")
    private LocalDateTime approvedOn;

//    @OneToMany(fetch = FetchType.LAZY)
//    @JoinColumn(name = "REMARK_ID")
//    private List<BPConfigurationChangeRequestRemark> remark;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "CREATED_ON", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;
}
