package com.flickzz.desk.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.AgentMaster;

public interface AgentMasterRepository extends JpaRepository<AgentMaster, Long> {

	Optional<AgentMaster> findByAgentName(String agentName);
}
