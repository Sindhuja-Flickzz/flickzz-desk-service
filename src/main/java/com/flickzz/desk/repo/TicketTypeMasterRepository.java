package com.flickzz.desk.repo;

import java.util.*;

import org.springframework.data.jpa.repository.*;

import com.flickzz.desk.model.*;

public interface TicketTypeMasterRepository extends JpaRepository<TicketTypeMaster, Long> {

	List<TicketTypeMaster> findByIsActiveTrue();

	Optional<TicketTypeMaster> findByTicketTypeIdAndIsActiveTrue(Long ticketTypeId);
}
