package com.flickzz.desk.repo;

import com.flickzz.desk.model.StatusMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatusMasterRepository extends JpaRepository<StatusMaster, Long> {

    boolean existsByCompanyCompanyIdAndStatusCode(Long companyId, String statusCode);

    boolean existsByCompanyCompanyIdAndSequenceNo(Long companyId, Integer sequenceNo);

    boolean existsByCompany_CompanyIdAndWorkItem_ItemIdAndStatusCode(Long companyId, Long workItemId, String statusCode);

    boolean existsByCompany_CompanyIdAndWorkItem_ItemIdAndSequenceNo(Long companyId, Long workItemId, Integer sequenceNo);

    List<StatusMaster> findByCompany_CompanyIdAndWorkItem_ItemIdAndStatusCodeIn(
            Long companyId, Long workItemId, List<String> statusCodes);

    List<StatusMaster> findByCompanyCompanyIdAndIsActiveTrue(Long orgId);

    Optional<StatusMaster> findFirstByCompanyCompanyIdAndIsActiveTrueOrderBySequenceNoAsc(Long companyId);

    Optional<StatusMaster> findFirstByCompany_CompanyIdAndSequenceNoGreaterThanAndIsActiveTrueOrderBySequenceNoAsc(Long companyId, Integer currentSequenceNo);

    Optional<StatusMaster> findFirstByCompany_CompanyIdAndSequenceNoAndIsActiveTrue(Long companyId, Integer sequenceNo);

    List<StatusMaster> findByCompanyCompanyId(Long orgId);
}