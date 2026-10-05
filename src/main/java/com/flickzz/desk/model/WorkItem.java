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
@Table(name = "FD_WORK_ITEMS")
public class WorkItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "work_item_seq")
    @SequenceGenerator(name = "work_item_seq", sequenceName = "WORK_ITEM_SEQ", allocationSize = 1)
    @Column(name = "ITEM_ID")
    private Long itemId;

    @Column(name = "CODE", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "LABEL", nullable = false, length = 100)
    private String label;

    @Builder.Default
    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    private CompanyMaster company;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "IS_CREATOR_ADMIN", nullable = false)
    private Boolean isCreatorAdmin;

    @Builder.Default
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
