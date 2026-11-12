package com.evacuation.service;

import com.evacuation.dto.BlockedPassageDto;
import com.evacuation.dto.ScenarioDto;
import com.evacuation.entity.*;
import com.evacuation.exception.ResourceNotFoundException;
import com.evacuation.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScenarioService {

    private final ScenarioRepository scenarioRepository;
    private final BlockedPassageRepository blockedPassageRepository;
    private final CorridorRepository corridorRepository;
    private final ExitRepository exitRepository;

    public ScenarioService(ScenarioRepository scenarioRepository,
                           BlockedPassageRepository blockedPassageRepository,
                           CorridorRepository corridorRepository,
                           ExitRepository exitRepository) {
        this.scenarioRepository = scenarioRepository;
        this.blockedPassageRepository = blockedPassageRepository;
        this.corridorRepository = corridorRepository;
        this.exitRepository = exitRepository;
    }

    @Transactional(readOnly = true)
    public List<ScenarioDto> getAllScenarios() {
        return scenarioRepository.findAll().stream()
                .map(this::toScenarioDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ScenarioDto getScenarioById(Long id) {
        Scenario scenario = scenarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scenario not found with id: " + id));
        return toScenarioDto(scenario);
    }

    @Transactional(readOnly = true)
    public Scenario getDefaultScenario() {
        return scenarioRepository.findByIsDefaultTrue()
                .orElseGet(() -> scenarioRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("No scenario configured")));
    }

    public ScenarioDto createScenario(ScenarioDto dto) {
        Scenario scenario = new Scenario(
                dto.getName(),
                dto.getDescription(),
                dto.getScenarioType() != null ? dto.getScenarioType() : "CUSTOM",
                dto.getWalkingSpeed() != null ? dto.getWalkingSpeed() : 1.2,
                dto.getCrowdDensityMultiplier() != null ? dto.getCrowdDensityMultiplier() : 1.0,
                dto.getReactionDelaySeconds() != null ? dto.getReactionDelaySeconds() : 5,
                Boolean.TRUE.equals(dto.getIsDefault())
        );
        Scenario saved = scenarioRepository.save(scenario);

        if (dto.getBlockedPassages() != null) {
            for (BlockedPassageDto bpDto : dto.getBlockedPassages()) {
                addBlockedPassage(saved.getId(), bpDto);
            }
        }
        return toScenarioDto(saved);
    }

    public ScenarioDto toggleCorridorBlock(Long scenarioId, Long corridorId, String reason) {
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Scenario not found with id: " + scenarioId));
        Corridor corridor = corridorRepository.findById(corridorId)
                .orElseThrow(() -> new ResourceNotFoundException("Corridor not found with id: " + corridorId));

        List<BlockedPassage> passages = blockedPassageRepository.findByScenarioId(scenarioId);
        Optional<BlockedPassage> existing = passages.stream()
                .filter(bp -> bp.getCorridor() != null && bp.getCorridor().getId().equals(corridorId))
                .findFirst();

        if (existing.isPresent()) {
            blockedPassageRepository.delete(existing.get());
        } else {
            BlockedPassage newBp = new BlockedPassage(scenario, corridor, null, "CORRIDOR",
                    reason != null ? reason : "Marked blocked by user interaction");
            blockedPassageRepository.save(newBp);
        }

        return toScenarioDto(scenarioRepository.findById(scenarioId).get());
    }

    public ScenarioDto toggleExitAvailability(Long scenarioId, Long exitId, String reason) {
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Scenario not found with id: " + scenarioId));
        Exit exit = exitRepository.findById(exitId)
                .orElseThrow(() -> new ResourceNotFoundException("Exit not found with id: " + exitId));

        List<BlockedPassage> passages = blockedPassageRepository.findByScenarioId(scenarioId);
        Optional<BlockedPassage> existing = passages.stream()
                .filter(bp -> bp.getExit() != null && bp.getExit().getId().equals(exitId))
                .findFirst();

        if (existing.isPresent()) {
            blockedPassageRepository.delete(existing.get());
        } else {
            BlockedPassage newBp = new BlockedPassage(scenario, null, exit, "EXIT",
                    reason != null ? reason : "Exit unavailable in this scenario");
            blockedPassageRepository.save(newBp);
        }

        return toScenarioDto(scenarioRepository.findById(scenarioId).get());
    }

    private void addBlockedPassage(Long scenarioId, BlockedPassageDto bpDto) {
        Scenario scenario = scenarioRepository.findById(scenarioId).orElse(null);
        if (scenario == null) return;

        Corridor corridor = bpDto.getCorridorId() != null
                ? corridorRepository.findById(bpDto.getCorridorId()).orElse(null) : null;
        Exit exit = bpDto.getExitId() != null
                ? exitRepository.findById(bpDto.getExitId()).orElse(null) : null;

        BlockedPassage bp = new BlockedPassage(scenario, corridor, exit, bpDto.getBlockageType(), bpDto.getReason());
        blockedPassageRepository.save(bp);
    }

    public ScenarioDto toScenarioDto(Scenario scenario) {
        ScenarioDto dto = new ScenarioDto();
        dto.setId(scenario.getId());
        dto.setName(scenario.getName());
        dto.setDescription(scenario.getDescription());
        dto.setScenarioType(scenario.getScenarioType());
        dto.setWalkingSpeed(scenario.getWalkingSpeed());
        dto.setCrowdDensityMultiplier(scenario.getCrowdDensityMultiplier());
        dto.setReactionDelaySeconds(scenario.getReactionDelaySeconds());
        dto.setIsDefault(scenario.getIsDefault());
        dto.setCreatedAt(scenario.getCreatedAt());

        List<BlockedPassageDto> bpDtos = new ArrayList<>();
        List<BlockedPassage> passages = blockedPassageRepository.findByScenarioId(scenario.getId());
        for (BlockedPassage bp : passages) {
            BlockedPassageDto bpd = new BlockedPassageDto();
            bpd.setId(bp.getId());
            bpd.setScenarioId(scenario.getId());
            bpd.setBlockageType(bp.getBlockageType());
            bpd.setReason(bp.getReason());
            if (bp.getCorridor() != null) {
                bpd.setCorridorId(bp.getCorridor().getId());
                bpd.setCorridorCode(bp.getCorridor().getCorridorCode());
                bpd.setCorridorName(bp.getCorridor().getName());
            }
            if (bp.getExit() != null) {
                bpd.setExitId(bp.getExit().getId());
                bpd.setExitCode(bp.getExit().getExitCode());
                bpd.setExitName(bp.getExit().getName());
            }
            bpDtos.add(bpd);
        }
        dto.setBlockedPassages(bpDtos);
        return dto;
    }
}
