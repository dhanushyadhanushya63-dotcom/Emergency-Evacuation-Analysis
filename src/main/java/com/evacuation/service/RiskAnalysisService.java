package com.evacuation.service;

import com.evacuation.dto.EvacuationRouteDto;
import com.evacuation.dto.RiskAnalysisDto;
import com.evacuation.dto.SimulationResponse;
import com.evacuation.entity.*;
import com.evacuation.exception.ResourceNotFoundException;
import com.evacuation.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class RiskAnalysisService {

    private final SimulationService simulationService;
    private final ScenarioRepository scenarioRepository;
    private final RoomRepository roomRepository;
    private final CorridorRepository corridorRepository;
    private final ExitRepository exitRepository;
    private final BlockedPassageRepository blockedPassageRepository;

    public RiskAnalysisService(SimulationService simulationService,
                               ScenarioRepository scenarioRepository,
                               RoomRepository roomRepository,
                               CorridorRepository corridorRepository,
                               ExitRepository exitRepository,
                               BlockedPassageRepository blockedPassageRepository) {
        this.simulationService = simulationService;
        this.scenarioRepository = scenarioRepository;
        this.roomRepository = roomRepository;
        this.corridorRepository = corridorRepository;
        this.exitRepository = exitRepository;
        this.blockedPassageRepository = blockedPassageRepository;
    }

    public RiskAnalysisDto analyzeScenarioRisk(Long scenarioId) {
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Scenario not found with id: " + scenarioId));

        SimulationResponse sim = simulationService.runSimulation(scenarioId);

        List<Corridor> corridors = corridorRepository.findAll();
        List<Exit> exits = exitRepository.findAll();
        List<BlockedPassage> blockedPassages = blockedPassageRepository.findByScenarioId(scenarioId);

        Set<Long> blockedCorridorIds = new HashSet<>();
        Set<Long> unavailableExitIds = new HashSet<>();
        for (BlockedPassage bp : blockedPassages) {
            if ("CORRIDOR".equalsIgnoreCase(bp.getBlockageType()) && bp.getCorridor() != null) {
                blockedCorridorIds.add(bp.getCorridor().getId());
            } else if ("EXIT".equalsIgnoreCase(bp.getBlockageType()) && bp.getExit() != null) {
                unavailableExitIds.add(bp.getExit().getId());
            }
        }

        RiskAnalysisDto dto = new RiskAnalysisDto();
        dto.setScenarioId(scenario.getId());
        dto.setScenarioName(scenario.getName());
        dto.setOverallRiskLevel(sim.getOverallRiskLevel());
        dto.setOverallCongestionIndex(sim.getPeakCongestionRatio());
        dto.setTotalOccupants(sim.getTotalOccupants());
        dto.setTrappedOccupants(sim.getTrappedOccupants());
        dto.setMaxEvacuationTimeSeconds(sim.getTotalEvacuationTime());
        dto.setAvgEvacuationTimeSeconds(sim.getAverageEvacuationTime());
        dto.setBlockedCorridorsCount(blockedCorridorIds.size());
        dto.setAvailableExitsCount(exits.size() - unavailableExitIds.size());

        // Corridor Risk Metrics
        List<RiskAnalysisDto.CorridorRiskMetric> corridorRisks = new ArrayList<>();
        int affectedRooms = 0;

        for (Corridor corridor : corridors) {
            RiskAnalysisDto.CorridorRiskMetric cm = new RiskAnalysisDto.CorridorRiskMetric();
            cm.setCorridorId(corridor.getId());
            cm.setCorridorCode(corridor.getCorridorCode());
            cm.setName(corridor.getName());
            cm.setLengthMeters(corridor.getLengthMeters());
            cm.setWidthMeters(corridor.getWidthMeters());
            cm.setMaxCapacity(corridor.getMaxCapacity());
            cm.setIsBlocked(blockedCorridorIds.contains(corridor.getId()) || Boolean.TRUE.equals(corridor.getIsBlocked()));

            Double ratio = sim.getCorridorCongestion().getOrDefault(corridor.getCorridorCode(), 0.0);
            cm.setCongestionRatio(ratio);
            cm.setTotalOccupantsRouted((int) Math.round(ratio * (corridor.getMaxCapacity() != null ? corridor.getMaxCapacity() : 50)));

            if (cm.getIsBlocked()) {
                cm.setRiskCategory("HIGH (BLOCKED)");
            } else if (ratio > 1.0) {
                cm.setRiskCategory("HIGH");
            } else if (ratio > 0.65) {
                cm.setRiskCategory("MODERATE");
            } else {
                cm.setRiskCategory("LOW");
            }
            corridorRisks.add(cm);
        }
        dto.setCorridorRisks(corridorRisks);

        // Room Risk Metrics
        List<RiskAnalysisDto.RoomRiskMetric> roomRisks = new ArrayList<>();
        for (EvacuationRouteDto r : sim.getRoutes()) {
            RiskAnalysisDto.RoomRiskMetric rm = new RiskAnalysisDto.RoomRiskMetric();
            rm.setRoomId(r.getRoomId());
            rm.setRoomCode(r.getRoomCode());
            rm.setName(r.getRoomName());
            rm.setOccupants(r.getOccupantCount());
            rm.setAssignedExit(r.getExitName());
            rm.setEstimatedTimeSeconds(r.getEstimatedTimeSeconds());
            rm.setIsTrapped(!r.getIsReachable());
            rm.setRiskCategory(r.getRiskLevel());

            if (!r.getIsReachable() || "HIGH".equalsIgnoreCase(r.getRiskLevel())) {
                affectedRooms++;
            }
            roomRisks.add(rm);
        }
        dto.setRoomRisks(roomRisks);
        dto.setAffectedRoomsCount(affectedRooms);

        // Exit Risk Metrics
        Map<String, RiskAnalysisDto.ExitRiskMetric> exitRisks = new HashMap<>();
        for (Exit exit : exits) {
            RiskAnalysisDto.ExitRiskMetric em = new RiskAnalysisDto.ExitRiskMetric();
            em.setExitCode(exit.getExitCode());
            em.setName(exit.getName());
            em.setFlowRateCapacity(exit.getFlowRateCapacity());
            em.setIsAvailable(!unavailableExitIds.contains(exit.getId()) && Boolean.TRUE.equals(exit.getIsAvailable()));

            int assigned = sim.getExitUtilization().getOrDefault(exit.getExitCode(), 0);
            em.setAssignedEvacuees(assigned);

            double util = (sim.getTotalOccupants() > 0)
                    ? Math.round(((double) assigned / sim.getTotalOccupants()) * 1000.0) / 10.0
                    : 0.0;
            em.setUtilizationPercentage(util);

            exitRisks.put(exit.getExitCode(), em);
        }
        dto.setExitRisks(exitRisks);

        // Recommendations
        List<String> recs = new ArrayList<>();
        if (dto.getTrappedOccupants() > 0) {
            recs.add("CRITICAL: " + dto.getTrappedOccupants() + " occupants in isolated zones have NO navigable path to an exit. Establish secondary emergency breach passages immediately.");
        }
        if (!sim.getBottleneckCorridors().isEmpty()) {
            recs.add("Bottlenecks detected along " + String.join(", ", sim.getBottleneckCorridors()) + ". Recommend staggered evacuation phasing or widening passage pathways.");
        }
        if (dto.getAvailableExitsCount() < 2) {
            recs.add("High single-point-of-failure risk: Only " + dto.getAvailableExitsCount() + " exit currently available. Provide backup exterior egress points.");
        }
        if (sim.getTotalEvacuationTime() > 120.0) {
            recs.add("Estimated building clearance time (" + sim.getTotalEvacuationTime() + "s) exceeds NFPA recommended 2-minute threshold for high-density occupancy.");
        }
        if (recs.isEmpty()) {
            recs.add("All egress paths operate within standard capacity margins. Evacuation efficiency is optimal under current simulation parameters.");
        }
        dto.setHighRiskRecommendations(recs);

        dto.setEducationalDisclaimer("SIMULATION RISK CLASSIFICATION DISCLAIMER: Risk indices, congestion coefficients, and bottleneck rankings are educational simulations generated by heuristic algorithms. They do not constitute official architectural safety certifications.");

        return dto;
    }
}
