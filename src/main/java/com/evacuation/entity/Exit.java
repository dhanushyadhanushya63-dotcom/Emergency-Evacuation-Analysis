package com.evacuation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "exits")
public class Exit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    @JsonIgnore
    private Floor floor;

    @Column(name = "exit_code", nullable = false, length = 50)
    private String exitCode;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "exit_type", nullable = false, length = 50)
    private String exitType = "EMERGENCY_EXIT"; // EMERGENCY_EXIT, MAIN_ENTRANCE

    @Column(nullable = false)
    private Double x;

    @Column(nullable = false)
    private Double y;

    @Column(name = "width_meters", nullable = false)
    private Double widthMeters = 2.0;

    @Column(name = "flow_rate_capacity", nullable = false)
    private Integer flowRateCapacity = 40; // persons/minute

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    public Exit() {}

    public Exit(Floor floor, String exitCode, String name, String exitType,
                Double x, Double y, Double widthMeters, Integer flowRateCapacity, Boolean isAvailable) {
        this.floor = floor;
        this.exitCode = exitCode;
        this.name = name;
        this.exitType = exitType;
        this.x = x;
        this.y = y;
        this.widthMeters = widthMeters;
        this.flowRateCapacity = flowRateCapacity;
        this.isAvailable = isAvailable != null ? isAvailable : true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Floor getFloor() { return floor; }
    public void setFloor(Floor floor) { this.floor = floor; }

    public String getExitCode() { return exitCode; }
    public void setExitCode(String exitCode) { this.exitCode = exitCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getExitType() { return exitType; }
    public void setExitType(String exitType) { this.exitType = exitType; }

    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }

    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }

    public Double getWidthMeters() { return widthMeters; }
    public void setWidthMeters(Double widthMeters) { this.widthMeters = widthMeters; }

    public Integer getFlowRateCapacity() { return flowRateCapacity; }
    public void setFlowRateCapacity(Integer flowRateCapacity) { this.flowRateCapacity = flowRateCapacity; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
}
