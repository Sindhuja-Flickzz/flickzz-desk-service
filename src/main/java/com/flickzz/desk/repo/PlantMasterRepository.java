package com.flickzz.desk.repo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.PlantMaster;

public interface PlantMasterRepository extends JpaRepository<PlantMaster, Long> {

	Optional<PlantMaster> findByPlantName(String plantName);

	Optional<PlantMaster> findByPlantId(Long plantId);

	List<PlantMaster> findAllByCompany_CompanyId(Long companyId);

	Optional<PlantMaster> findByPlantNameAndCompany_CompanyId(String plantName, Long companyId);

	boolean existsByCalendar_CalendarIdAndIsActiveTrue(Long calendarId);

	List<PlantMaster> findAllByCompany_CompanyIdAndIsActiveTrue(Long companyId);
}
