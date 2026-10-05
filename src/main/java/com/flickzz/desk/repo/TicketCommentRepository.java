package com.flickzz.desk.repo;

import com.flickzz.desk.model.TicketComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {

    List<TicketComment> findAllByTicket_TicketIdOrderByCommentIdDesc(Long ticketId);
}
