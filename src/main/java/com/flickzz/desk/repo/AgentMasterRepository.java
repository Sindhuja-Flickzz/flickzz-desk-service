package com.flickzz.desk.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.AgentMaster;
import com.flickzz.desk.model.User;

public interface AgentMasterRepository extends JpaRepository<AgentMaster, Long> {

	Optional<AgentMaster> findByAgentName(String agentName);

	AgentMaster findByUser(User user);

	List<AgentMaster> findAllByOrganization_CompanyId(Long orgId);

	Optional<AgentMaster> findByMailId(String emailId);
}
