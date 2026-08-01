package com.flickzz.desk.repo;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;

import com.flickzz.desk.model.*;

@Repository
public interface BusinessPartnerRepository extends JpaRepository<BusinessPartner, Long> {

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

	@Query(value = """
			SELECT COUNT(*)
			FROM FD_BUSINESS_PARTNER
			WHERE (
			        (COMPANY_ID = :companyId AND MAPPING_ID = :mappedCompanyId)
			     OR (COMPANY_ID = :mappedCompanyId AND MAPPING_ID = :companyId)
			)
			AND IS_ACTIVE = TRUE
			""", nativeQuery = true)
	long existsBusinessPartnerMapping(@Param("companyId") Long companyId,
			@Param("mappedCompanyId") Long mappedCompanyId);

	@Query("""
			SELECT bp
			FROM BusinessPartner bp
			WHERE bp.isActive = true
			  AND bp.validTo IS NOT NULL
			  AND bp.validTo <= :now
			""")
	List<BusinessPartner> findExpiredActivePartners(@Param("now") Date now);

	Optional<BusinessPartner> findByBusinessPartnerIdAndIsActive(Long businessPartnerId, Boolean active);

	Optional<BusinessPartner> findByCompany_CompanyIdAndMappedCompany_CompanyIdAndIsActive(Long companyId,
			Long mappedCompanyId, Boolean active);
}