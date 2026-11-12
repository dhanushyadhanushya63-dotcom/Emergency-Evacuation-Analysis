package com.evacuation.dto;

import java.util.List;

public class BuildingDto {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer totalFloors;
    private Double totalAreaSqm;
    private List<FloorDto> floors;

    public BuildingDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getTotalFloors() { return totalFloors; }
    public void setTotalFloors(Integer totalFloors) { this.totalFloors = totalFloors; }

    public Double getTotalAreaSqm() { return totalAreaSqm; }
    public void setTotalAreaSqm(Double totalAreaSqm) { this.totalAreaSqm = totalAreaSqm; }

    public List<FloorDto> getFloors() { return floors; }
    public void setFloors(List<FloorDto> floors) { this.floors = floors; }
}
