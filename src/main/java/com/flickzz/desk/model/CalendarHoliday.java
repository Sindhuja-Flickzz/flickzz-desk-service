package com.flickzz.desk.model;

import java.util.Date;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_CALENDAR_HOLIDAY")
public class CalendarHoliday {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calendarHolidayGen")
    @SequenceGenerator(name = "calendarHolidayGen", sequenceName = "CALENDAR_HOLIDAY_SEQ", allocationSize = 1)
    @Column(name = "HOLIDAY_ID", unique = true, nullable = false)
    private Long holidayId;

    @Column(name = "HOLIDAY_DATE", nullable = false, length = 10)
    private Date holidayDate;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

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
    private Date createdAt;

    @Column(name = "UPDATED_AT")
    private Date updatedAt;
}
