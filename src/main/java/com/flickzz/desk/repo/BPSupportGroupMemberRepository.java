package com.flickzz.desk.repo;

import com.flickzz.desk.model.AgentMaster;
import com.flickzz.desk.model.BPSupportGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BPSupportGroupMemberRepository extends JpaRepository<BPSupportGroupMember, Long> {

    List<BPSupportGroupMember> findBySupportGroupSupportGroupIdAndIsActive(Long supportGroupId, Boolean active);

    List<BPSupportGroupMember> findBySupportGroupSupportGroupId(Long supportGroupId);

    List<BPSupportGroupMember> findBySupportGroupSupportGroupIdInAndIsActive(List<Long> supportGroupIds, Boolean active);

    Optional<BPSupportGroupMember> findBySupportGroupSupportGroupIdAndAgentAgentIdAndIsActive(Long supportGroupId,
                                                                                              Long agentId, Boolean active);

    Optional<BPSupportGroupMember> findBySupportGroupSupportGroupIdAndAgentAgentId(Long supportGroupId, Long agentId);

    boolean existsBySupportGroupSupportGroupIdAndAgentAgentIdAndIsActive(Long supportGroupId, Long agentId,
                                                                         Boolean active);

    @Query("select m.supportGroup.supportGroupId from BPSupportGroupMember m "
            + "where m.agent.agentId = :agentId and m.isActive = true and m.supportGroup.isActive = true "
            + "and m.agent.organization.companyId = :orgId order by m.supportGroup.supportGroupId asc")
    List<Long> findActiveSupportGroupIdsByAgentIdAndOrgId(@Param("agentId") Long agentId, @Param("orgId") Long orgId);

    @Query("select distinct m.agent from BPSupportGroupMember m "
            + "where m.supportGroup.supportGroupId in :supportGroupIds "
            + "and m.isActive = true and m.supportGroup.isActive = true "
            + "and m.agent.isActive = true ")
    List<AgentMaster> findDistinctActiveUsersBySupportGroupIds(@Param("supportGroupIds") List<Long> supportGroupIds);
}
