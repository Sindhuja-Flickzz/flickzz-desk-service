package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "FD_TICKET_APPROVER_REMARK")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketApproverRemark {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ticketApproverRemarkSeq"
    )
    @SequenceGenerator(
            name = "ticketApproverRemarkSeq",
            sequenceName = "FD_TICKET_APPROVER_REMARK_SEQ",
            allocationSize = 1
    )
    @Column(name = "REMARK_ID")
    private Long remarkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "TICKET_APPROVER_ID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "FK_RITM_APPROVER_REMARK_APPROVER"
            )
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TicketApprover ticketApprover;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "TICKET_ID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "FK_TICKET_APPROVER_REMARK_TICKET"
            )
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TicketMaster ticket;

    @Column(
            name = "REMARK_TYPE",
            length = 30,
            nullable = false
    )
    private String remarkType;

    @Column(
            name = "REMARK",
            length = 4000,
            nullable = false
    )
    private String remark;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "CREATED_ON", nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive = true;
}