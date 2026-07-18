package com.flickzz.desk.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.BPCategory;

public interface BPCategoryRepository extends JpaRepository<BPCategory, Long> {

	List<BPCategory> findByConfigurationConfigurationIdAndIsActive(Long configurationId, Boolean active);

	Optional<BPCategory> findByCategoryIdAndIsActive(Long categoryId, Boolean active);

	boolean existsByConfigurationConfigurationIdAndCategoryNameAndIsActive(Long configurationId, String categoryName,
			Boolean active);
}
