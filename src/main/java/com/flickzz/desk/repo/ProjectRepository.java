package com.flickzz.desk.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flickzz.desk.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
}
