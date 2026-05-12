package com.flickzz.desk.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flickzz.desk.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}