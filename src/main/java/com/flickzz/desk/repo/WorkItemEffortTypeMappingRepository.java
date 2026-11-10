package com.flickzz.desk.repo;

import com.flickzz.desk.model.WorkItemEffortTypeMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkItemEffortTypeMappingRepository extends JpaRepository<WorkItemEffortTypeMapping, Long> {

    List<WorkItemEffortTypeMapping> findByCompany_CompanyIdAndIsActiveTrueOrderByMappingIdAsc(Long companyId);

    List<WorkItemEffortTypeMapping> findByCompany_CompanyIdAndWorkItem_ItemIdOrderByMappingIdAsc(
            Long companyId, Long workItemId);

    List<WorkItemEffortTypeMapping> findByCompany_CompanyIdAndWorkItem_ItemIdAndIsActiveTrueOrderByMappingIdAsc(
            Long companyId, Long workItemId);

    Optional<WorkItemEffortTypeMapping> findByCompany_CompanyIdAndMappingIdAndIsActiveTrue(
            Long companyId, Long mappingId);

    List<WorkItemEffortTypeMapping> findByCompany_CompanyIdAndWorkItem_ItemIdAndIsActiveTrueAndEffortType_IsActiveTrueOrderByEffortType_SequenceAscEffortType_EffortTypeIdAsc(
            Long companyId, Long workItemId);
}
