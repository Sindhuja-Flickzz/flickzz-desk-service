package com.flickzz.desk.repo;

import com.flickzz.desk.model.AgentEffortLog;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgentEffortLogRepository extends JpaRepository<AgentEffortLog, Long> {

    List<AgentEffortLog> findByTicket_TicketIdAndTicket_Company_CompanyIdOrderByEffortDateDescEffortIdDesc(
            Long ticketId, Long companyId);

    @EntityGraph(attributePaths = {"agent", "ticket"})
    List<AgentEffortLog> findByTicket_TicketIdInAndTicket_Company_CompanyIdOrderByEffortDateDescEffortIdDesc(
            List<Long> ticketIds, Long companyId);

    List<AgentEffortLog> findByAgent_AgentIdAndTicket_TicketIdAndTicket_Company_CompanyIdOrderByEffortDateDescEffortIdDesc(
            Long agentId, Long ticketId, Long companyId);

    Optional<AgentEffortLog> findByEffortIdAndTicket_TicketIdAndTicket_Company_CompanyId(
            Long effortId, Long ticketId, Long companyId);
}
