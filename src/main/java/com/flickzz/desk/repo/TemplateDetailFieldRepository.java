package com.flickzz.desk.repo;

import java.util.*;

import org.springframework.data.jpa.repository.*;

import com.flickzz.desk.model.*;

public interface TemplateDetailFieldRepository extends JpaRepository<TemplateDetailField, Long> {
	List<TemplateDetailField> findByTemplateTemplateIdOrderByFieldSequence(Long templateId);

	Optional<TemplateDetailField> findByTemplateTemplateIdAndFieldName(Long templateId, String fieldName);

	void deleteByTemplateTemplateId(Long templateId);
}
