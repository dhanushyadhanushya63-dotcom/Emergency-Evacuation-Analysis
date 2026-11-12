package com.evacuation.controller;

import com.evacuation.dto.SimulationRequest;
import com.evacuation.dto.SimulationResponse;
import com.evacuation.service.SimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulation")
@CrossOrigin(origins = "*")
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/run")
    public ResponseEntity<SimulationResponse> runSimulation(@RequestBody(required = false) SimulationRequest request) {
        if (request == null || (request.getScenarioId() == null && request.getWalkingSpeed() == null)) {
            // Default baseline run
            return ResponseEntity.ok(simulationService.runSimulation(1L));
        }

        if (request.getScenarioId() != null && request.getWalkingSpeed() == null && request.getCrowdDensityMultiplier() == null) {
            return ResponseEntity.ok(simulationService.runSimulation(request.getScenarioId()));
        }

        return ResponseEntity.ok(simulationService.runCustomSimulation(request));
    }

    @GetMapping("/latest/{scenarioId}")
    public ResponseEntity<SimulationResponse> getLatestResult(@PathVariable Long scenarioId) {
        return ResponseEntity.ok(simulationService.getLatestSimulationResult(scenarioId));
    }
}
