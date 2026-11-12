package com.evacuation.dto;

public class ExitDto {
    private Long id;
    private Long floorId;
    private String exitCode;
    private String name;
    private String exitType;
    private Double x;
    private Double y;
    private Double widthMeters;
    private Integer flowRateCapacity;
    private Boolean isAvailable;

    public ExitDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFloorId() { return floorId; }
    public void setFloorId(Long floorId) { this.floorId = floorId; }

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
