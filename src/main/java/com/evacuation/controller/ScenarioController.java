package com.evacuation.controller;

import com.evacuation.dto.ScenarioDto;
import com.evacuation.service.ScenarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scenarios")
@CrossOrigin(origins = "*")
public class ScenarioController {

    private final ScenarioService scenarioService;

    public ScenarioController(ScenarioService scenarioService) {
        this.scenarioService = scenarioService;
    }

    @GetMapping
    public ResponseEntity<List<ScenarioDto>> getAllScenarios() {
        return ResponseEntity.ok(scenarioService.getAllScenarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScenarioDto> getScenarioById(@PathVariable Long id) {
        return ResponseEntity.ok(scenarioService.getScenarioById(id));
    }

    @PostMapping
    public ResponseEntity<ScenarioDto> createScenario(@RequestBody ScenarioDto dto) {
        return ResponseEntity.ok(scenarioService.createScenario(dto));
    }

    @PostMapping("/{id}/toggle-corridor/{corridorId}")
    public ResponseEntity<ScenarioDto> toggleCorridor(
            @PathVariable Long id,
            @PathVariable Long corridorId,
            @RequestBody(required = false) Map<String, String> payload) {
        String reason = payload != null ? payload.get("reason") : "Manual toggle";
        return ResponseEntity.ok(scenarioService.toggleCorridorBlock(id, corridorId, reason));
    }

    @PostMapping("/{id}/toggle-exit/{exitId}")
    public ResponseEntity<ScenarioDto> toggleExit(
            @PathVariable Long id,
            @PathVariable Long exitId,
            @RequestBody(required = false) Map<String, String> payload) {
        String reason = payload != null ? payload.get("reason") : "Manual toggle";
        return ResponseEntity.ok(scenarioService.toggleExitAvailability(id, exitId, reason));
    }
}
