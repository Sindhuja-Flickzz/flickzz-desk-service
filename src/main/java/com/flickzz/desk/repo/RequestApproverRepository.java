package com.flickzz.desk.repo;

import com.flickzz.desk.model.RequestApprover;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestApproverRepository extends JpaRepository<RequestApprover, Long> {

    List<RequestApprover> findByApproverConfig_ApproverConfigIdAndIsActiveTrueOrderByApproverSequenceAsc(Long configId);

    List<RequestApprover> findByApproverConfig_ApproverConfigId(Long configId);
}