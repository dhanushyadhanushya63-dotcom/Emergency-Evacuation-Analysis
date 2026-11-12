package com.evacuation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "simulation_results")
public class SimulationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "scenario_id", nullable = false)
    private Scenario scenario;

    @Column(name = "total_evacuation_time", nullable = false)
    private Double totalEvacuationTime; // seconds

    @Column(name = "average_evacuation_time", nullable = false)
    private Double averageEvacuationTime; // seconds

    @Column(name = "total_occupants", nullable = false)
    private Integer totalOccupants;

    @Column(name = "safely_evacuated", nullable = false)
    private Integer safelyEvacuated;

    @Column(name = "trapped_occupants", nullable = false)
    private Integer trappedOccupants = 0;

    @Column(name = "peak_congestion_ratio", nullable = false)
    private Double peakCongestionRatio = 0.0;

    @Column(name = "overall_risk_level", nullable = false, length = 50)
    private String overallRiskLevel = "LOW"; // LOW, MODERATE, HIGH

    @Column(name = "simulated_at")
    private LocalDateTime simulatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "simulationResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvacuationRoute> evacuationRoutes = new ArrayList<>();

    public SimulationResult() {}

    public SimulationResult(Scenario scenario, Double totalEvacuationTime, Double averageEvacuationTime,
                            Integer totalOccupants, Integer safelyEvacuated, Integer trappedOccupants,
                            Double peakCongestionRatio, String overallRiskLevel) {
        this.scenario = scenario;
        this.totalEvacuationTime = totalEvacuationTime;
        this.averageEvacuationTime = averageEvacuationTime;
        this.totalOccupants = totalOccupants;
        this.safelyEvacuated = safelyEvacuated;
        this.trappedOccupants = trappedOccupants;
        this.peakCongestionRatio = peakCongestionRatio;
        this.overallRiskLevel = overallRiskLevel;
        this.simulatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Scenario getScenario() { return scenario; }
    public void setScenario(Scenario scenario) { this.scenario = scenario; }

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

    public List<EvacuationRoute> getEvacuationRoutes() { return evacuationRoutes; }
    public void setEvacuationRoutes(List<EvacuationRoute> evacuationRoutes) { this.evacuationRoutes = evacuationRoutes; }
}
