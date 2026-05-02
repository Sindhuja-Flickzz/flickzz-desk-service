package com.flickzz.desk.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.Incident;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

}
