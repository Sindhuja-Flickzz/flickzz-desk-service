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
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "FD_RITM_MASTER",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_RITM_NUMBER",
                        columnNames = "RITM_NUMBER"
                )
        }
)
public class RitmMaster {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ritmSeq"
    )
    @SequenceGenerator(
            name = "ritmSeq",
            sequenceName = "FD_RITM_SEQ",
            allocationSize = 1
    )
    @Column(name = "RITM_ID")
    private Long ritmId;

    @Column(name = "RITM_NUMBER", nullable = false, length = 50)
    private String ritmNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    private CompanyMaster company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUESTED_BY", nullable = false)
    private AgentMaster requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUESTED_FOR", nullable = false)
    private AgentMaster requestedFor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private BPCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUB_CATEGORY_ID", nullable = false)
    private BPSubCategory subCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUPPORT_GROUP_ID", nullable = false)
    private BPSupportGroup supportGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRIORITY_ID", nullable = false)
    private BPPriority priority;

    @Column(name = "SHORT_DESCRIPTION", nullable = false, length = 255)
    private String shortDescription;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;

    @Column(name = "STEPS_TO_REPRODUCE", columnDefinition = "TEXT")
    private String stepsToReproduce;

    @Column(name = "OTHER_NOTES", columnDefinition = "TEXT")
    private String otherNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNED_TO")
    private AgentMaster assignedTo;

    @OneToMany(mappedBy = "ritm", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<RitmWatchlist> watchlist;

    @OneToMany(mappedBy = "ritm", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<RitmAttachment> attachment;

    @OneToMany(mappedBy = "ritm", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<RitmComment> comments;

    @OneToMany(mappedBy = "ritm", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<RitmAudit> audits;

    @Column(name = "STATUS", nullable = false, length = 30)
    private String status = "OPEN";

    @Column(name = "REQUESTED_AT")
    private LocalDateTime requestedAt;

    @Column(name = "DUE_DATE")
    private LocalDateTime dueDate;

    @Column(name = "RESOLVED_AT")
    private LocalDateTime resolvedAt;

    @Column(name = "CLOSED_AT")
    private LocalDateTime closedAt;

    @Column(name = "CANCELLED_AT")
    private LocalDateTime cancelledAt;

    @Column(name = "ACTION_REASON", columnDefinition = "TEXT")
    private String actionReason;

    @Builder.Default
    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive = true;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "IS_CREATOR_ADMIN", nullable = false)
    private Boolean isCreatorAdmin;

    @Column(name = "IS_UPDATER_ADMIN")
    private Boolean isUpdaterAdmin = false;

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
