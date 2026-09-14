package com.flickzz.desk.repo;

import com.flickzz.desk.model.RitmMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RitmMasterRepository extends JpaRepository<RitmMaster, Long> {

    List<RitmMaster> findByCompanyCompanyId(Long orgId);

    List<RitmMaster> findByRequestedByAgentId(Long agentId);

    List<RitmMaster> findByAssignedToAgentId(Long agentId);

    List<RitmMaster> findByAssignedToIsNullAndSupportGroupSupportGroupIdIn(List<Long> supportGroupIds);
}
