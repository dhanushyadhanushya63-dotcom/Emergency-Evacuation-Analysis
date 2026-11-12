package com.evacuation.repository;

import com.evacuation.entity.BlockedPassage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockedPassageRepository extends JpaRepository<BlockedPassage, Long> {
    List<BlockedPassage> findByScenarioId(Long scenarioId);
    void deleteByScenarioId(Long scenarioId);
}
