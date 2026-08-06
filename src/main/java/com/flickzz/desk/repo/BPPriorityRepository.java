package com.flickzz.desk.repo;

import java.util.*;

import org.springframework.data.jpa.repository.*;

import com.flickzz.desk.model.*;

public interface BPPriorityRepository extends JpaRepository<BPPriority, Long> {

	List<BPPriority> findByConfigurationConfigurationId(Long configurationId);

	boolean existsByConfigurationConfigurationIdAndTicketTypeTicketTypeIdAndLevel(Long configurationId,
			Long ticketTypeId, Integer level);

	boolean existsByConfigurationConfigurationIdAndTicketTypeTicketTypeIdAndCode(Long configurationId,
			Long ticketTypeId, String code);

	Optional<BPPriority> findByPriorityIdAndIsActive(Long priorityId, boolean active);

}
