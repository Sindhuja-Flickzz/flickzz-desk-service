package com.flickzz.desk.repo;

import com.flickzz.desk.model.StatusVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusVisibilityRepository extends JpaRepository<StatusVisibility, Long> {

    List<StatusVisibility> findByCompanyCompanyIdAndWorkItemItemIdAndCurrentStatusStatusId(
            Long companyId, Long workItemId, Long currentStatusId);

    List<StatusVisibility> findByCompanyCompanyIdAndCurrentStatusStatusIdAndIsActiveTrue(Long orgId, Long statusId);
}
