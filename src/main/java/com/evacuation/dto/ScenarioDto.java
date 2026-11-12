package com.evacuation.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ScenarioDto {
    private Long id;
    private String name;
    private String description;
    private String scenarioType;
    private Double walkingSpeed;
    private Double crowdDensityMultiplier;
    private Integer reactionDelaySeconds;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private List<BlockedPassageDto> blockedPassages;

    public ScenarioDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getScenarioType() { return scenarioType; }
    public void setScenarioType(String scenarioType) { this.scenarioType = scenarioType; }

    public Double getWalkingSpeed() { return walkingSpeed; }
    public void setWalkingSpeed(Double walkingSpeed) { this.walkingSpeed = walkingSpeed; }

    public Double getCrowdDensityMultiplier() { return crowdDensityMultiplier; }
    public void setCrowdDensityMultiplier(Double crowdDensityMultiplier) { this.crowdDensityMultiplier = crowdDensityMultiplier; }

    public Integer getReactionDelaySeconds() { return reactionDelaySeconds; }
    public void setReactionDelaySeconds(Integer reactionDelaySeconds) { this.reactionDelaySeconds = reactionDelaySeconds; }

    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<BlockedPassageDto> getBlockedPassages() { return blockedPassages; }
    public void setBlockedPassages(List<BlockedPassageDto> blockedPassages) { this.blockedPassages = blockedPassages; }
}
