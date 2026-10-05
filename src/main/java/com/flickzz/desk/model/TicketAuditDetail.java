package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FD_TICKET_AUDIT_DETAIL")
public class TicketAuditDetail {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ticketAuditDetailSeq"
    )
    @SequenceGenerator(
            name = "ticketAuditDetailSeq",
            sequenceName = "FD_TICKET_AUDIT_DETAIL_SEQ",
            allocationSize = 1
    )
    @Column(name = "AUDIT_DETAIL_ID")
    private Long auditDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AUDIT_ID", nullable = false)
    private TicketAudit audit;

    @Column(name = "FIELD_NAME", nullable = false, length = 100)
    private String fieldName;

    @Column(name = "OLD_VALUE", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "NEW_VALUE", columnDefinition = "TEXT")
    private String newValue;
}
