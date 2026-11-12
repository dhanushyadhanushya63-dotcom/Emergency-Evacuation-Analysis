package com.evacuation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    @JsonIgnore
    private Floor floor;

    @Column(name = "room_code", nullable = false, length = 50)
    private String roomCode;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "room_type", nullable = false, length = 80)
    private String roomType;

    @Column(nullable = false)
    private Double x;

    @Column(nullable = false)
    private Double y;

    @Column(nullable = false)
    private Double width;

    @Column(nullable = false)
    private Double height;

    @Column(nullable = false)
    private Integer capacity = 20;

    @Column(name = "default_occupants", nullable = false)
    private Integer defaultOccupants = 10;

    public Room() {}

    public Room(Floor floor, String roomCode, String name, String roomType,
                Double x, Double y, Double width, Double height,
                Integer capacity, Integer defaultOccupants) {
        this.floor = floor;
        this.roomCode = roomCode;
        this.name = name;
        this.roomType = roomType;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.capacity = capacity;
        this.defaultOccupants = defaultOccupants;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Floor getFloor() { return floor; }
    public void setFloor(Floor floor) { this.floor = floor; }

    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }

    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }

    public Double getWidth() { return width; }
    public void setWidth(Double width) { this.width = width; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getDefaultOccupants() { return defaultOccupants; }
    public void setDefaultOccupants(Integer defaultOccupants) { this.defaultOccupants = defaultOccupants; }

    // Center coordinates for path calculations
    public double getCenterX() {
        return x + (width / 2.0);
    }

    public double getCenterY() {
        return y + (height / 2.0);
    }
}
