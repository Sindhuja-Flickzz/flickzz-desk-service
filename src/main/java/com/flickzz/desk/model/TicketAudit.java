package com.flickzz.desk.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_TICKET_AUDIT")
public class TicketAudit {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ticketAuditSeq"
    )
    @SequenceGenerator(
            name = "ticketAuditSeq",
            sequenceName = "FD_TICKET_AUDIT_SEQ",
            allocationSize = 1
    )
    @Column(name = "AUDIT_ID")
    private Long auditId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TICKET_ID", nullable = false)
    private TicketMaster ticket;

    @Column(name = "ACTION_TYPE", nullable = false, length = 50)
    private String actionType;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "audit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private List<TicketAuditDetail> auditDetails;

    @Column(name = "CHANGED_BY", nullable = false)
    private Long changedBy;

    @Column(name = "CHANGED_AT")
    private LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        if (changedAt == null) {
            changedAt = LocalDateTime.now();
        }
    }
}
