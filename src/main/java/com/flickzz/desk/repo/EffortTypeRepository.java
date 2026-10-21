package com.flickzz.desk.repo;

import com.flickzz.desk.model.EffortType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EffortTypeRepository extends JpaRepository<EffortType, Long> {

    List<EffortType> findByCompany_CompanyIdAndIsActiveTrueOrderBySequenceAscEffortTypeIdAsc(Long companyId);

    List<EffortType> findByCompany_CompanyIdOrderBySequenceAscEffortTypeIdAsc(Long companyId);

    Optional<EffortType> findByEffortTypeIdAndCompany_CompanyId(Long effortTypeId, Long companyId);
}
