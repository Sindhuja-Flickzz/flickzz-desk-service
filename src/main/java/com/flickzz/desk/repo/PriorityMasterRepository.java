package com.flickzz.desk.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.PriorityMaster;

public interface PriorityMasterRepository extends JpaRepository<PriorityMaster, Long> {

	Optional<PriorityMaster> findByOrganizationCompanyIdAndPriorityName(Long organizationId, String priorityName);

}
