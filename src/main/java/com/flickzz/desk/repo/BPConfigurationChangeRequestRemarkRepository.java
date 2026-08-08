package com.flickzz.desk.repo;

import com.flickzz.desk.model.BPConfigurationChangeRequest;
import com.flickzz.desk.model.BPConfigurationChangeRequestRemark;
import com.flickzz.desk.model.ConfigChangeApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BPConfigurationChangeRequestRemarkRepository extends JpaRepository<BPConfigurationChangeRequestRemark, Long> {
    List<BPConfigurationChangeRequestRemark> findByConfigurationChangeRequest_CcrId(Long ccrId);
}

