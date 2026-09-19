package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "FD_RITM_STATUS",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UQ_COMPANY_STATUS_CODE",
                        columnNames = {"COMPANY_ID", "STATUS_CODE"}
                ),
                @UniqueConstraint(
                        name = "UQ_ORG_RITM_STATUS_SEQUENCE",
                        columnNames = {"COMPANY_ID", "SEQUENCE_NO"}
                )
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RitmStatus {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "fd_status_seq_generator"
    )
    @SequenceGenerator(
            name = "fd_status_seq_generator",
            sequenceName = "FD_STATUS_SEQ",
            allocationSize = 1
    )
    @Column(name = "STATUS_ID")
    private Long statusId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    private CompanyMaster company;

    @Column(name = "STATUS_CODE", nullable = false, length = 50)
    private String statusCode;

    @Column(name = "SEQUENCE_NO", nullable = false)
    private Integer sequenceNo;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive = true;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "IS_CREATOR_ADMIN", nullable = false)
    private Boolean isCreatorAdmin = false;

    @Column(name = "IS_UPDATER_ADMIN")
    private Boolean isUpdaterAdmin = false;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (isActive == null) {
            isActive = true;
        }

        if (isCreatorAdmin == null) {
            isCreatorAdmin = false;
        }

        if (isUpdaterAdmin == null) {
            isUpdaterAdmin = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}