package com.evacuation.controller;

import com.evacuation.dto.RoomDto;
import com.evacuation.service.BuildingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    private final BuildingService buildingService;

    public RoomController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getRoomsByFloor(@RequestParam(defaultValue = "1") Long floorId) {
        return ResponseEntity.ok(buildingService.getRoomsByFloor(floorId));
    }

    @PutMapping("/{id}/occupants")
    public ResponseEntity<RoomDto> updateOccupants(@PathVariable Long id, @RequestBody Map<String, Integer> payload) {
        Integer occupants = payload.getOrDefault("occupants", 10);
        return ResponseEntity.ok(buildingService.updateRoomOccupants(id, occupants));
    }
}
