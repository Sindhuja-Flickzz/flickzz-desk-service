package com.flickzz.desk.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.AgentMaster;
import com.flickzz.desk.model.User;

public interface AgentMasterRepository extends JpaRepository<AgentMaster, Long> {

	Optional<AgentMaster> findByAgentNameAndIsActiveTrue(String agentName);

	AgentMaster findByUserAndIsActiveTrue(User user);

	List<AgentMaster> findAllByOrganization_CompanyId(Long orgId);

	Optional<AgentMaster> findByMailIdAndIsActiveTrue(String emailId);

    boolean existsByCalendarMaster_CalendarIdAndIsActiveTrue(Long calendarId);
}
