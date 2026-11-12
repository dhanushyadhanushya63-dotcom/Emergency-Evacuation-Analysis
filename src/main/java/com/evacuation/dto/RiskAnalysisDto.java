package com.evacuation.dto;

import java.util.List;
import java.util.Map;

public class RiskAnalysisDto {
    private Long scenarioId;
    private String scenarioName;
    private String overallRiskLevel; // LOW, MODERATE, HIGH
    private Double overallCongestionIndex;
    private Integer totalOccupants;
    private Integer trappedOccupants;
    private Integer affectedRoomsCount;
    private Integer blockedCorridorsCount;
    private Integer availableExitsCount;
    private Double maxEvacuationTimeSeconds;
    private Double avgEvacuationTimeSeconds;

    // Detailed metrics
    private List<CorridorRiskMetric> corridorRisks;
    private List<RoomRiskMetric> roomRisks;
    private Map<String, ExitRiskMetric> exitRisks;
    private List<String> highRiskRecommendations;
    private String educationalDisclaimer;

    public RiskAnalysisDto() {}

    public static class CorridorRiskMetric {
        private Long corridorId;
        private String corridorCode;
        private String name;
        private Double lengthMeters;
        private Double widthMeters;
        private Integer maxCapacity;
        private Integer totalOccupantsRouted;
        private Double congestionRatio;
        private String riskCategory; // LOW, MODERATE, HIGH
        private Boolean isBlocked;

        public CorridorRiskMetric() {}

        public Long getCorridorId() { return corridorId; }
        public void setCorridorId(Long corridorId) { this.corridorId = corridorId; }

        public String getCorridorCode() { return corridorCode; }
        public void setCorridorCode(String corridorCode) { this.corridorCode = corridorCode; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Double getLengthMeters() { return lengthMeters; }
        public void setLengthMeters(Double lengthMeters) { this.lengthMeters = lengthMeters; }

        public Double getWidthMeters() { return widthMeters; }
        public void setWidthMeters(Double widthMeters) { this.widthMeters = widthMeters; }

        public Integer getMaxCapacity() { return maxCapacity; }
        public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }

        public Integer getTotalOccupantsRouted() { return totalOccupantsRouted; }
        public void setTotalOccupantsRouted(Integer totalOccupantsRouted) { this.totalOccupantsRouted = totalOccupantsRouted; }

        public Double getCongestionRatio() { return congestionRatio; }
        public void setCongestionRatio(Double congestionRatio) { this.congestionRatio = congestionRatio; }

        public String getRiskCategory() { return riskCategory; }
        public void setRiskCategory(String riskCategory) { this.riskCategory = riskCategory; }

        public Boolean getIsBlocked() { return isBlocked; }
        public void setIsBlocked(Boolean blocked) { isBlocked = blocked; }
    }

    public static class RoomRiskMetric {
        private Long roomId;
        private String roomCode;
        private String name;
        private Integer occupants;
        private String assignedExit;
        private Double estimatedTimeSeconds;
        private String riskCategory; // LOW, MODERATE, HIGH
        private Boolean isTrapped;

        public RoomRiskMetric() {}

        public Long getRoomId() { return roomId; }
        public void setRoomId(Long roomId) { this.roomId = roomId; }

        public String getRoomCode() { return roomCode; }
        public void setRoomCode(String roomCode) { this.roomCode = roomCode; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Integer getOccupants() { return occupants; }
        public void setOccupants(Integer occupants) { this.occupants = occupants; }

        public String getAssignedExit() { return assignedExit; }
        public void setAssignedExit(String assignedExit) { this.assignedExit = assignedExit; }

        public Double getEstimatedTimeSeconds() { return estimatedTimeSeconds; }
        public void setEstimatedTimeSeconds(Double estimatedTimeSeconds) { this.estimatedTimeSeconds = estimatedTimeSeconds; }

        public String getRiskCategory() { return riskCategory; }
        public void setRiskCategory(String riskCategory) { this.riskCategory = riskCategory; }

        public Boolean getIsTrapped() { return isTrapped; }
        public void setIsTrapped(Boolean trapped) { isTrapped = trapped; }
    }

