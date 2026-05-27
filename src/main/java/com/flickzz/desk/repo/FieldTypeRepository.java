package com.flickzz.desk.repo;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import com.flickzz.desk.model.*;

@Repository
public interface FieldTypeRepository extends JpaRepository<FieldType, Long> {

	List<FieldType> findByIsActive(Boolean active);
}