package com.flickzz.desk.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.CompanyApprover;

public interface CompanyApproverRepository extends JpaRepository<CompanyApprover, Long> {
}
