package com.flickzz.desk.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.flickzz.desk.model.SystemAudit;

public interface SystemAuditRepository extends JpaRepository<SystemAudit, Long>, JpaSpecificationExecutor<SystemAudit> {

    List<SystemAudit> findByCompanyId(Long orgId);
}
