package com.flickzz.desk.repo;

import com.flickzz.desk.model.RitmStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RitmStatusRepository extends JpaRepository<RitmStatus, Long> {

    boolean existsByCompanyCompanyIdAndStatusCode(Long companyId, String statusCode);

    boolean existsByCompanyCompanyIdAndSequenceNo(Long companyId, Integer sequenceNo);

    List<RitmStatus> findByCompanyCompanyIdAndIsActiveTrue(Long orgId);

    Optional<RitmStatus> findFirstByCompanyCompanyIdAndIsActiveTrueOrderBySequenceNoAsc(Long companyId);

    Optional<RitmStatus> findFirstByCompany_CompanyIdAndSequenceNoGreaterThanAndIsActiveTrueOrderBySequenceNoAsc(Long companyId, Integer currentSequenceNo);
}