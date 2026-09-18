package com.flickzz.desk.repo;

import com.flickzz.desk.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TemplateDetailsRepository extends JpaRepository<Template, Long> {

    Optional<Template> findByTemplateNameAndCompany_CompanyId(String templateName, Long companyId);

    Optional<Template> findByTemplateIdAndCompany_CompanyId(Long templateId, Long companyId);

    List<Template> findByCompany_CompanyIdAndIsActive(Long companyId, Boolean isActive);

    List<Template> findByCompany_CompanyId(Long companyId);

    List<Template> findByWorkItem_ItemId(Long workItemId);

    Optional<Template> findByTemplateId(Long templateId);

    List<Template> findByCompany_CompanyIdAndWorkItem_ItemIdAndIsActiveTrue(Long orgId, Long itemId);
}
