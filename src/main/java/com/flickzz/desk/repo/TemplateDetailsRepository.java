package com.flickzz.desk.repo;

import java.util.*;

import org.springframework.data.jpa.repository.*;

import com.flickzz.desk.model.*;

public interface TemplateDetailsRepository extends JpaRepository<TemplateDetails, Long> {

	Optional<TemplateDetails> findByTemplateNameAndCompany_CompanyId(String templateName, Long companyId);

	Optional<TemplateDetails> findByTemplateIdAndCompany_CompanyId(Long templateId, Long companyId);

	List<TemplateDetails> findByCompany_CompanyIdAndIsActive(Long companyId, Boolean isActive);

	List<TemplateDetails> findByCompany_CompanyId(Long companyId);

	List<TemplateDetails> findByWorkItem_ItemId(Long workItemId);

	Optional<TemplateDetails> findByTemplateId(Long templateId);
}
