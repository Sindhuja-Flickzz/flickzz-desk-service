package com.flickzz.desk.repo;

import com.flickzz.desk.model.WorkItemRequestTypeMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkItemRequestTypeMappingRepository
        extends JpaRepository<WorkItemRequestTypeMapping, Long> {

    List<WorkItemRequestTypeMapping> findByCompany_CompanyIdAndIsActiveTrueOrderByMappingIdAsc(Long companyId);

    Optional<WorkItemRequestTypeMapping> findByCompany_CompanyIdAndMappingIdAndIsActiveTrue(
            Long companyId, Long mappingId);

    List<WorkItemRequestTypeMapping> findByCompany_CompanyIdAndWorkItem_ItemIdOrderByMappingIdAsc(
            Long companyId, Long workItemId);

    List<WorkItemRequestTypeMapping> findByCompany_CompanyIdAndWorkItem_ItemIdAndIsActiveTrueOrderByMappingIdAsc(Long companyId, Long workItemId);

    List<WorkItemRequestTypeMapping> findByCompany_CompanyIdAndWorkItem_ItemIdAndIsActiveTrueAndRequestType_IsActiveTrueOrderByRequestType_RequestTypeNameAsc(
            Long companyId, Long workItemId);
}
