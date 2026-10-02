package com.flickzz.desk.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "FD_STATUS_MASTER",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UQ_STATUS_MASTER_CODE",
                        columnNames = {
                                "COMPANY_ID",
                                "WORK_ITEM_ID",
                                "STATUS_CODE"
                        }
                ),
                @UniqueConstraint(
                        name = "UQ_STATUS_MASTER_SEQUENCE",
                        columnNames = {
                                "COMPANY_ID",
                                "WORK_ITEM_ID",
                                "SEQUENCE_NO"
                        }
                )
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusMaster {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "statusMasterSeq"
    )
    @SequenceGenerator(
            name = "statusMasterSeq",
            sequenceName = "FD_STATUS_SEQ",
            allocationSize = 1
    )
    @Column(name = "STATUS_ID")
    private Long statusId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    private CompanyMaster company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORK_ITEM_ID", nullable = false)
    private WorkItem workItem;

    @Column(name = "STATUS_CODE", nullable = false, length = 50)
    private String statusCode;

    @Column(name = "SEQUENCE_NO", nullable = false)
    private Integer sequenceNo;

    @Column(name = "STATUS_COLOR", length = 7)
    private String statusColor;

    @OneToMany(mappedBy = "currentStatus", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<StatusVisibility> visibility;

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