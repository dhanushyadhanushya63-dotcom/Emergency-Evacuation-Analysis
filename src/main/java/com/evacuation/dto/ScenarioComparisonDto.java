package com.evacuation.dto;

import java.util.List;

public class ScenarioComparisonDto {
    private List<ScenarioComparisonItem> scenarios;
    private String comparisonSummary;
    private String mostOptimalScenario;
    private String highestRiskScenario;

    public ScenarioComparisonDto() {}

    public static class ScenarioComparisonItem {
        private Long scenarioId;
        private String scenarioName;
        private String scenarioType;
        private Double totalEvacuationTime;
        private Double averageEvacuationTime;
        private Integer totalOccupants;
        private Integer safelyEvacuated;
        private Integer trappedOccupants;
        private Integer availableExits;
        private Integer blockedCorridors;
        private Double peakCongestionRatio;
        private String overallRiskLevel;
        private Integer highRiskCorridorsCount;

        public ScenarioComparisonItem() {}

        public Long getScenarioId() { return scenarioId; }
        public void setScenarioId(Long scenarioId) { this.scenarioId = scenarioId; }

        public String getScenarioName() { return scenarioName; }
        public void setScenarioName(String scenarioName) { this.scenarioName = scenarioName; }

        public String getScenarioType() { return scenarioType; }
        public void setScenarioType(String scenarioType) { this.scenarioType = scenarioType; }

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

        public Integer getAvailableExits() { return availableExits; }
        public void setAvailableExits(Integer availableExits) { this.availableExits = availableExits; }

        public Integer getBlockedCorridors() { return blockedCorridors; }
        public void setBlockedCorridors(Integer blockedCorridors) { this.blockedCorridors = blockedCorridors; }

        public Double getPeakCongestionRatio() { return peakCongestionRatio; }
        public void setPeakCongestionRatio(Double peakCongestionRatio) { this.peakCongestionRatio = peakCongestionRatio; }

        public String getOverallRiskLevel() { return overallRiskLevel; }
        public void setOverallRiskLevel(String overallRiskLevel) { this.overallRiskLevel = overallRiskLevel; }

        public Integer getHighRiskCorridorsCount() { return highRiskCorridorsCount; }
        public void setHighRiskCorridorsCount(Integer highRiskCorridorsCount) { this.highRiskCorridorsCount = highRiskCorridorsCount; }
    }

    public List<ScenarioComparisonItem> getScenarios() { return scenarios; }
    public void setScenarios(List<ScenarioComparisonItem> scenarios) { this.scenarios = scenarios; }

    public String getComparisonSummary() { return comparisonSummary; }
    public void setComparisonSummary(String comparisonSummary) { this.comparisonSummary = comparisonSummary; }

    public String getMostOptimalScenario() { return mostOptimalScenario; }
    public void setMostOptimalScenario(String mostOptimalScenario) { this.mostOptimalScenario = mostOptimalScenario; }

    public String getHighestRiskScenario() { return highestRiskScenario; }
    public void setHighestRiskScenario(String highestRiskScenario) { this.highestRiskScenario = highestRiskScenario; }
}
