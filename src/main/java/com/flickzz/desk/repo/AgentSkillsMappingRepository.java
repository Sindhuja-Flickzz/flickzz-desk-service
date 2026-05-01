package com.flickzz.desk.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.AgentSkillsMapping;

public interface AgentSkillsMappingRepository extends JpaRepository<AgentSkillsMapping, Long> {

	List<AgentSkillsMapping> findByAgentAgentId(Long agentId);
}
