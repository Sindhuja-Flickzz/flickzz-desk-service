package com.flickzz.desk.repo;

import com.flickzz.desk.model.AgentPlantMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AgentPlantMappingRepository extends JpaRepository<AgentPlantMapping, Long> {
    List<AgentPlantMapping> findByPlant_PlantId(Long plantId);
    List<AgentPlantMapping> findByAgent_AgentId(Long agentId);

    Optional<AgentPlantMapping> findByPlant_PlantIdAndAgent_AgentId(Long plantId, Long agentId);

    List<AgentPlantMapping> findByPlant_Company_CompanyIdAndIsActiveTrue(String orgId);
}