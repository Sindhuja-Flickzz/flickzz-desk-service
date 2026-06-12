package com.flickzz.desk.repo;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import com.flickzz.desk.model.*;

@Repository
public interface CompanyRoleRepository extends JpaRepository<CompanyRole, Long> {

	List<CompanyRole> findByCompany_CompanyIdAndIsActive(Long valueOf, boolean active);
}