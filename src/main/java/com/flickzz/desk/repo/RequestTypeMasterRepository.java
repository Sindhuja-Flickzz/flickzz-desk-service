package com.flickzz.desk.repo;

import com.flickzz.desk.model.RequestTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestTypeMasterRepository extends JpaRepository<RequestTypeMaster, Long> {

    Optional<RequestTypeMaster> findByRequestTypeNameAndCompany_CompanyId(String requestTypeName, Long companyId);

    List<RequestTypeMaster> findByCompany_CompanyIdAndIsActiveTrueOrderByRequestTypeNameAsc(Long companyId);
}