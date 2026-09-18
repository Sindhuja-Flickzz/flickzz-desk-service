package com.flickzz.desk.repo;

import com.flickzz.desk.model.RitmMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

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

    @EntityGraph(attributePaths = {"fieldValues", "fieldValues.templateField"})
    List<RitmMaster> findByAssignedToIsNullAndSupportGroupSupportGroupIdIn(List<Long> supportGroupIds);
}
