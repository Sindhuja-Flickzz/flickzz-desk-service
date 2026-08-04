package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_CONFIG_CHANGE_REQUEST_REMARK")
public class BPConfigurationChangeRequestRemark {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fd_change_request_remark_seq")
    @SequenceGenerator(
            name = "fd_change_request_remark_seq",
            sequenceName = "FD_CHANGE_REQUEST_REMARK_SEQ",
            allocationSize = 1
    )
    @Column(name = "REMARK_ID")
    private Long remarkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CCR_ID", nullable = false)
    private BPConfigurationChangeRequest configurationChangeRequest;

    @Column(name = "REMARK_TYPE", nullable = false, length = 30)
    private String remarkType;

    @Column(name = "APPROVER_LEVEL")
    private Integer approverLevel;

    @Column(name = "APPROVAL_STATUS", nullable = false, length = 30)
    private String approvalStatus;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "ORGANIZATION_ID", nullable = false)
    private Long organizationId;

    @Column(name = "REMARK", nullable = false, columnDefinition = "TEXT")
    private String remark;

    @Column(name = "CREATED_ON", nullable = false)
    private LocalDateTime createdOn;
}