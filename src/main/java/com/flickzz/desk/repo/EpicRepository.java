package com.flickzz.desk.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flickzz.desk.model.Epic;

@Repository
public interface EpicRepository extends JpaRepository<Epic, Long> {
}