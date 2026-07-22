package com.flickzz.desk.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.BPSubCategory;

public interface BPSubCategoryRepository extends JpaRepository<BPSubCategory, Long> {

	List<BPSubCategory> findByCategoryCategoryIdAndIsActive(Long categoryId, Boolean active);

	Optional<BPSubCategory> findBySubCategoryIdAndIsActive(Long subCategoryId, Boolean active);

	boolean existsByCategoryCategoryIdAndSubCategoryNameAndIsActive(Long categoryId, String subCategoryName,
			Boolean active);
}
