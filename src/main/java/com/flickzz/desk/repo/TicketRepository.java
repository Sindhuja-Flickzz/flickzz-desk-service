package com.flickzz.desk.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

}
