package com.evacuation.service;

import com.evacuation.dto.*;
import com.evacuation.entity.*;
import com.evacuation.exception.ResourceNotFoundException;
import com.evacuation.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;
    private final RoomRepository roomRepository;
    private final CorridorRepository corridorRepository;
    private final ExitRepository exitRepository;

    public BuildingService(BuildingRepository buildingRepository,
                           FloorRepository floorRepository,
                           RoomRepository roomRepository,
                           CorridorRepository corridorRepository,
                           ExitRepository exitRepository) {
        this.buildingRepository = buildingRepository;
        this.floorRepository = floorRepository;
        this.roomRepository = roomRepository;
        this.corridorRepository = corridorRepository;
        this.exitRepository = exitRepository;
    }

    @Transactional(readOnly = true)
    public List<BuildingDto> getAllBuildings() {
        return buildingRepository.findAll().stream()
                .map(this::toBuildingDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BuildingDto getBuildingById(Long id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + id));
        return toBuildingDto(building);
    }

    @Transactional(readOnly = true)
    public BuildingDto getDefaultSampleBuilding() {
        return buildingRepository.findAll().stream()
                .findFirst()
                .map(this::toBuildingDto)
                .orElseThrow(() -> new ResourceNotFoundException("No sample building found in database"));
    }

    @Transactional(readOnly = true)
    public List<RoomDto> getRoomsByFloor(Long floorId) {
        return roomRepository.findByFloorId(floorId).stream()
                .map(this::toRoomDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CorridorDto> getCorridorsByFloor(Long floorId) {
        return corridorRepository.findByFloorId(floorId).stream()
                .map(this::toCorridorDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExitDto> getExitsByFloor(Long floorId) {
        return exitRepository.findByFloorId(floorId).stream()
                .map(this::toExitDto)
                .collect(Collectors.toList());
    }

    public RoomDto updateRoomOccupants(Long roomId, Integer occupants) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));
        room.setDefaultOccupants(occupants);
        return toRoomDto(roomRepository.save(room));
    }

    public CorridorDto toggleCorridorBlocked(Long corridorId) {
        Corridor corridor = corridorRepository.findById(corridorId)
                .orElseThrow(() -> new ResourceNotFoundException("Corridor not found with id: " + corridorId));
        corridor.setIsBlocked(!Boolean.TRUE.equals(corridor.getIsBlocked()));
        return toCorridorDto(corridorRepository.save(corridor));
    }

    public ExitDto toggleExitAvailable(Long exitId) {
        Exit exit = exitRepository.findById(exitId)
                .orElseThrow(() -> new ResourceNotFoundException("Exit not found with id: " + exitId));
        exit.setIsAvailable(!Boolean.TRUE.equals(exit.getIsAvailable()));
        return toExitDto(exitRepository.save(exit));
    }

    // Mapping helper methods
    public BuildingDto toBuildingDto(Building building) {
        BuildingDto dto = new BuildingDto();
        dto.setId(building.getId());
        dto.setCode(building.getCode());
        dto.setName(building.getName());
        dto.setDescription(building.getDescription());
        dto.setTotalFloors(building.getTotalFloors());
        dto.setTotalAreaSqm(building.getTotalAreaSqm());

        List<FloorDto> floorDtos = new ArrayList<>();
        if (building.getFloors() != null) {
            for (Floor floor : building.getFloors()) {
                floorDtos.add(toFloorDto(floor));
            }
        }
        dto.setFloors(floorDtos);
        return dto;
    }

    public FloorDto toFloorDto(Floor floor) {
        FloorDto dto = new FloorDto();
        dto.setId(floor.getId());
        dto.setBuildingId(floor.getBuilding() != null ? floor.getBuilding().getId() : null);
        dto.setFloorNumber(floor.getFloorNumber());
        dto.setFloorName(floor.getFloorName());
        dto.setCanvasWidth(floor.getCanvasWidth());
        dto.setCanvasHeight(floor.getCanvasHeight());

        if (floor.getRooms() != null) {
            dto.setRooms(floor.getRooms().stream().map(this::toRoomDto).collect(Collectors.toList()));
        }
        if (floor.getCorridors() != null) {
            dto.setCorridors(floor.getCorridors().stream().map(this::toCorridorDto).collect(Collectors.toList()));
        }
        if (floor.getExits() != null) {
            dto.setExits(floor.getExits().stream().map(this::toExitDto).collect(Collectors.toList()));
        }
        return dto;
    }

    public RoomDto toRoomDto(Room room) {
        RoomDto dto = new RoomDto();
        dto.setId(room.getId());
        dto.setFloorId(room.getFloor() != null ? room.getFloor().getId() : null);
        dto.setRoomCode(room.getRoomCode());
        dto.setName(room.getName());
        dto.setRoomType(room.getRoomType());
        dto.setX(room.getX());
        dto.setY(room.getY());
        dto.setWidth(room.getWidth());
        dto.setHeight(room.getHeight());
        dto.setCapacity(room.getCapacity());
        dto.setDefaultOccupants(room.getDefaultOccupants());
        dto.setCenterX(room.getCenterX());
        dto.setCenterY(room.getCenterY());
        return dto;
    }

    public CorridorDto toCorridorDto(Corridor corridor) {
        CorridorDto dto = new CorridorDto();
        dto.setId(corridor.getId());
        dto.setFloorId(corridor.getFloor() != null ? corridor.getFloor().getId() : null);
        dto.setCorridorCode(corridor.getCorridorCode());
        dto.setName(corridor.getName());
        dto.setStartX(corridor.getStartX());
        dto.setStartY(corridor.getStartY());
        dto.setEndX(corridor.getEndX());
        dto.setEndY(corridor.getEndY());
        dto.setLengthMeters(corridor.getLengthMeters());
        dto.setWidthMeters(corridor.getWidthMeters());
        dto.setMaxCapacity(corridor.getMaxCapacity());
        dto.setIsBlocked(corridor.getIsBlocked());
        dto.setMidX(corridor.getMidX());
        dto.setMidY(corridor.getMidY());
        return dto;
    }

    public ExitDto toExitDto(Exit exit) {
        ExitDto dto = new ExitDto();
        dto.setId(exit.getId());
        dto.setFloorId(exit.getFloor() != null ? exit.getFloor().getId() : null);
        dto.setExitCode(exit.getExitCode());
        dto.setName(exit.getName());
        dto.setExitType(exit.getExitType());
        dto.setX(exit.getX());
        dto.setY(exit.getY());
        dto.setWidthMeters(exit.getWidthMeters());
        dto.setFlowRateCapacity(exit.getFlowRateCapacity());
        dto.setIsAvailable(exit.getIsAvailable());
        return dto;
    }
}
