package com.flickzz.desk.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.BPSupportGroupManager;

public interface BPSupportGroupManagerRepository extends JpaRepository<BPSupportGroupManager, Long> {

	List<BPSupportGroupManager> findBySupportGroupSupportGroupId(Long supportGroupId);

	Optional<BPSupportGroupManager> findBySupportGroupSupportGroupIdAndAgentAgentId(Long supportGroupId,
			Long agentId);

	boolean existsBySupportGroupSupportGroupIdAndAgentAgentIdAndIsActive(Long supportGroupId, Long agentId,
			Boolean active);
}