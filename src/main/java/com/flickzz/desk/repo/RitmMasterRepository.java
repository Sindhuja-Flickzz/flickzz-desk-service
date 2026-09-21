package com.flickzz.desk.repo;

import com.flickzz.desk.model.RitmMaster;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RitmMasterRepository extends JpaRepository<RitmMaster, Long> {

    @EntityGraph(attributePaths = {"fieldValues", "fieldValues.templateField"})
    List<RitmMaster> findByCompanyCompanyId(Long orgId);

    @Override
    @EntityGraph(attributePaths = {"fieldValues", "fieldValues.templateField"})
    java.util.Optional<RitmMaster> findById(Long ritmId);

    @EntityGraph(attributePaths = {"fieldValues", "fieldValues.templateField"})
    List<RitmMaster> findByRequestedByAgentId(Long agentId);

    @EntityGraph(attributePaths = {"fieldValues", "fieldValues.templateField"})
    List<RitmMaster> findByAssignedToAgentId(Long agentId);

    long countBySupportGroupSupportGroupIdAndStatusStatusIdAndIsActiveTrue(Long supportGroupId, Long statusId);

    long countBySupportGroupSupportGroupIdAndIsActiveTrue(Long supportGroupId);

    long countBySupportGroupSupportGroupIdAndAssignedToIsNullAndIsActiveTrue(Long supportGroupId);

    long countBySupportGroupSupportGroupIdAndAssignedToAgentIdAndIsActiveTrue(Long supportGroupId, Long agentId);

    @EntityGraph(attributePaths = {"fieldValues", "fieldValues.templateField"})
    List<RitmMaster> findByAssignedToIsNullAndSupportGroupSupportGroupIdIn(List<Long> supportGroupIds);

    List<RitmMaster> findByAssignedToIsNullAndSupportGroupSupportGroupId(Long supportGroupId);

    List<RitmMaster> findByStatusStatusIdAndSupportGroupSupportGroupId(Long statusId, Long supportGroupId);

    List<RitmMaster> findBySupportGroupSupportGroupIdAndStatusIsActiveFalse(Long supportGroupId);
}
