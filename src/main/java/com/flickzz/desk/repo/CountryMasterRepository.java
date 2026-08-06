package com.flickzz.desk.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.CountryMaster;

import java.util.Collection;
import java.util.Optional;

public interface CountryMasterRepository extends JpaRepository<CountryMaster, Long> {

    Optional<CountryMaster> findByCountryIdAndIsActiveTrue(Long countryId);

    Collection<CountryMaster> findAllByIsActiveTrue();
}
