package com.evacuation.controller;

import com.evacuation.dto.RiskAnalysisDto;
import com.evacuation.service.RiskAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/risk-analysis")
@CrossOrigin(origins = "*")
public class RiskAnalysisController {

    private final RiskAnalysisService riskAnalysisService;

    public RiskAnalysisController(RiskAnalysisService riskAnalysisService) {
        this.riskAnalysisService = riskAnalysisService;
    }

    @GetMapping("/{scenarioId}")
    public ResponseEntity<RiskAnalysisDto> getScenarioRisk(@PathVariable Long scenarioId) {
        return ResponseEntity.ok(riskAnalysisService.analyzeScenarioRisk(scenarioId));
    }
}
