package com.flickzz.desk.repo;

import java.util.*;

import org.springframework.data.jpa.repository.*;

import com.flickzz.desk.model.*;

public interface CompanyMasterRepository extends JpaRepository<CompanyMaster, Long> {
	Optional<CompanyMaster> findByCompanyName(String companyName);

	Optional<CompanyMaster> findByCompanyNameOrRegisteredNumber(String companyName, String registeredNumber);

	Optional<CompanyMaster> findByCompanyNameAndIsActive(String orgName, Boolean active);

	@Query("SELECT c.uid FROM CompanyMaster c WHERE c.companyId = (SELECT MAX(cm.companyId) FROM CompanyMaster cm)")
	String findMaxUid();

	Optional<CompanyMaster> findByCompanyIdAndIsActive(Long companyId, Boolean active);

	Optional<CompanyMaster> findByCompanyId(Long companyId);

	List<CompanyMaster> findByIsActive(Boolean active);

	Optional<CompanyMaster> findByUidAndIsActive(String uid, Boolean active);
}
