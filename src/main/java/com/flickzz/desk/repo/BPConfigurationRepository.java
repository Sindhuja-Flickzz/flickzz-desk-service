package com.flickzz.desk.repo;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import com.flickzz.desk.model.*;

@Repository
public interface BPConfigurationRepository extends JpaRepository<BPConfiguration, Long> {

	Optional<BPConfiguration> findByBusinessPartnerBusinessPartnerIdAndIsActiveTrue(Long businessPartnerId);
}