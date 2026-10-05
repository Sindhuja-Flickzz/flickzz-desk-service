package com.flickzz.desk.repo;

import com.flickzz.desk.model.TicketApprover;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketApproverRepository extends JpaRepository<TicketApprover, Long> {

    List<TicketApprover> findByTicket_TicketIdAndIsActiveTrueOrderByApproverSequenceAsc(Long ticketId);
}