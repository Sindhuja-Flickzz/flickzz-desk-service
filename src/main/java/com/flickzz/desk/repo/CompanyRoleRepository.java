package com.flickzz.desk.repo;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import com.flickzz.desk.model.*;

@Repository
public interface CompanyRoleRepository extends JpaRepository<BusinessPartner, Long> {

	List<BusinessPartner> findByCompany_CompanyIdAndIsActive(Long valueOf, boolean active);

	Optional<BusinessPartner> findByCompany_CompanyIdAndMappedCompany_CompanyIdAndIsActive(Long companyId, Long bpUid,
			boolean isActive);
}