package com.evacuation.dto;

import java.util.List;

public class SimulationRequest {
    private Long scenarioId;
    private Double walkingSpeed;
    private Double crowdDensityMultiplier;
    private Integer reactionDelaySeconds;
    private List<Long> blockedCorridorIds;
    private List<Long> unavailableExitIds;

    public SimulationRequest() {}

    public Long getScenarioId() { return scenarioId; }
    public void setScenarioId(Long scenarioId) { this.scenarioId = scenarioId; }

    public Double getWalkingSpeed() { return walkingSpeed; }
    public void setWalkingSpeed(Double walkingSpeed) { this.walkingSpeed = walkingSpeed; }

    public Double getCrowdDensityMultiplier() { return crowdDensityMultiplier; }
    public void setCrowdDensityMultiplier(Double crowdDensityMultiplier) { this.crowdDensityMultiplier = crowdDensityMultiplier; }

    public Integer getReactionDelaySeconds() { return reactionDelaySeconds; }
    public void setReactionDelaySeconds(Integer reactionDelaySeconds) { this.reactionDelaySeconds = reactionDelaySeconds; }

    public List<Long> getBlockedCorridorIds() { return blockedCorridorIds; }
    public void setBlockedCorridorIds(List<Long> blockedCorridorIds) { this.blockedCorridorIds = blockedCorridorIds; }

    public List<Long> getUnavailableExitIds() { return unavailableExitIds; }
    public void setUnavailableExitIds(List<Long> unavailableExitIds) { this.unavailableExitIds = unavailableExitIds; }
}
