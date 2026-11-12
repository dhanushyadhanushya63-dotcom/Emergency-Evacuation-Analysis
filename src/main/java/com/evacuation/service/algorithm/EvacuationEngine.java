package com.evacuation.service.algorithm;

import com.evacuation.dto.EvacuationRouteDto;
import com.evacuation.dto.SimulationResponse;
import com.evacuation.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Educational Evacuation Simulation & Crowd Dynamics Engine.
 * Combines Dijkstra route calculation, capacity-constrained queuing models,
 * corridor congestion analysis, and total clearance time estimation.
 */
public class EvacuationEngine {

    private final DijkstraPathfinder dijkstra = new DijkstraPathfinder();
    private final BfsPathfinder bfs = new BfsPathfinder();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public static class SimulationOutput {
        private final SimulationResponse response;
        private final List<EvacuationRouteDto> routes;

        public SimulationOutput(SimulationResponse response, List<EvacuationRouteDto> routes) {
            this.response = response;
            this.routes = routes;
        }

        public SimulationResponse getResponse() { return response; }
        public List<EvacuationRouteDto> getRoutes() { return routes; }
    }

    public SimulationOutput simulate(Graph graph, List<Room> rooms, List<Corridor> corridors,
                                     List<Exit> exits, Scenario scenario) {

        double walkingSpeed = scenario.getWalkingSpeed() != null ? scenario.getWalkingSpeed() : 1.2;
        double densityMultiplier = scenario.getCrowdDensityMultiplier() != null ? scenario.getCrowdDensityMultiplier() : 1.0;
        int reactionDelay = scenario.getReactionDelaySeconds() != null ? scenario.getReactionDelaySeconds() : 5;

        List<EvacuationRouteDto> routes = new ArrayList<>();
        Map<String, Integer> corridorOccupantCount = new HashMap<>();
        Map<String, Integer> exitUtilization = new HashMap<>();
        Map<String, Double> corridorCongestion = new HashMap<>();
        List<String> bottleneckCorridors = new ArrayList<>();

        int totalOccupants = 0;
        int safelyEvacuated = 0;
        int trappedOccupants = 0;
        double maxTimeSeconds = 0.0;
        double sumTimeSeconds = 0.0;

        // Step 1: Calculate primary Dijkstra paths for all rooms
        for (Room room : rooms) {
            int occupants = (int) Math.round(room.getDefaultOccupants() * densityMultiplier);
            totalOccupants += occupants;

            DijkstraPathfinder.PathResult path = dijkstra.findShortestPathToNearestExit(graph, room.getRoomCode(), walkingSpeed);

            EvacuationRouteDto routeDto = new EvacuationRouteDto();
            routeDto.setRoomId(room.getId());
            routeDto.setRoomCode(room.getRoomCode());
            routeDto.setRoomName(room.getName());
            routeDto.setOccupantCount(occupants);
            routeDto.setRouteType("PRIMARY");

            if (path.isFound()) {
                routeDto.setIsReachable(true);
                routeDto.setExitCode(path.getTargetExit().getId());
                routeDto.setExitName(path.getTargetExit().getLabel());
                routeDto.setTotalDistanceMeters(Math.round(path.getTotalDistanceMeters() * 10.0) / 10.0);

                List<String> nodeIds = new ArrayList<>();
                List<double[]> coords = new ArrayList<>();
                for (Graph.Node n : path.getPathNodes()) {
                    nodeIds.add(n.getId());
                    coords.add(new double[]{n.getX(), n.getY()});
                }
                routeDto.setPathNodes(nodeIds);
                routeDto.setPathCoordinates(coords);

                try {
                    routeDto.setRoutePathJson(objectMapper.writeValueAsString(nodeIds));
                } catch (Exception e) {
                    routeDto.setRoutePathJson("[]");
                }

                // Count occupants routed through each corridor edge
                for (Graph.Edge edge : path.getPathEdges()) {
                    if (edge.getCorridorCode() != null) {
                        corridorOccupantCount.merge(edge.getCorridorCode(), occupants, Integer::sum);
                    }
                }

                // Count exit utilization
                exitUtilization.merge(path.getTargetExit().getId(), occupants, Integer::sum);

                // Initial walking time
                double initialTime = reactionDelay + path.getWalkingDurationSeconds();
                routeDto.setEstimatedTimeSeconds(initialTime);
                safelyEvacuated += occupants;
            } else {
                routeDto.setIsReachable(false);
                routeDto.setTotalDistanceMeters(0.0);
                routeDto.setEstimatedTimeSeconds(0.0);
                routeDto.setPathNodes(Collections.emptyList());
                routeDto.setPathCoordinates(Collections.emptyList());
                routeDto.setRoutePathJson("[]");
                routeDto.setRiskLevel("HIGH");
                trappedOccupants += occupants;
            }

            routes.add(routeDto);
        }

        // Step 2: Calculate Corridor Congestion Ratios and Queuing Delays
        Map<String, Double> corridorQueuingDelay = new HashMap<>();

        for (Corridor corridor : corridors) {
            String code = corridor.getCorridorCode();
            int routedOccupants = corridorOccupantCount.getOrDefault(code, 0);

            // Flow capacity: width * 1.3 persons/meter/sec
            double flowRatePerSec = (corridor.getWidthMeters() != null ? corridor.getWidthMeters() : 2.0) * 1.3;
            double corridorCapacity = flowRatePerSec * 60.0; // persons per minute capacity

            double ratio = (routedOccupants > 0 && corridorCapacity > 0)
                    ? (double) routedOccupants / (corridor.getMaxCapacity() != null ? corridor.getMaxCapacity() : 50.0)
                    : 0.0;
            ratio = Math.round(ratio * 100.0) / 100.0;
            corridorCongestion.put(code, ratio);

            if (ratio > 1.0) {
                bottleneckCorridors.add(code);
                // Queuing delay for excess occupants
                double excess = routedOccupants - (corridor.getMaxCapacity() != null ? corridor.getMaxCapacity() : 50.0);
                double delaySeconds = excess / flowRatePerSec;
                corridorQueuingDelay.put(code, Math.max(0.0, delaySeconds));
            } else {
                corridorQueuingDelay.put(code, 0.0);
            }
        }

        // Step 3: Refine route times with queuing delays and assign risk level
        for (EvacuationRouteDto route : routes) {
            if (route.getIsReachable()) {
                double addedDelay = 0.0;
                if (route.getPathNodes() != null) {
                    for (String nodeId : route.getPathNodes()) {
                        addedDelay += corridorQueuingDelay.getOrDefault(nodeId, 0.0);
                    }
                }
                double finalTime = route.getEstimatedTimeSeconds() + addedDelay;
                finalTime = Math.round(finalTime * 10.0) / 10.0;
                route.setEstimatedTimeSeconds(finalTime);

                // Assign route risk level
                if (finalTime > 120.0 || addedDelay > 15.0) {
                    route.setRiskLevel("HIGH");
                } else if (finalTime > 60.0 || addedDelay > 5.0) {
                    route.setRiskLevel("MODERATE");
                } else {
                    route.setRiskLevel("LOW");
                }

                maxTimeSeconds = Math.max(maxTimeSeconds, finalTime);
                sumTimeSeconds += finalTime;
            }
        }

        // Calculate overall peak congestion
        double peakRatio = 0.0;
        for (double r : corridorCongestion.values()) {
            if (r > peakRatio) peakRatio = r;
        }

        double avgTime = safelyEvacuated > 0 ? (sumTimeSeconds / routes.stream().filter(EvacuationRouteDto::getIsReachable).count()) : 0.0;

        String overallRisk;
        if (trappedOccupants > 0) {
            overallRisk = "HIGH (TRAPPED OCCUPANTS)";
        } else if (peakRatio > 1.15 || maxTimeSeconds > 150.0) {
            overallRisk = "HIGH";
        } else if (peakRatio > 0.75 || maxTimeSeconds > 75.0) {
            overallRisk = "MODERATE";
        } else {
            overallRisk = "LOW";
        }

        SimulationResponse response = new SimulationResponse();
        response.setScenarioId(scenario.getId());
        response.setScenarioName(scenario.getName());
        response.setTotalOccupants(totalOccupants);
        response.setSafelyEvacuated(safelyEvacuated);
        response.setTrappedOccupants(trappedOccupants);
        response.setTotalEvacuationTime(Math.round(maxTimeSeconds * 10.0) / 10.0);
        response.setAverageEvacuationTime(Math.round(avgTime * 10.0) / 10.0);
        response.setPeakCongestionRatio(Math.round(peakRatio * 100.0) / 100.0);
        response.setOverallRiskLevel(overallRisk);
        response.setSimulatedAt(LocalDateTime.now());
        response.setRoutes(routes);
        response.setExitUtilization(exitUtilization);
        response.setCorridorCongestion(corridorCongestion);
        response.setBottleneckCorridors(bottleneckCorridors);
        response.setSafetyNotice("EDUCATIONAL SIMULATION NOTICE: Results are theoretical mathematical estimates based on Dijkstra shortest path and queuing delay approximations. Not certified for architectural life safety compliance.");

        return new SimulationOutput(response, routes);
    }
}
