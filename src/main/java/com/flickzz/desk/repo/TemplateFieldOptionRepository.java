package com.flickzz.desk.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.TemplateFieldOption;

public interface TemplateFieldOptionRepository extends JpaRepository<TemplateFieldOption, Long> {
	List<TemplateFieldOption> findByFieldFieldIdOrderByOptionSequence(Long fieldId);

	void deleteByFieldFieldId(Long fieldId);
}
