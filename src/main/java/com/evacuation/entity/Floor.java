package com.evacuation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "floors")
public class Floor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    @JsonIgnore
    private Building building;

    @Column(name = "floor_number", nullable = false)
    private Integer floorNumber = 1;

    @Column(name = "floor_name", nullable = false, length = 100)
    private String floorName;

    @Column(name = "canvas_width", nullable = false)
    private Integer canvasWidth = 1000;

    @Column(name = "canvas_height", nullable = false)
    private Integer canvasHeight = 600;

    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Corridor> corridors = new ArrayList<>();

    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Exit> exits = new ArrayList<>();

    public Floor() {}

    public Floor(Building building, Integer floorNumber, String floorName, Integer canvasWidth, Integer canvasHeight) {
        this.building = building;
        this.floorNumber = floorNumber;
        this.floorName = floorName;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Building getBuilding() { return building; }
    public void setBuilding(Building building) { this.building = building; }

    public Integer getFloorNumber() { return floorNumber; }
    public void setFloorNumber(Integer floorNumber) { this.floorNumber = floorNumber; }

    public String getFloorName() { return floorName; }
    public void setFloorName(String floorName) { this.floorName = floorName; }

    public Integer getCanvasWidth() { return canvasWidth; }
    public void setCanvasWidth(Integer canvasWidth) { this.canvasWidth = canvasWidth; }

    public Integer getCanvasHeight() { return canvasHeight; }
    public void setCanvasHeight(Integer canvasHeight) { this.canvasHeight = canvasHeight; }

    public List<Room> getRooms() { return rooms; }
    public void setRooms(List<Room> rooms) { this.rooms = rooms; }

    public List<Corridor> getCorridors() { return corridors; }
    public void setCorridors(List<Corridor> corridors) { this.corridors = corridors; }

    public List<Exit> getExits() { return exits; }
    public void setExits(List<Exit> exits) { this.exits = exits; }
}
