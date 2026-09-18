package com.flickzz.desk.repo;

import java.util.*;

import org.springframework.data.jpa.repository.*;

import com.flickzz.desk.model.*;

public interface BPAssignmentRepository extends JpaRepository<BPAssignment, Long> {

	List<BPAssignment> findByConfigurationConfigurationId(Long configurationId);

	List<BPAssignment> findByConfigurationConfigurationIdAndIsActive(Long configurationId, Boolean active);

	Optional<BPAssignment> findByAssignmentIdAndIsActive(Long assignmentId, Boolean active);

	Optional<BPAssignment> findByConfigurationConfigurationIdAndSupportGroupSupportGroupIdAndIsActive(
			Long configurationId, Long supportGroupId, Boolean active);

	Optional<BPAssignment> findByConfigurationConfigurationIdAndSubCategorySubCategoryIdAndIsActive(
			Long configurationId, Long subCategoryId, Boolean active);

	Optional<BPAssignment> findByConfigurationConfigurationIdAndSupportGroupSupportGroupIdAndSubCategorySubCategoryId(
			Long configurationId, Long supportGroupId, Long subCategoryId);

    BPAssignment findBySubCategorySubCategoryIdAndIsActiveTrue(Long subCategoryId);
}
