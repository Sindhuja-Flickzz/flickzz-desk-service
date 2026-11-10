package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "FD_WORK_ITEM_EFFORT_TYPE_MAPPING")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkItemEffortTypeMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workItemEffortTypeMappingSeq")
    @SequenceGenerator(name = "workItemEffortTypeMappingSeq",
            sequenceName = "FD_WORK_ITEM_EFFORT_TYPE_MAPPING_SEQ", allocationSize = 1)
    @Column(name = "MAPPING_ID")
    private Long mappingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    private CompanyMaster company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORK_ITEM_ID", nullable = false)
    private WorkItem workItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EFFORT_TYPE_ID", nullable = false)
    private EffortType effortType;

    @Builder.Default
    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive = true;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @Column(name = "IS_CREATOR_ADMIN", nullable = false)
    private Boolean isCreatorAdmin = false;

    @Builder.Default
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
    }
}
