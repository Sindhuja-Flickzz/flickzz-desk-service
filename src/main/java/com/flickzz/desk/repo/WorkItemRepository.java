package com.flickzz.desk.repo;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import com.flickzz.desk.model.*;

@Repository
public interface WorkItemRepository extends JpaRepository<WorkItem, Long> {

	List<WorkItem> findByIsActive(Boolean active);
}