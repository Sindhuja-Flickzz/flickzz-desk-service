package com.flickzz.desk.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.RequestConfig;

public interface RequestConfigRepository extends JpaRepository<RequestConfig, Long> {

	Optional<RequestConfig> findByRequestTypeAndPlant_PlantId(String requestType, Long plantId);

}
