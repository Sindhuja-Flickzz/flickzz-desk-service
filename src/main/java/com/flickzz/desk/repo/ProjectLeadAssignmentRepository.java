package com.flickzz.desk.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flickzz.desk.model.ProjectLeadAssignment;

@Repository
public interface ProjectLeadAssignmentRepository extends JpaRepository<ProjectLeadAssignment, Long> {
}