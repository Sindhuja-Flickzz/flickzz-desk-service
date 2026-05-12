package com.flickzz.desk.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.TicketWatchlist;

public interface TicketWatchlistRepository extends JpaRepository<TicketWatchlist, Long> {

}
