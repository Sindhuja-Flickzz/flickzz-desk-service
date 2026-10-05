package com.flickzz.desk.repo;

import com.flickzz.desk.model.TicketFieldValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketFieldValueRepository extends JpaRepository<TicketFieldValue, Long> {

    List<TicketFieldValue> findAllByTicket_TicketId(Long ticketId);
}