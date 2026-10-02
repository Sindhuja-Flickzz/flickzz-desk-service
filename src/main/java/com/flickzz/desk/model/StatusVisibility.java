package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "FD_STATUS_VISIBILITY",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_STATUS_VISIBILITY",
                        columnNames = {
                                "COMPANY_ID",
                                "WORK_ITEM_ID",
                                "CURRENT_STATUS_ID",
                                "VISIBLE_STATUS_ID"
                        }
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusVisibility {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "statusVisibilitySeq"
    )
    @SequenceGenerator(
            name = "statusVisibilitySeq",
            sequenceName = "FD_STATUS_VISIBILITY_SEQ",
            allocationSize = 1
    )
    @Column(name = "VISIBILITY_ID")
    private Long visibilityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    private CompanyMaster company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORK_ITEM_ID", nullable = false)
    private WorkItem workItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENT_STATUS_ID")
    private StatusMaster currentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VISIBLE_STATUS_ID")
    private StatusMaster visibleStatus;

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