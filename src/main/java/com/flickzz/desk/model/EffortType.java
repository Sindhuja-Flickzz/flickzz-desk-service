package com.flickzz.desk.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "FD_EFFORT_TYPE")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EffortType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "effortTypeGen")
    @SequenceGenerator(name = "effortTypeGen", sequenceName = "FD_EFFORT_TYPE_SEQ", allocationSize = 1)
    @Column(name = "EFFORT_TYPE_ID", nullable = false)
    private Long effortTypeId;

    @Column(name = "EFFORT_TYPE_NAME", nullable = false, length = 100)
    private String typeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    private CompanyMaster company;

    @Column(name = "IS_ACTIVE", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "DISPLAY_ORDER", nullable = false)
    private Integer sequence;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "IS_CREATOR_ADMIN", nullable = false)
    private Boolean isCreatorAdmin;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "IS_UPDATER_ADMIN", nullable = false)
    private Boolean isUpdaterAdmin;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
