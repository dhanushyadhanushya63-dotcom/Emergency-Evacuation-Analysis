package com.evacuation.repository;

import com.evacuation.entity.Corridor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CorridorRepository extends JpaRepository<Corridor, Long> {
    List<Corridor> findByFloorId(Long floorId);
    Optional<Corridor> findByCorridorCode(String corridorCode);
}
