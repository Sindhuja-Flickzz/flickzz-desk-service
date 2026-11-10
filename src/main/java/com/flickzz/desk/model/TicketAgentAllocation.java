package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "FD_TICKET_AGENT_ALLOCATION",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_TICKET_AGENT_WORK_ITEM",
                columnNames = {"TICKET_ID", "WORK_ITEM_ID", "AGENT_ID"}
        )
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketAgentAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticketAgentAllocationSeq")
    @SequenceGenerator(
            name = "ticketAgentAllocationSeq",
            sequenceName = "FD_TICKET_AGENT_ALLOCATION_SEQ",
            allocationSize = 1
    )
    @Column(name = "ALLOCATION_ID")
    private Long allocationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AGENT_ID", nullable = false)
    private AgentMaster agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORK_ITEM_ID", nullable = false)
    private WorkItem workItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TICKET_ID", nullable = false)
    private TicketMaster ticket;

    @Column(name = "PLANNED_HOURS", nullable = false)
    private Integer plannedHours;

    @Column(name = "PLANNED_MINUTES", nullable = false)
    private Integer plannedMinutes;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Builder.Default
    @Column(name = "IS_CREATOR_ADMIN", nullable = false)
    private Boolean isCreatorAdmin = false;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Builder.Default
    @Column(name = "IS_UPDATER_ADMIN", nullable = false)
    private Boolean isUpdaterAdmin = false;

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
