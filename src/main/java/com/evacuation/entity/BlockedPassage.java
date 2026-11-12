package com.evacuation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "blocked_passages")
public class BlockedPassage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", nullable = false)
    @JsonIgnore
    private Scenario scenario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "corridor_id")
    private Corridor corridor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exit_id")
    private Exit exit;

    @Column(name = "blockage_type", nullable = false, length = 50)
    private String blockageType; // CORRIDOR, EXIT

    @Column(length = 255)
    private String reason;

    public BlockedPassage() {}

    public BlockedPassage(Scenario scenario, Corridor corridor, Exit exit, String blockageType, String reason) {
        this.scenario = scenario;
        this.corridor = corridor;
        this.exit = exit;
        this.blockageType = blockageType;
        this.reason = reason;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Scenario getScenario() { return scenario; }
    public void setScenario(Scenario scenario) { this.scenario = scenario; }

    public Corridor getCorridor() { return corridor; }
    public void setCorridor(Corridor corridor) { this.corridor = corridor; }

    public Exit getExit() { return exit; }
    public void setExit(Exit exit) { this.exit = exit; }

    public String getBlockageType() { return blockageType; }
    public void setBlockageType(String blockageType) { this.blockageType = blockageType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
