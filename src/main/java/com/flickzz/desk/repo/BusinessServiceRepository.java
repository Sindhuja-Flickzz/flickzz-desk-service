package com.flickzz.desk.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.BusinessService;

public interface BusinessServiceRepository extends JpaRepository<BusinessService, Long> {

	Optional<BusinessService> findByServiceName(String serviceName);

	Optional<BusinessService> findByServiceId(Long serviceId);

}
