package com.evacuation.repository;

import com.evacuation.entity.SimulationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SimulationResultRepository extends JpaRepository<SimulationResult, Long> {
    List<SimulationResult> findByScenarioIdOrderBySimulatedAtDesc(Long scenarioId);
    Optional<SimulationResult> findFirstByScenarioIdOrderBySimulatedAtDesc(Long scenarioId);
}
