package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "FD_WORK_ITEM_REQUEST_TYPE_MAPPING")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkItemRequestTypeMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workItemRequestTypeMappingSeq")
    @SequenceGenerator(name = "workItemRequestTypeMappingSeq",
            sequenceName = "FD_WORK_ITEM_REQUEST_TYPE_MAPPING_SEQ", allocationSize = 1)
    @Column(name = "MAPPING_ID")
    private Long mappingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    private CompanyMaster company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORK_ITEM_ID", nullable = false)
    private WorkItem workItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_TYPE_ID", nullable = false)
    private RequestTypeMaster requestType;

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
