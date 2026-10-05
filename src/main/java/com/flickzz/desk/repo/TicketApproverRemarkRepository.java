package com.flickzz.desk.repo;

import com.flickzz.desk.model.TicketApproverRemark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketApproverRemarkRepository extends JpaRepository<TicketApproverRemark, Long> {
}