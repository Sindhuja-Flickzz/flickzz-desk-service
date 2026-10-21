package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "FD_AGENT_EFFORT_LOG")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentEffortLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agentEffortLogGen")
    @SequenceGenerator(name = "agentEffortLogGen", sequenceName = "FD_AGENT_EFFORT_LOG_SEQ", allocationSize = 1)
    @Column(name = "EFFORT_ID", nullable = false)
    private Long effortId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AGENT_ID", nullable = false)
    private AgentMaster agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORK_ITEM_ID", nullable = false)
    private WorkItem workItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TICKET_ID", nullable = false)
    private TicketMaster ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EFFORT_TYPE_ID", nullable = false)
    private EffortType effortType;

    @Column(name = "EFFORT_DATE", nullable = false)
    private LocalDate effortDate;

    @Column(name = "EFFORT_HOURS")
    private Integer effortHours;

    @Column(name = "EFFORT_MINUTES")
    private Integer effortMinutes;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String remark;

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

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
