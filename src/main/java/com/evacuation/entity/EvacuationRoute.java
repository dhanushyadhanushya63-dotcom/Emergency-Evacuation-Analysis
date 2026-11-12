package com.evacuation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "evacuation_routes")
public class EvacuationRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_result_id", nullable = false)
    @JsonIgnore
    private SimulationResult simulationResult;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exit_id")
    private Exit exit;

    @Column(name = "route_path_json", columnDefinition = "TEXT", nullable = false)
    private String routePathJson;

    @Column(name = "total_distance_meters", nullable = false)
    private Double totalDistanceMeters;

    @Column(name = "estimated_time_seconds", nullable = false)
    private Double estimatedTimeSeconds;

    @Column(name = "route_type", nullable = false, length = 50)
    private String routeType = "PRIMARY"; // PRIMARY, ALTERNATIVE

    @Column(name = "risk_level", nullable = false, length = 50)
    private String riskLevel = "LOW";

    public EvacuationRoute() {}

    public EvacuationRoute(SimulationResult simulationResult, Room room, Exit exit,
                           String routePathJson, Double totalDistanceMeters,
                           Double estimatedTimeSeconds, String routeType, String riskLevel) {
        this.simulationResult = simulationResult;
        this.room = room;
        this.exit = exit;
        this.routePathJson = routePathJson;
        this.totalDistanceMeters = totalDistanceMeters;
        this.estimatedTimeSeconds = estimatedTimeSeconds;
        this.routeType = routeType != null ? routeType : "PRIMARY";
        this.riskLevel = riskLevel != null ? riskLevel : "LOW";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public SimulationResult getSimulationResult() { return simulationResult; }
    public void setSimulationResult(SimulationResult simulationResult) { this.simulationResult = simulationResult; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public Exit getExit() { return exit; }
    public void setExit(Exit exit) { this.exit = exit; }

    public String getRoutePathJson() { return routePathJson; }
    public void setRoutePathJson(String routePathJson) { this.routePathJson = routePathJson; }

    public Double getTotalDistanceMeters() { return totalDistanceMeters; }
    public void setTotalDistanceMeters(Double totalDistanceMeters) { this.totalDistanceMeters = totalDistanceMeters; }

    public Double getEstimatedTimeSeconds() { return estimatedTimeSeconds; }
    public void setEstimatedTimeSeconds(Double estimatedTimeSeconds) { this.estimatedTimeSeconds = estimatedTimeSeconds; }

    public String getRouteType() { return routeType; }
    public void setRouteType(String routeType) { this.routeType = routeType; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
}
