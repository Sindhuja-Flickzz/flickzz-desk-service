package com.flickzz.desk.repo;

import com.flickzz.desk.model.TemplateField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TemplateFieldRepository extends JpaRepository<TemplateField, Long> {

    List<TemplateField> findByTemplateCompanyCompanyIdAndDefaultValueIsNotNullAndIsActiveOrderByTemplateTemplateIdAscFieldSequenceAsc(
            Long companyId, Boolean isActive);

    void deleteByTemplateTemplateId(Long templateId);

    Optional<TemplateField> findByFieldIdAndDefaultValueIsNull(Long fieldId);
}
