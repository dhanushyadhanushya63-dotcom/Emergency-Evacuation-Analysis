package com.evacuation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "corridors")
public class Corridor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    @JsonIgnore
    private Floor floor;

    @Column(name = "corridor_code", nullable = false, length = 50)
    private String corridorCode;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "start_x", nullable = false)
    private Double startX;

    @Column(name = "start_y", nullable = false)
    private Double startY;

    @Column(name = "end_x", nullable = false)
    private Double endX;

    @Column(name = "end_y", nullable = false)
    private Double endY;

    @Column(name = "length_meters", nullable = false)
    private Double lengthMeters;

    @Column(name = "width_meters", nullable = false)
    private Double widthMeters;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity = 50;

    @Column(name = "is_blocked", nullable = false)
    private Boolean isBlocked = false;

    public Corridor() {}

    public Corridor(Floor floor, String corridorCode, String name,
                    Double startX, Double startY, Double endX, Double endY,
                    Double lengthMeters, Double widthMeters, Integer maxCapacity, Boolean isBlocked) {
        this.floor = floor;
        this.corridorCode = corridorCode;
        this.name = name;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.lengthMeters = lengthMeters;
        this.widthMeters = widthMeters;
        this.maxCapacity = maxCapacity;
        this.isBlocked = isBlocked != null ? isBlocked : false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Floor getFloor() { return floor; }
    public void setFloor(Floor floor) { this.floor = floor; }

    public String getCorridorCode() { return corridorCode; }
    public void setCorridorCode(String corridorCode) { this.corridorCode = corridorCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getStartX() { return startX; }
    public void setStartX(Double startX) { this.startX = startX; }

    public Double getStartY() { return startY; }
    public void setStartY(Double startY) { this.startY = startY; }

    public Double getEndX() { return endX; }
    public void setEndX(Double endX) { this.endX = endX; }

    public Double getEndY() { return endY; }
    public void setEndY(Double endY) { this.endY = endY; }

    public Double getLengthMeters() { return lengthMeters; }
    public void setLengthMeters(Double lengthMeters) { this.lengthMeters = lengthMeters; }

    public Double getWidthMeters() { return widthMeters; }
    public void setWidthMeters(Double widthMeters) { this.widthMeters = widthMeters; }

    public Integer getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }

    public Boolean getIsBlocked() { return isBlocked; }
    public void setIsBlocked(Boolean isBlocked) { this.isBlocked = isBlocked; }

    public double getMidX() { return (startX + endX) / 2.0; }
    public double getMidY() { return (startY + endY) / 2.0; }
}
