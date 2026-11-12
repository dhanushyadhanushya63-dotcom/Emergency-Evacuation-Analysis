package com.evacuation.controller;

import com.evacuation.dto.ExitDto;
import com.evacuation.service.BuildingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exits")
@CrossOrigin(origins = "*")
public class ExitController {

    private final BuildingService buildingService;

    public ExitController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping
    public ResponseEntity<List<ExitDto>> getExitsByFloor(@RequestParam(defaultValue = "1") Long floorId) {
        return ResponseEntity.ok(buildingService.getExitsByFloor(floorId));
    }

    @PostMapping("/{id}/toggle-available")
    public ResponseEntity<ExitDto> toggleAvailable(@PathVariable Long id) {
        return ResponseEntity.ok(buildingService.toggleExitAvailable(id));
    }
}
