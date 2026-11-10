package com.flickzz.desk.repo;

import com.flickzz.desk.model.TicketAgentAllocation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketAgentAllocationRepository extends JpaRepository<TicketAgentAllocation, Long> {

    @EntityGraph(attributePaths = {"ticket.company", "agent", "workItem"})
    List<TicketAgentAllocation> findByTicket_Company_CompanyIdAndAgent_AgentId(Long companyId, Long agentId);

    @EntityGraph(attributePaths = {"agent", "workItem"})
    Optional<TicketAgentAllocation> findByTicket_TicketIdAndAgent_AgentIdAndWorkItem_ItemId(
            Long ticketId, Long agentId, Long workItemId);

    List<TicketAgentAllocation> findByTicket_TicketId(Long ticketId);
}
