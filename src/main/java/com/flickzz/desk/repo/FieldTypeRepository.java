package com.flickzz.desk.repo;

import com.flickzz.desk.model.FieldType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FieldTypeRepository extends JpaRepository<FieldType, Long> {

    List<FieldType> findByIsActiveTrue();

    Optional<FieldType> findByTypeIdAndIsActive(Long typeId, boolean active);
}