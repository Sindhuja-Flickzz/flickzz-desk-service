package com.flickzz.desk.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flickzz.desk.model.SubTask;

@Repository
public interface SubTaskRepository extends JpaRepository<SubTask, Long> {
}