package com.evacuation.repository;

import com.evacuation.entity.EvacuationRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvacuationRouteRepository extends JpaRepository<EvacuationRoute, Long> {
    List<EvacuationRoute> findBySimulationResultId(Long simulationResultId);
}
