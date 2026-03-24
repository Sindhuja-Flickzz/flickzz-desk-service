package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_CALENDAR_WORKDAYS")
public class CalendarWorkday {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workdayGen")
    @SequenceGenerator(name = "workdayGen", sequenceName = "WORKDAY_SEQ", allocationSize = 1)
    @Column(name = "WORKDAY_ID", unique = true, nullable = false)
    private Long workdayId;

    @Column(name = "WORKDAY", nullable = false, length = 20)
    private String workday; // e.g., "Monday", "Tuesday"

    @ManyToOne
    @JoinColumn(name = "CALENDAR_ID", nullable = false)
    private CalendarMaster calendarMaster;

    @Builder.Default
    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;

    @Column(name = "CREATED_AT", updatable = false)
    private java.util.Date createdAt;

    @Column(name = "UPDATED_AT")
    private java.util.Date updatedAt;
}
