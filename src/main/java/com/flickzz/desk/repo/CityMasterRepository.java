package com.flickzz.desk.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.CityMaster;

public interface CityMasterRepository extends JpaRepository<CityMaster, Long> {

	List<CityMaster> findByCountryCountryIdAndIsActive(Long valueOf, Boolean isActive);

	List<CityMaster> findByStateStateIdAndIsActive(Long valueOf, Boolean active);
}
