package com.flickzz.desk.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.CompanyMaster;

public interface CompanyMasterRepository extends JpaRepository<CompanyMaster, Long> {
    Optional<CompanyMaster> findByCompanyName(String companyName);

	Optional<CompanyMaster> findByCompanyNameOrRegisteredNumber(String companyName, String registeredNumber);
}
