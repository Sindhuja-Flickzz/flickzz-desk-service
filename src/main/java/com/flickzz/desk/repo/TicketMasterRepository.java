package com.flickzz.desk.repo;

import com.flickzz.desk.model.TicketMaster;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketMasterRepository extends JpaRepository<TicketMaster, Long> {

    @EntityGraph(attributePaths = {"fieldValues", "fieldValues.templateField"})
    List<TicketMaster> findByCompanyCompanyId(Long orgId);

    @Override
    @EntityGraph(attributePaths = {"fieldValues", "fieldValues.templateField"})
    Optional<TicketMaster> findById(Long ticketId);

    @EntityGraph(attributePaths = {"fieldValues", "fieldValues.templateField"})
    List<TicketMaster> findByRequestedByAgentId(Long agentId);

    @EntityGraph(attributePaths = {"fieldValues", "fieldValues.templateField"})
    List<TicketMaster> findByAssignedToAgentId(Long agentId);

    long countBySupportGroupSupportGroupIdAndStatusStatusIdAndIsActiveTrue(Long supportGroupId, Long statusId);

    long countBySupportGroupSupportGroupIdAndIsActiveTrue(Long supportGroupId);

    long countBySupportGroupSupportGroupIdAndAssignedToIsNullAndIsActiveTrue(Long supportGroupId);

    long countBySupportGroupSupportGroupIdAndAssignedToAgentIdAndIsActiveTrue(Long supportGroupId, Long agentId);

    @EntityGraph(attributePaths = {"fieldValues", "fieldValues.templateField"})
    List<TicketMaster> findByAssignedToIsNullAndSupportGroupSupportGroupIdIn(List<Long> supportGroupIds);

    List<TicketMaster> findByAssignedToIsNullAndSupportGroupSupportGroupId(Long supportGroupId);

    List<TicketMaster> findByStatusStatusIdAndSupportGroupSupportGroupId(Long statusId, Long supportGroupId);

    List<TicketMaster> findBySupportGroupSupportGroupIdAndStatusIsActiveFalse(Long supportGroupId);
}
