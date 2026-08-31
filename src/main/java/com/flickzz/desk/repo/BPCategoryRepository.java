package com.flickzz.desk.repo;

import com.flickzz.desk.model.BPCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BPCategoryRepository extends JpaRepository<BPCategory, Long> {

    List<BPCategory> findByConfigurationConfigurationId(Long configurationId);

    List<BPCategory> findByConfigurationConfigurationIdAndIsActiveTrue(Long configurationId);

    Optional<BPCategory> findByCategoryIdAndIsActive(Long categoryId, Boolean active);

    boolean existsByConfigurationConfigurationIdAndCategoryNameAndIsActive(Long configurationId, String categoryName,
                                                                           Boolean active);
}
