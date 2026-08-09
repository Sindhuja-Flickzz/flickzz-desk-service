package com.flickzz.desk.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.UserLanguageMapping;
import com.flickzz.desk.model.User;
import java.util.List;

public interface UserLanguageMappingRepository extends JpaRepository<UserLanguageMapping, Long> {

    List<UserLanguageMapping> findByUserUserIdAndIsActiveTrue(Long userId);

    void deleteByUser(User user);
}
