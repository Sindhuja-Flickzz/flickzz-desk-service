package com.flickzz.desk.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.PlantMaster;

public interface PlantMasterRepository extends JpaRepository<PlantMaster, Long> {

	Optional<PlantMaster> findByPlantName(String plantName);

	Optional<PlantMaster> findByPlantId(Long plantId);

	List<PlantMaster> findAllByCompany_CompanyIdAndIsActive(Long companyId, Boolean active);

	Optional<PlantMaster> findByPlantNameAndCompany_CompanyIdAndIsActive(String plantName, Long companyId,
			Boolean active);

}
