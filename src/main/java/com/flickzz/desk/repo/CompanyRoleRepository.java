package com.flickzz.desk.repo;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;

import com.flickzz.desk.model.*;

@Repository
public interface CompanyRoleRepository extends JpaRepository<BusinessPartner, Long> {

	@Query("""
			SELECT bp
			FROM BusinessPartner bp
			WHERE bp.isActive = :active
			  AND (
			        bp.company.companyId = :companyId
			     OR bp.mappedCompany.companyId = :companyId
			  )
			""")
	List<BusinessPartner> findActivePartnersByCompany(@Param("companyId") Long companyId,
			@Param("active") boolean active);

	Optional<BusinessPartner> findByCompany_CompanyIdAndMappedCompany_CompanyIdAndIsActive(Long companyId,
			Long mappedCompanyId, boolean isActive);
}