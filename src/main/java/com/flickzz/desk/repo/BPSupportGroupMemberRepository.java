package com.flickzz.desk.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.BPSupportGroupMember;

public interface BPSupportGroupMemberRepository extends JpaRepository<BPSupportGroupMember, Long> {

	List<BPSupportGroupMember> findBySupportGroupSupportGroupIdAndIsActive(Long supportGroupId, Boolean active);

	List<BPSupportGroupMember> findBySupportGroupSupportGroupId(Long supportGroupId);

	Optional<BPSupportGroupMember> findBySupportGroupSupportGroupIdAndAgentAgentIdAndIsActive(Long supportGroupId,
			Long agentId, Boolean active);

	Optional<BPSupportGroupMember> findBySupportGroupSupportGroupIdAndAgentAgentId(Long supportGroupId, Long agentId);

	boolean existsBySupportGroupSupportGroupIdAndAgentAgentIdAndIsActive(Long supportGroupId, Long agentId,
			Boolean active);
}
