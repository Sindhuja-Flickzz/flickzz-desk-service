package com.flickzz.desk.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.SkillMaster;

public interface SkillMasterRepository extends JpaRepository<SkillMaster, Long> {

	Optional<SkillMaster> findBySkillName(String skillName);

	Optional<SkillMaster> findBySkillId(Long skillId);
    
}
