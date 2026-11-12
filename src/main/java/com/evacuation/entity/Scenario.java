package com.evacuation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scenarios")
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "scenario_type", nullable = false, length = 80)
    private String scenarioType = "NORMAL";

    @Column(name = "walking_speed", nullable = false)
    private Double walkingSpeed = 1.2; // m/s

    @Column(name = "crowd_density_multiplier", nullable = false)
    private Double crowdDensityMultiplier = 1.0;

    @Column(name = "reaction_delay_seconds", nullable = false)
    private Integer reactionDelaySeconds = 5;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BlockedPassage> blockedPassages = new ArrayList<>();

    public Scenario() {}

    public Scenario(String name, String description, String scenarioType,
                    Double walkingSpeed, Double crowdDensityMultiplier,
                    Integer reactionDelaySeconds, Boolean isDefault) {
        this.name = name;
        this.description = description;
        this.scenarioType = scenarioType;
        this.walkingSpeed = walkingSpeed;
        this.crowdDensityMultiplier = crowdDensityMultiplier;
        this.reactionDelaySeconds = reactionDelaySeconds;
        this.isDefault = isDefault != null ? isDefault : false;
        this.createdAt = LocalDateTime.now();
    }

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

    public List<BlockedPassage> getBlockedPassages() { return blockedPassages; }
    public void setBlockedPassages(List<BlockedPassage> blockedPassages) { this.blockedPassages = blockedPassages; }
}
