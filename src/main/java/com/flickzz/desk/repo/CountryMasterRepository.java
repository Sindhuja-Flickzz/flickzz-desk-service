package com.flickzz.desk.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.CountryMaster;

public interface CountryMasterRepository extends JpaRepository<CountryMaster, Long> {
}
