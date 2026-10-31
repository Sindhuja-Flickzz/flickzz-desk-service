package com.flickzz.desk.repo;

import com.flickzz.desk.model.TicketClosureApprovalConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketClosureApprovalConfigRepository
        extends JpaRepository<TicketClosureApprovalConfig, Long> {

    List<TicketClosureApprovalConfig> findByCompany_CompanyIdAndIsActiveTrueOrderByConfigIdAsc(Long companyId);

    List<TicketClosureApprovalConfig> findByWorkItem_ItemIdAndCompany_CompanyIdAndIsActiveTrueOrderByConfigIdAsc(
            Long workItemId, Long companyId);

    Optional<TicketClosureApprovalConfig> findByConfigIdAndCompany_CompanyId(Long configId, Long companyId);
}
