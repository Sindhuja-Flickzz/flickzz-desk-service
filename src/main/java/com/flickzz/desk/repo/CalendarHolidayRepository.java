package com.flickzz.desk.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flickzz.desk.model.CalendarHoliday;

@Repository
public interface CalendarHolidayRepository extends JpaRepository<CalendarHoliday, Long> {
}
