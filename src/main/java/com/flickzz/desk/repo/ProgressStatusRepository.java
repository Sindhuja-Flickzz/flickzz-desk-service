package com.flickzz.desk.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flickzz.desk.model.ProgressStatus;

@Repository
public interface ProgressStatusRepository extends JpaRepository<ProgressStatus, Long> {
}
