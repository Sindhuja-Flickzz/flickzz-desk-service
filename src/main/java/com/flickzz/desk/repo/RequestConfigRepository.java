package com.flickzz.desk.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.RequestConfig;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RequestConfigRepository extends JpaRepository<RequestConfig, Long> {

        Optional<RequestConfig> findByRequestTypeAndCompany_CompanyId(String requestType, Long plantId);

        @Query("""
            SELECT COALESCE(MAX(r.revision), 0)
            FROM RequestConfig r
            WHERE r.company.companyId = :companyId
        """)
        Integer findMaxRevisionByCompanyId(@Param("companyId") Long companyId);

    List<RequestConfig> findByCompany_CompanyId(Long orgId);

    List<RequestConfig> findAllByIsActiveTrue();
}
