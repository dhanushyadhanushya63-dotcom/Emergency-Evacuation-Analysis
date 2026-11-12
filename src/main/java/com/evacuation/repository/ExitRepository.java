package com.evacuation.repository;

import com.evacuation.entity.Exit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExitRepository extends JpaRepository<Exit, Long> {
    List<Exit> findByFloorId(Long floorId);
    Optional<Exit> findByExitCode(String exitCode);
}
