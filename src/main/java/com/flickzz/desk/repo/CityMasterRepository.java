package com.flickzz.desk.repo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.flickzz.desk.vo.CityMasterVO;
import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.CityMaster;

public interface CityMasterRepository extends JpaRepository<CityMaster, Long> {

	Optional<CityMaster> findByCityIdAndIsActiveTrue(Long cityId);

	List<CityMaster> findByCountryCountryIdAndIsActiveTrue(Long aLong);

	Collection<CityMaster> findAllByIsActiveTrue();

	Collection<CityMaster> findByStateStateIdAndIsActiveTrue(Long aLong);
}
