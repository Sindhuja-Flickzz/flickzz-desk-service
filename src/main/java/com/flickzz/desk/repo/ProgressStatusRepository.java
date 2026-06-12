package com.flickzz.desk.repo;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;

import com.flickzz.desk.model.*;

@Repository
public interface ProgressStatusRepository extends JpaRepository<ProgressStatus, Long> {

	List<ProgressStatus> findByCompanyCompanyIdAndIsActiveOrderByProgressSequenceAsc(Long orgId, boolean active);

	@Query("SELECT MAX(p.progressSequence) " + "FROM ProgressStatus p"
			+ " WHERE p.company.companyId = :companyId AND isActive = true")
	Integer findMaxProgressStatus(@Param("companyId") Long companyId);
}
