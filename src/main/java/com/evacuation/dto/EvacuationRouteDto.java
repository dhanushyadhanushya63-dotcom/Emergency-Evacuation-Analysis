package com.evacuation.dto;

import java.util.List;

public class EvacuationRouteDto {
    private Long id;
    private Long roomId;
    private String roomCode;
    private String roomName;
    private Long exitId;
    private String exitCode;
    private String exitName;
    private String routePathJson;
    private List<String> pathNodes; // e.g. ["RM-101", "COR-1", "COR-W", "EX-4"]
    private List<double[]> pathCoordinates; // [[x1, y1], [x2, y2], ...] for rendering
    private Double totalDistanceMeters;
    private Double estimatedTimeSeconds;
    private String routeType; // PRIMARY, ALTERNATIVE
    private String riskLevel; // LOW, MODERATE, HIGH
    private Integer occupantCount;
    private Boolean isReachable;

    public EvacuationRouteDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public Long getExitId() { return exitId; }
    public void setExitId(Long exitId) { this.exitId = exitId; }

    public String getExitCode() { return exitCode; }
    public void setExitCode(String exitCode) { this.exitCode = exitCode; }

    public String getExitName() { return exitName; }
    public void setExitName(String exitName) { this.exitName = exitName; }

    public String getRoutePathJson() { return routePathJson; }
    public void setRoutePathJson(String routePathJson) { this.routePathJson = routePathJson; }

    public List<String> getPathNodes() { return pathNodes; }
    public void setPathNodes(List<String> pathNodes) { this.pathNodes = pathNodes; }

    public List<double[]> getPathCoordinates() { return pathCoordinates; }
    public void setPathCoordinates(List<double[]> pathCoordinates) { this.pathCoordinates = pathCoordinates; }

    public Double getTotalDistanceMeters() { return totalDistanceMeters; }
    public void setTotalDistanceMeters(Double totalDistanceMeters) { this.totalDistanceMeters = totalDistanceMeters; }

    public Double getEstimatedTimeSeconds() { return estimatedTimeSeconds; }
    public void setEstimatedTimeSeconds(Double estimatedTimeSeconds) { this.estimatedTimeSeconds = estimatedTimeSeconds; }

    public String getRouteType() { return routeType; }
    public void setRouteType(String routeType) { this.routeType = routeType; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public Integer getOccupantCount() { return occupantCount; }
    public void setOccupantCount(Integer occupantCount) { this.occupantCount = occupantCount; }

    public Boolean getIsReachable() { return isReachable; }
    public void setIsReachable(Boolean reachable) { isReachable = reachable; }
}
