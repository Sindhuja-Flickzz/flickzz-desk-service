package com.flickzz.desk.repo;

import com.flickzz.desk.model.CompanyMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.CompanyApprover;

import java.util.List;

public interface CompanyApproverRepository extends JpaRepository<CompanyApprover, Long> {

    List<CompanyApprover> findByCompany_CompanyIdAndIsActiveTrue(CompanyMaster requestedByOrg);
}
