package com.flickzz.desk.repo;

import com.flickzz.desk.model.BPConfigurationChangeRequest;
import com.flickzz.desk.model.BPConfigurationChangeRequestRemark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BPConfigurationChangeRequestRemarkRepository extends JpaRepository<BPConfigurationChangeRequestRemark, Long> {

}
