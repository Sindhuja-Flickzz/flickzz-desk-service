package com.flickzz.desk.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.BusinessService;
import com.flickzz.desk.model.ServiceOffering;

public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {

	void deleteAllByBusinessService(BusinessService saved);

}