    public static class ExitRiskMetric {
        private String exitCode;
        private String name;
        private Integer flowRateCapacity;
        private Integer assignedEvacuees;
        private Double utilizationPercentage;
        private Boolean isAvailable;

        public ExitRiskMetric() {}

        public String getExitCode() { return exitCode; }
        public void setExitCode(String exitCode) { this.exitCode = exitCode; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Integer getFlowRateCapacity() { return flowRateCapacity; }
        public void setFlowRateCapacity(Integer flowRateCapacity) { this.flowRateCapacity = flowRateCapacity; }

        public Integer getAssignedEvacuees() { return assignedEvacuees; }
        public void setAssignedEvacuees(Integer assignedEvacuees) { this.assignedEvacuees = assignedEvacuees; }

        public Double getUtilizationPercentage() { return utilizationPercentage; }
        public void setUtilizationPercentage(Double utilizationPercentage) { this.utilizationPercentage = utilizationPercentage; }

        public Boolean getIsAvailable() { return isAvailable; }
        public void setIsAvailable(Boolean available) { isAvailable = available; }
    }

    public Long getScenarioId() { return scenarioId; }
    public void setScenarioId(Long scenarioId) { this.scenarioId = scenarioId; }

    public String getScenarioName() { return scenarioName; }
    public void setScenarioName(String scenarioName) { this.scenarioName = scenarioName; }

    public String getOverallRiskLevel() { return overallRiskLevel; }
    public void setOverallRiskLevel(String overallRiskLevel) { this.overallRiskLevel = overallRiskLevel; }

    public Double getOverallCongestionIndex() { return overallCongestionIndex; }
    public void setOverallCongestionIndex(Double overallCongestionIndex) { this.overallCongestionIndex = overallCongestionIndex; }

    public Integer getTotalOccupants() { return totalOccupants; }
    public void setTotalOccupants(Integer totalOccupants) { this.totalOccupants = totalOccupants; }

    public Integer getTrappedOccupants() { return trappedOccupants; }
    public void setTrappedOccupants(Integer trappedOccupants) { this.trappedOccupants = trappedOccupants; }

    public Integer getAffectedRoomsCount() { return affectedRoomsCount; }
    public void setAffectedRoomsCount(Integer affectedRoomsCount) { this.affectedRoomsCount = affectedRoomsCount; }

    public Integer getBlockedCorridorsCount() { return blockedCorridorsCount; }
    public void setBlockedCorridorsCount(Integer blockedCorridorsCount) { this.blockedCorridorsCount = blockedCorridorsCount; }

    public Integer getAvailableExitsCount() { return availableExitsCount; }
    public void setAvailableExitsCount(Integer availableExitsCount) { this.availableExitsCount = availableExitsCount; }

    public Double getMaxEvacuationTimeSeconds() { return maxEvacuationTimeSeconds; }
    public void setMaxEvacuationTimeSeconds(Double maxEvacuationTimeSeconds) { this.maxEvacuationTimeSeconds = maxEvacuationTimeSeconds; }

    public Double getAvgEvacuationTimeSeconds() { return avgEvacuationTimeSeconds; }
    public void setAvgEvacuationTimeSeconds(Double avgEvacuationTimeSeconds) { this.avgEvacuationTimeSeconds = avgEvacuationTimeSeconds; }

    public List<CorridorRiskMetric> getCorridorRisks() { return corridorRisks; }
    public void setCorridorRisks(List<CorridorRiskMetric> corridorRisks) { this.corridorRisks = corridorRisks; }

    public List<RoomRiskMetric> getRoomRisks() { return roomRisks; }
    public void setRoomRisks(List<RoomRiskMetric> roomRisks) { this.roomRisks = roomRisks; }

    public Map<String, ExitRiskMetric> getExitRisks() { return exitRisks; }
    public void setExitRisks(Map<String, ExitRiskMetric> exitRisks) { this.exitRisks = exitRisks; }

    public List<String> getHighRiskRecommendations() { return highRiskRecommendations; }
    public void setHighRiskRecommendations(List<String> highRiskRecommendations) { this.highRiskRecommendations = highRiskRecommendations; }

    public String getEducationalDisclaimer() { return educationalDisclaimer; }
    public void setEducationalDisclaimer(String educationalDisclaimer) { this.educationalDisclaimer = educationalDisclaimer; }
}
