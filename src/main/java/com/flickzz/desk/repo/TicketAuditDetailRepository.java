package com.flickzz.desk.repo;

import com.flickzz.desk.model.TicketAuditDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketAuditDetailRepository extends JpaRepository<TicketAuditDetail, Long> {

    @EntityGraph(attributePaths = {"audit", "audit.ticket"})
    @Query("""
            select detail
            from TicketAuditDetail detail
            where detail.audit.ticket.company.companyId = :companyId
            order by detail.audit.changedAt desc, detail.audit.auditId desc, detail.auditDetailId desc
            """)
    List<TicketAuditDetail> findAllByCompanyId(@Param("companyId") Long companyId);
}
