package com.flickzz.desk.repo;

import com.flickzz.desk.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, Long> {

    Optional<Template> findByTemplateNameAndWorkItem_ItemIdAndCompany_CompanyId(String templateName, Long workItemId, Long companyId);

    Optional<Template> findByTemplateIdAndCompany_CompanyId(Long templateId, Long companyId);

    List<Template> findByCompany_CompanyIdAndIsActive(Long companyId, Boolean isActive);

    Optional<Template> findByTemplateId(Long templateId);

    List<Template> findByCompany_CompanyIdAndWorkItem_ItemIdAndIsActiveTrue(Long orgId, Long itemId);
}
