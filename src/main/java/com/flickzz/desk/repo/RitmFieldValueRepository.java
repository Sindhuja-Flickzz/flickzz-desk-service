package com.flickzz.desk.repo;

import com.flickzz.desk.model.RitmFieldValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RitmFieldValueRepository extends JpaRepository<RitmFieldValue, Long> {

    void deleteByRitmRitmId(Long ritmId);
}