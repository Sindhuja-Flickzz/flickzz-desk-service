package com.flickzz.desk.model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_CONFIG_CHANGE_REQUEST")
public class BPConfigurationChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fd_change_request_seq")
    @SequenceGenerator(
            name = "fd_change_request_seq",
            sequenceName = "FD_CHANGE_REQUEST_SEQ",
            allocationSize = 1
    )
    @Column(name = "CCR_ID")
    private Long ccrId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONFIGURATION_ID", nullable = false)
    private BPConfiguration configuration;

    @OneToMany(mappedBy = "configurationChangeRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<BPConfigurationChangeRequestRemark> remarks;

    @Column(name = "CHANGED_REQUEST_ID", nullable = false)
    private Long changedRequestId;

    @Column(name = "SOURCE_CHANGE_ID")
    private Long sourceChangeId;

    @Column(name = "IS_BP_PRIORITY")
    private Boolean bpPriority = Boolean.FALSE;

    @Column(name = "IS_BP_SLA")
    private Boolean bpSla = Boolean.FALSE;

    @Column(name = "IS_CATEGORY")
    private Boolean category = Boolean.FALSE;

    @Column(name = "IS_SUPPORT_GROUP")
    private Boolean supportGroup = Boolean.FALSE;

    @Column(name = "IS_ASSIGNMENT")
    private Boolean assignment = Boolean.FALSE;

    @Column(name = "OPERATION", nullable = false, length = 20)
    private String operation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUESTED_BY_ORG", nullable = false)
    private CompanyMaster requestedByOrg;

    @Column(name = "REQUESTED_BY_USER_ID", nullable = false)
    private Long requestedByUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPROVAL_ORG", nullable = false)
    private CompanyMaster approvalOrg;

    @Column(name = "STATUS", nullable = false, length = 30)
    private String status;

    @Column(name = "TOTAL_INTERNAL_APPROVAL_LEVELS", nullable = false)
    private Integer totalInternalApprovalLevels;

    @Column(name = "TOTAL_BP_APPROVAL_LEVELS", nullable = false)
    private Integer totalBpApprovalLevels;

    @Column(name = "CURRENT_INTERNAL_APPROVAL_LEVEL")
    private Integer currentInternalApprovalLevel= 0;

    @Column(name = "CURRENT_BP_APPROVAL_LEVEL")
    private Integer currentBpApprovalLevel = 0;

    @Column(name = "REQUESTED_ON", nullable = false)
    private LocalDateTime requestedOn;

    @Column(name = "COMPLETED_ON")
    private LocalDateTime completedOn;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "CREATED_ON", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;

    @Column(name = "IS_CREATOR_ADMIN")
    private Boolean isCreatorAdmin;
}