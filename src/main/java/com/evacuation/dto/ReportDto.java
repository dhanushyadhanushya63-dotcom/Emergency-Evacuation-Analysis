package com.evacuation.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ReportDto {
    private String reportId;
    private LocalDateTime generatedAt;
    private BuildingDto building;
    private ScenarioDto scenario;
    private SimulationResponse simulation;
    private RiskAnalysisDto riskAnalysis;
    private List<BlockedPassageDto> blockedPassages;
    private List<EvacuationRouteDto> evacuationRoutes;
    private String executiveSummary;
    private List<String> strategicRecommendations;
    private String safetyNotice;

    public ReportDto() {}

    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public BuildingDto getBuilding() { return building; }
    public void setBuilding(BuildingDto building) { this.building = building; }

    public ScenarioDto getScenario() { return scenario; }
    public void setScenario(ScenarioDto scenario) { this.scenario = scenario; }

    public SimulationResponse getSimulation() { return simulation; }
    public void setSimulation(SimulationResponse simulation) { this.simulation = simulation; }

    public RiskAnalysisDto getRiskAnalysis() { return riskAnalysis; }
    public void setRiskAnalysis(RiskAnalysisDto riskAnalysis) { this.riskAnalysis = riskAnalysis; }

    public List<BlockedPassageDto> getBlockedPassages() { return blockedPassages; }
    public void setBlockedPassages(List<BlockedPassageDto> blockedPassages) { this.blockedPassages = blockedPassages; }

    public List<EvacuationRouteDto> getEvacuationRoutes() { return evacuationRoutes; }
    public void setEvacuationRoutes(List<EvacuationRouteDto> evacuationRoutes) { this.evacuationRoutes = evacuationRoutes; }

    public String getExecutiveSummary() { return executiveSummary; }
    public void setExecutiveSummary(String executiveSummary) { this.executiveSummary = executiveSummary; }

    public List<String> getStrategicRecommendations() { return strategicRecommendations; }
    public void setStrategicRecommendations(List<String> strategicRecommendations) { this.strategicRecommendations = strategicRecommendations; }

    public String getSafetyNotice() { return safetyNotice; }
    public void setSafetyNotice(String safetyNotice) { this.safetyNotice = safetyNotice; }
}
