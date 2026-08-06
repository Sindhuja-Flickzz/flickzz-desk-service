package com.flickzz.desk.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.BPSupportGroup;

public interface BPSupportGroupRepository extends JpaRepository<BPSupportGroup, Long> {

	List<BPSupportGroup> findByConfigurationConfigurationId(Long configurationId);

	List<BPSupportGroup> findByConfigurationConfigurationIdAndIsActive(Long configurationId, Boolean active);

	Optional<BPSupportGroup> findBySupportGroupIdAndIsActive(Long supportGroupId, Boolean active);

	boolean existsByConfigurationConfigurationIdAndGroupNameAndIsActive(Long configurationId, String groupName,
			Boolean active);
}
