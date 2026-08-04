package com.flickzz.desk.repo;

import com.flickzz.desk.model.BPConfigurationChangeRequestRemark;
import com.flickzz.desk.model.ConfigChangeNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigChangeNotificationRepository extends JpaRepository<ConfigChangeNotification, Long> {
}
