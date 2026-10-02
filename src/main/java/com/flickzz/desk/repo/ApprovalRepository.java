package com.flickzz.desk.repo;

import com.flickzz.desk.model.ApprovalMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalRepository extends JpaRepository<ApprovalMaster, Long> {
    
    List<ApprovalMaster> findByApproverUserId(Long approverUserId);

    List<ApprovalMaster> findByRequestTypeAndRequestIdIn(String requestType, List<Long> requestIds);

    List<ApprovalMaster> findByRequestTypeAndRequestId(String requestType, Long requestId);
}
