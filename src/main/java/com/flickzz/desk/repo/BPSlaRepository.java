package com.flickzz.desk.repo;

import java.util.*;

import org.springframework.data.jpa.repository.*;

import com.flickzz.desk.model.*;

public interface BPSlaRepository extends JpaRepository<BPSla, Long> {

	List<BPSla> findByConfigurationConfigurationIdAndIsActive(Long configurationId, Boolean active);

	Optional<BPSla> findBySlaIdAndIsActive(Long slaId, Boolean active);

	boolean existsByConfigurationConfigurationIdAndPriorityPriorityIdAndIsActive(Long configurationId, Long priorityId,
			Boolean active);

}
