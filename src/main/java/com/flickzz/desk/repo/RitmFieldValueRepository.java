package com.flickzz.desk.repo;

import com.flickzz.desk.model.RitmFieldValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RitmFieldValueRepository extends JpaRepository<RitmFieldValue, Long> {

    List<RitmFieldValue> findAllByRitmRitmId(Long ritmId);
}