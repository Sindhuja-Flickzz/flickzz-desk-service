package com.flickzz.desk.repo;

import org.springframework.data.jpa.repository.*;

import com.flickzz.desk.model.*;

public interface SystemAuditRepository extends JpaRepository<SystemAudit, Long> {

}
