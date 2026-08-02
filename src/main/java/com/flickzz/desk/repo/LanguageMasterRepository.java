package com.flickzz.desk.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.LanguageMaster;

import java.util.List;
import java.util.Optional;

public interface LanguageMasterRepository extends JpaRepository<LanguageMaster, Long> {

    List<LanguageMaster> findAllByIsActiveTrue();

    Optional<LanguageMaster> findByLanguageIdAndIsActiveTrue(Long languageId);
}
