package com.evacuation.dto;

import java.util.List;

public class FloorDto {
    private Long id;
    private Long buildingId;
    private Integer floorNumber;
    private String floorName;
    private Integer canvasWidth;
    private Integer canvasHeight;
    private List<RoomDto> rooms;
    private List<CorridorDto> corridors;
    private List<ExitDto> exits;

    public FloorDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBuildingId() { return buildingId; }
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }

    public Integer getFloorNumber() { return floorNumber; }
    public void setFloorNumber(Integer floorNumber) { this.floorNumber = floorNumber; }

    public String getFloorName() { return floorName; }
    public void setFloorName(String floorName) { this.floorName = floorName; }

    public Integer getCanvasWidth() { return canvasWidth; }
    public void setCanvasWidth(Integer canvasWidth) { this.canvasWidth = canvasWidth; }

    public Integer getCanvasHeight() { return canvasHeight; }
    public void setCanvasHeight(Integer canvasHeight) { this.canvasHeight = canvasHeight; }

    public List<RoomDto> getRooms() { return rooms; }
    public void setRooms(List<RoomDto> rooms) { this.rooms = rooms; }

    public List<CorridorDto> getCorridors() { return corridors; }
    public void setCorridors(List<CorridorDto> corridors) { this.corridors = corridors; }

    public List<ExitDto> getExits() { return exits; }
    public void setExits(List<ExitDto> exits) { this.exits = exits; }
}
