package com.flickzz.desk.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flickzz.desk.model.CalendarMaster;

@Repository
public interface CalendarMasterRepository extends JpaRepository<CalendarMaster, Long> {
    Optional<CalendarMaster> findByCalendarCode(String calendarCode);
    void deleteByCalendarCode(String calendarCode);
    
    Optional<CalendarMaster> findByCalendarCodeAndIsActive(String calendarCode, Boolean isActive);
}
