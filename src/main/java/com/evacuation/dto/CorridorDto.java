package com.evacuation.dto;

public class CorridorDto {
    private Long id;
    private Long floorId;
    private String corridorCode;
    private String name;
    private Double startX;
    private Double startY;
    private Double endX;
    private Double endY;
    private Double lengthMeters;
    private Double widthMeters;
    private Integer maxCapacity;
    private Boolean isBlocked;
    private Double midX;
    private Double midY;

    public CorridorDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFloorId() { return floorId; }
    public void setFloorId(Long floorId) { this.floorId = floorId; }

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

    public Double getMidX() { return midX; }
    public void setMidX(Double midX) { this.midX = midX; }

    public Double getMidY() { return midY; }
    public void setMidY(Double midY) { this.midY = midY; }
}
