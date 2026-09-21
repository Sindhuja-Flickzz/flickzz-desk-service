package com.flickzz.desk.repo;

import com.flickzz.desk.model.BPSupportGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BPSupportGroupRepository extends JpaRepository<BPSupportGroup, Long> {

    List<BPSupportGroup> findByConfigurationConfigurationId(Long configurationId);

    List<BPSupportGroup> findByConfigurationConfigurationIdAndIsActiveTrue(Long configurationId);

    Optional<BPSupportGroup> findBySupportGroupIdAndIsActive(Long supportGroupId, Boolean active);

    boolean existsByConfigurationConfigurationIdAndGroupNameAndIsActive(Long configurationId, String groupName,
                                                                        Boolean active);
}
