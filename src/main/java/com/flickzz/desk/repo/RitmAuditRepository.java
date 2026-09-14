package com.flickzz.desk.repo;

import com.flickzz.desk.model.RitmAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RitmAuditRepository extends JpaRepository<RitmAudit, Long> {
    
    List<RitmAudit> findAllByRitmRitmIdOrderByAuditIdDesc(Long ritmId);
}
