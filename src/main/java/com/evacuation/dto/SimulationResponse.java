package com.evacuation.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SimulationResponse {
    private Long id;
    private Long scenarioId;
    private String scenarioName;
    private Double totalEvacuationTime; // in seconds
    private Double averageEvacuationTime; // in seconds
    private Integer totalOccupants;
    private Integer safelyEvacuated;
    private Integer trappedOccupants;
    private Double peakCongestionRatio;
    private String overallRiskLevel; // LOW, MODERATE, HIGH
    private LocalDateTime simulatedAt;
    private List<EvacuationRouteDto> routes;
    private Map<String, Integer> exitUtilization; // exitCode -> evacuee count
    private Map<String, Double> corridorCongestion; // corridorCode -> congestion ratio
    private List<String> bottleneckCorridors;
    private String safetyNotice;

    public SimulationResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getScenarioId() { return scenarioId; }
    public void setScenarioId(Long scenarioId) { this.scenarioId = scenarioId; }

    public String getScenarioName() { return scenarioName; }
    public void setScenarioName(String scenarioName) { this.scenarioName = scenarioName; }

    public Double getTotalEvacuationTime() { return totalEvacuationTime; }
    public void setTotalEvacuationTime(Double totalEvacuationTime) { this.totalEvacuationTime = totalEvacuationTime; }

    public Double getAverageEvacuationTime() { return averageEvacuationTime; }
    public void setAverageEvacuationTime(Double averageEvacuationTime) { this.averageEvacuationTime = averageEvacuationTime; }

    public Integer getTotalOccupants() { return totalOccupants; }
    public void setTotalOccupants(Integer totalOccupants) { this.totalOccupants = totalOccupants; }

    public Integer getSafelyEvacuated() { return safelyEvacuated; }
    public void setSafelyEvacuated(Integer safelyEvacuated) { this.safelyEvacuated = safelyEvacuated; }

    public Integer getTrappedOccupants() { return trappedOccupants; }
    public void setTrappedOccupants(Integer trappedOccupants) { this.trappedOccupants = trappedOccupants; }

    public Double getPeakCongestionRatio() { return peakCongestionRatio; }
    public void setPeakCongestionRatio(Double peakCongestionRatio) { this.peakCongestionRatio = peakCongestionRatio; }

    public String getOverallRiskLevel() { return overallRiskLevel; }
    public void setOverallRiskLevel(String overallRiskLevel) { this.overallRiskLevel = overallRiskLevel; }

    public LocalDateTime getSimulatedAt() { return simulatedAt; }
    public void setSimulatedAt(LocalDateTime simulatedAt) { this.simulatedAt = simulatedAt; }

    public List<EvacuationRouteDto> getRoutes() { return routes; }
    public void setRoutes(List<EvacuationRouteDto> routes) { this.routes = routes; }

    public Map<String, Integer> getExitUtilization() { return exitUtilization; }
    public void setExitUtilization(Map<String, Integer> exitUtilization) { this.exitUtilization = exitUtilization; }

    public Map<String, Double> getCorridorCongestion() { return corridorCongestion; }
    public void setCorridorCongestion(Map<String, Double> corridorCongestion) { this.corridorCongestion = corridorCongestion; }

    public List<String> getBottleneckCorridors() { return bottleneckCorridors; }
    public void setBottleneckCorridors(List<String> bottleneckCorridors) { this.bottleneckCorridors = bottleneckCorridors; }

    public String getSafetyNotice() { return safetyNotice; }
    public void setSafetyNotice(String safetyNotice) { this.safetyNotice = safetyNotice; }
}
