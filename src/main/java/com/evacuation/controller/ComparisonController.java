package com.evacuation.controller;

import com.evacuation.dto.ScenarioComparisonDto;
import com.evacuation.service.ScenarioComparisonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comparison")
@CrossOrigin(origins = "*")
public class ComparisonController {

    private final ScenarioComparisonService scenarioComparisonService;

    public ComparisonController(ScenarioComparisonService scenarioComparisonService) {
        this.scenarioComparisonService = scenarioComparisonService;
    }

    @GetMapping
    public ResponseEntity<ScenarioComparisonDto> compareScenarios(
            @RequestParam(required = false) String scenarioIds) {

        List<Long> ids = null;
        if (scenarioIds != null && !scenarioIds.isBlank()) {
            ids = Arrays.stream(scenarioIds.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(scenarioComparisonService.compareScenarios(ids));
    }
}
