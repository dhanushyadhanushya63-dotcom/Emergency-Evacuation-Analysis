package com.evacuation.service;

import com.evacuation.dto.ReportDto;
import com.evacuation.dto.RiskAnalysisDto;
import com.evacuation.dto.SimulationResponse;
import com.evacuation.entity.Scenario;
import com.evacuation.exception.ResourceNotFoundException;
import com.evacuation.repository.ScenarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final BuildingService buildingService;
    private final ScenarioService scenarioService;
    private final SimulationService simulationService;
    private final RiskAnalysisService riskAnalysisService;
    private final ScenarioRepository scenarioRepository;

    public ReportService(BuildingService buildingService,
                         ScenarioService scenarioService,
                         SimulationService simulationService,
                         RiskAnalysisService riskAnalysisService,
                         ScenarioRepository scenarioRepository) {
        this.buildingService = buildingService;
        this.scenarioService = scenarioService;
        this.simulationService = simulationService;
        this.riskAnalysisService = riskAnalysisService;
        this.scenarioRepository = scenarioRepository;
    }

    public ReportDto generateReport(Long scenarioId) {
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Scenario not found with id: " + scenarioId));

        SimulationResponse simulation = simulationService.runSimulation(scenarioId);
        RiskAnalysisDto risk = riskAnalysisService.analyzeScenarioRisk(scenarioId);

        ReportDto report = new ReportDto();
        report.setReportId("EVAC-REP-" + DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now())
                + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        report.setGeneratedAt(LocalDateTime.now());
        report.setBuilding(buildingService.getDefaultSampleBuilding());
        report.setScenario(scenarioService.getScenarioById(scenarioId));
        report.setSimulation(simulation);
        report.setRiskAnalysis(risk);
        report.setBlockedPassages(report.getScenario().getBlockedPassages());
        report.setEvacuationRoutes(simulation.getRoutes());

        String summary = String.format(
                "Evacuation simulation for scenario '%s' yielded a total calculated clearance duration of %.1f seconds " +
                        "for %d building occupants across %d designated zones. %d occupants reached exits safely, while %d occupants were identified in obstructed zones. " +
                        "Peak corridor congestion reached an index of %.2f with an overall safety classification of %s.",
                scenario.getName(),
                simulation.getTotalEvacuationTime(),
                simulation.getTotalOccupants(),
                simulation.getRoutes().size(),
                simulation.getSafelyEvacuated(),
                simulation.getTrappedOccupants(),
                simulation.getPeakCongestionRatio(),
                simulation.getOverallRiskLevel()
        );
        report.setExecutiveSummary(summary);
        report.setStrategicRecommendations(risk.getHighRiskRecommendations());
        report.setSafetyNotice("EDUCATIONAL DISCLAIMER: This simulation report is automatically compiled for educational, research, and design evaluation purposes only. Calculated routes, capacities, and evacuation times are mathematical approximations and do not constitute certified real-world emergency directives.");

        return report;
    }
}
