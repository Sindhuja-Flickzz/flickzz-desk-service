package com.flickzz.desk.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flickzz.desk.model.CalendarType;

@Repository
public interface CalendarTypeRepository extends JpaRepository<CalendarType, Long> {

	Optional<CalendarType> findByTypeNameAndCompany_CompanyId(String type, Long companyId);

	List<CalendarType> findAllByCompany_CompanyIdAndIsActive(Long companyId, Boolean active);

	Optional<CalendarType> findByCalendarTypeIdAndCompany_CompanyId(Long calendarTypeId, Long companyId);
}
