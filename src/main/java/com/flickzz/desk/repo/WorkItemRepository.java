package com.flickzz.desk.repo;

import com.flickzz.desk.model.WorkItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkItemRepository extends JpaRepository<WorkItem, Long> {

    List<WorkItem> findByIsActive(Boolean active);

    Optional<WorkItem> findByCodeAndIsActiveTrue(String item);
}