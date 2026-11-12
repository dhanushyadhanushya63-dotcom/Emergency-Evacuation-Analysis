package com.evacuation.dto;

public class RoomDto {
    private Long id;
    private Long floorId;
    private String roomCode;
    private String name;
    private String roomType;
    private Double x;
    private Double y;
    private Double width;
    private Double height;
    private Integer capacity;
    private Integer defaultOccupants;
    private Double centerX;
    private Double centerY;

    public RoomDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFloorId() { return floorId; }
    public void setFloorId(Long floorId) { this.floorId = floorId; }

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

    public Double getCenterX() { return centerX; }
    public void setCenterX(Double centerX) { this.centerX = centerX; }

    public Double getCenterY() { return centerY; }
    public void setCenterY(Double centerY) { this.centerY = centerY; }
}
