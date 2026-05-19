package com.flickzz.desk.repo;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import com.flickzz.desk.model.*;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

	Optional<Project> findByProjectNameAndCompanyCompanyIdAndIsActive(String projectName, Long orgId, Boolean active);

	List<Project> findByCompanyCompanyIdAndIsActive(String orgId, Boolean active);
}