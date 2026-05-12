package com.flickzz.desk.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.ImpactMaster;

public interface ImpactMasterRepository extends JpaRepository<ImpactMaster, Long> {

	Optional<ImpactMaster> findByOrganizationCompanyIdAndImpactCode(Long orgId, String impactCode);

	List<ImpactMaster> findAllByOrganizationCompanyIdAndIsActive(Long orgId, Boolean active);

	List<ImpactMaster> findAllByIsActive(Boolean active);

}
