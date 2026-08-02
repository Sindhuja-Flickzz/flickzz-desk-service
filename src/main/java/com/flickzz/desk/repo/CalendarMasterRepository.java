package com.flickzz.desk.repo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flickzz.desk.model.CalendarMaster;

@Repository
public interface CalendarMasterRepository extends JpaRepository<CalendarMaster, Long> {
	Optional<CalendarMaster> findByCalendarCode(String calendarCode);

	Optional<CalendarMaster> findByCalendarCodeAndIsActive(String calendarCode, Boolean isActive);

	Optional<CalendarMaster> findByCalendarCodeAndCompany_CompanyIdAndIsActive(String calendarCode, Long companyId,
			Boolean isActive);

	Optional<CalendarMaster> findByCalendarCodeAndCompany_CompanyId(String calendarCode, Long company);

    boolean existsByCalendarType_CalendarTypeId(Long calendarTypeId);

	List<CalendarMaster> findAllByCompany_CompanyIdAndIsActiveTrue(Long aLong);
}
