package com.evacuation.controller;

import com.evacuation.dto.CorridorDto;
import com.evacuation.service.BuildingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/corridors")
@CrossOrigin(origins = "*")
public class CorridorController {

    private final BuildingService buildingService;

    public CorridorController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping
    public ResponseEntity<List<CorridorDto>> getCorridorsByFloor(@RequestParam(defaultValue = "1") Long floorId) {
        return ResponseEntity.ok(buildingService.getCorridorsByFloor(floorId));
    }

    @PostMapping("/{id}/toggle-blocked")
    public ResponseEntity<CorridorDto> toggleBlocked(@PathVariable Long id) {
        return ResponseEntity.ok(buildingService.toggleCorridorBlocked(id));
    }
}
