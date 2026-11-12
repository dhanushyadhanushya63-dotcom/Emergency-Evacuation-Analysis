package com.evacuation.service;

import com.evacuation.dto.EvacuationRouteDto;
import com.evacuation.dto.SimulationRequest;
import com.evacuation.dto.SimulationResponse;
import com.evacuation.entity.*;
import com.evacuation.exception.ResourceNotFoundException;
import com.evacuation.repository.*;
import com.evacuation.service.algorithm.EvacuationEngine;
import com.evacuation.service.algorithm.Graph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SimulationService {

    private final ScenarioRepository scenarioRepository;
    private final BlockedPassageRepository blockedPassageRepository;
    private final RoomRepository roomRepository;
    private final CorridorRepository corridorRepository;
    private final ExitRepository exitRepository;
    private final SimulationResultRepository simulationResultRepository;
    private final EvacuationRouteRepository evacuationRouteRepository;
    private final GraphService graphService;
    private final EvacuationEngine evacuationEngine = new EvacuationEngine();

    public SimulationService(ScenarioRepository scenarioRepository,
                             BlockedPassageRepository blockedPassageRepository,
                             RoomRepository roomRepository,
                             CorridorRepository corridorRepository,
                             ExitRepository exitRepository,
                             SimulationResultRepository simulationResultRepository,
                             EvacuationRouteRepository evacuationRouteRepository,
                             GraphService graphService) {
        this.scenarioRepository = scenarioRepository;
        this.blockedPassageRepository = blockedPassageRepository;
        this.roomRepository = roomRepository;
        this.corridorRepository = corridorRepository;
        this.exitRepository = exitRepository;
        this.simulationResultRepository = simulationResultRepository;
        this.evacuationRouteRepository = evacuationRouteRepository;
        this.graphService = graphService;
    }

    public SimulationResponse runSimulation(Long scenarioId) {
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Scenario not found with id: " + scenarioId));

        return executeSimulation(scenario);
    }

    public SimulationResponse runCustomSimulation(SimulationRequest request) {
        Scenario scenario;
        if (request.getScenarioId() != null) {
            scenario = scenarioRepository.findById(request.getScenarioId())
                    .orElse(new Scenario("Ad-hoc Custom Simulation", "Transient simulation run", "CUSTOM", 1.2, 1.0, 5, false));
        } else {
            scenario = new Scenario("Custom Configuration", "On-the-fly custom scenario", "CUSTOM", 1.2, 1.0, 5, false);
        }

        if (request.getWalkingSpeed() != null) {
            scenario.setWalkingSpeed(request.getWalkingSpeed());
        }
        if (request.getCrowdDensityMultiplier() != null) {
            scenario.setCrowdDensityMultiplier(request.getCrowdDensityMultiplier());
        }
        if (request.getReactionDelaySeconds() != null) {
            scenario.setReactionDelaySeconds(request.getReactionDelaySeconds());
        }

        return executeSimulation(scenario);
    }

    private SimulationResponse executeSimulation(Scenario scenario) {
        List<Room> rooms = roomRepository.findAll();
        List<Corridor> corridors = corridorRepository.findAll();
        List<Exit> exits = exitRepository.findAll();
        List<BlockedPassage> blockedPassages = scenario.getId() != null
                ? blockedPassageRepository.findByScenarioId(scenario.getId())
                : List.of();

        // Build topological graph
        Graph graph = graphService.buildGraph(rooms, corridors, exits, blockedPassages);

        // Run simulation algorithm
        EvacuationEngine.SimulationOutput output = evacuationEngine.simulate(graph, rooms, corridors, exits, scenario);
        SimulationResponse response = output.getResponse();

        // Persist result if scenario is stored in DB
        if (scenario.getId() != null) {
            SimulationResult result = new SimulationResult(
                    scenario,
                    response.getTotalEvacuationTime(),
                    response.getAverageEvacuationTime(),
                    response.getTotalOccupants(),
                    response.getSafelyEvacuated(),
                    response.getTrappedOccupants(),
                    response.getPeakCongestionRatio(),
                    response.getOverallRiskLevel()
            );
            SimulationResult savedResult = simulationResultRepository.save(result);
            response.setId(savedResult.getId());

            for (EvacuationRouteDto rDto : output.getRoutes()) {
                Room room = rooms.stream().filter(r -> r.getId().equals(rDto.getRoomId())).findFirst().orElse(null);
                Exit exit = exits.stream().filter(e -> e.getExitCode().equalsIgnoreCase(rDto.getExitCode())).findFirst().orElse(null);

                if (room != null) {
                    EvacuationRoute route = new EvacuationRoute(
                            savedResult,
                            room,
                            exit,
                            rDto.getRoutePathJson(),
                            rDto.getTotalDistanceMeters(),
                            rDto.getEstimatedTimeSeconds(),
                            rDto.getRouteType(),
                            rDto.getRiskLevel()
                    );
                    evacuationRouteRepository.save(route);
                }
            }
        }

        return response;
    }

    @Transactional(readOnly = true)
    public SimulationResponse getLatestSimulationResult(Long scenarioId) {
        return simulationResultRepository.findFirstByScenarioIdOrderBySimulatedAtDesc(scenarioId)
                .map(res -> runSimulation(scenarioId)) // Return fresh run for rich map details
                .orElseGet(() -> runSimulation(scenarioId));
    }
}
