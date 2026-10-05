package com.flickzz.desk.repo;

import com.flickzz.desk.model.TicketAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketAuditRepository extends JpaRepository<TicketAudit, Long> {

    List<TicketAudit> findAllByTicket_TicketIdOrderByAuditIdDesc(Long ticketId);
}
