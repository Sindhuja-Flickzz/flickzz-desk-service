package com.flickzz.desk.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flickzz.desk.model.BPConfigurationChangeRequest;

@Repository
public interface BPConfigurationChangeRequestRepository extends JpaRepository<BPConfigurationChangeRequest, Long> {

}
