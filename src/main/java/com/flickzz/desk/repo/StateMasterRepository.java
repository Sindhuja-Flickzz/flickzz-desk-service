package com.flickzz.desk.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.StateMaster;

public interface StateMasterRepository extends JpaRepository<StateMaster, Long> {

	List<StateMaster> findByCountry_CountryIdAndIsActive(Long valueOf, Boolean active);

	List<StateMaster> findAllByIsActive(Boolean active);

}
