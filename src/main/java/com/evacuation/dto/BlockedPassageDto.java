package com.evacuation.dto;

public class BlockedPassageDto {
    private Long id;
    private Long scenarioId;
    private Long corridorId;
    private String corridorCode;
    private String corridorName;
    private Long exitId;
    private String exitCode;
    private String exitName;
    private String blockageType; // CORRIDOR, EXIT
    private String reason;

    public BlockedPassageDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getScenarioId() { return scenarioId; }
    public void setScenarioId(Long scenarioId) { this.scenarioId = scenarioId; }

    public Long getCorridorId() { return corridorId; }
    public void setCorridorId(Long corridorId) { this.corridorId = corridorId; }

    public String getCorridorCode() { return corridorCode; }
    public void setCorridorCode(String corridorCode) { this.corridorCode = corridorCode; }

    public String getCorridorName() { return corridorName; }
    public void setCorridorName(String corridorName) { this.corridorName = corridorName; }

    public Long getExitId() { return exitId; }
    public void setExitId(Long exitId) { this.exitId = exitId; }

    public String getExitCode() { return exitCode; }
    public void setExitCode(String exitCode) { this.exitCode = exitCode; }

    public String getExitName() { return exitName; }
    public void setExitName(String exitName) { this.exitName = exitName; }

    public String getBlockageType() { return blockageType; }
    public void setBlockageType(String blockageType) { this.blockageType = blockageType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
