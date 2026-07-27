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
@Table(name = "FD_COMPANY_APPROVER")
public class CompanyApprover {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "approverGen")
    @SequenceGenerator(name = "approverGen", sequenceName = "APPROVER_SEQ", allocationSize = 1)
    @Column(name = "APPROVER_ID")
    private Long approverId;

    @ManyToOne
    @JoinColumn(name = "COMPANY_ID", foreignKey = @ForeignKey(name = "FK_COMPANY_APPROVER_COMPANY"))
    private CompanyMaster company;

    @ManyToOne
    @JoinColumn(name = "AGENT_ID", foreignKey = @ForeignKey(name = "FK_COMPANY_APPROVER_AGENT"))
    private AgentMaster agent;

    @Column(name = "LEVEL", nullable = false)
    private Integer level;

    @Builder.Default
    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;

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
