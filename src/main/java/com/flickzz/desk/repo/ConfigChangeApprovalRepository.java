package com.flickzz.desk.repo;

import com.flickzz.desk.model.ConfigChangeApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfigChangeApprovalRepository  extends JpaRepository<ConfigChangeApproval, Long> {
	List<ConfigChangeApproval> findByApproverUserId(Long approverUserId);
}
