package com.flickzz.desk.repo;

import com.flickzz.desk.model.RequestApproverConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestApproverConfigRepository extends JpaRepository<RequestApproverConfig, Long> {

    Optional<RequestApproverConfig> findByApproverCodeAndCompany_CompanyIdAndIsActiveTrue(String approverCode,
                                                                                           Long companyId);

    List<RequestApproverConfig> findByCompany_CompanyIdAndIsActiveTrue(Long companyId);
}