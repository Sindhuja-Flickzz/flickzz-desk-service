package com.flickzz.desk.repo;

import com.flickzz.desk.model.AgentPlantMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AgentPlantMappingRepository extends JpaRepository<AgentPlantMapping, Long> {
    List<AgentPlantMapping> findByPlant_PlantId(Long plantId);
    List<AgentPlantMapping> findByAgent_AgentId(Long agentId);
}