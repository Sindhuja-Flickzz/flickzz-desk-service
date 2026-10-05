package com.flickzz.desk.repo;

import com.flickzz.desk.model.TicketWatchlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketWatchlistRepository extends JpaRepository<TicketWatchlist, Long> {

    void deleteByTicket_TicketId(Long ticketId);
}
