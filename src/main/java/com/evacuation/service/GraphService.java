package com.evacuation.service;

import com.evacuation.entity.*;
import com.evacuation.service.algorithm.Graph;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Constructs topological graph representations from JPA floor plan entities
 * and applies emergency scenario blockage conditions.
 */
@Service
public class GraphService {

    public Graph buildGraph(List<Room> rooms, List<Corridor> corridors, List<Exit> exits,
                            List<BlockedPassage> blockedPassages) {

        Graph graph = new Graph();

        // 1. Add Corridor Junction Nodes
        // North Concourse junctions
        Graph.Node jNW = new Graph.Node("J-NW", "North-West Junction", "JUNCTION", 140, 200);
        Graph.Node jN2 = new Graph.Node("J-N2", "North Lab Junction", "JUNCTION", 360, 200);
        Graph.Node jNC = new Graph.Node("J-NC", "North-Central Junction", "JUNCTION", 460, 200);
        Graph.Node jN3 = new Graph.Node("J-N3", "North Conf Junction", "JUNCTION", 580, 200);
        Graph.Node jNE = new Graph.Node("J-NE", "North-East Junction", "JUNCTION", 775, 200);

        // South Concourse junctions
        Graph.Node jSW = new Graph.Node("J-SW", "South-West Junction", "JUNCTION", 140, 400);
        Graph.Node jS2 = new Graph.Node("J-S2", "South Cafe Junction", "JUNCTION", 370, 400);
        Graph.Node jSC = new Graph.Node("J-SC", "South-Central Junction", "JUNCTION", 460, 400);
        Graph.Node jS3 = new Graph.Node("J-S3", "South Work Junction", "JUNCTION", 610, 400);
        Graph.Node jSE = new Graph.Node("J-SE", "South-East Junction", "JUNCTION", 820, 400);

        // Center / West / East Hall junctions
        Graph.Node jW = new Graph.Node("J-W", "West Entrance Hub", "JUNCTION", 140, 300);
        Graph.Node jE = new Graph.Node("J-E", "East Fire Escape Hub", "JUNCTION", 775, 300);

        graph.addNode(jNW); graph.addNode(jN2); graph.addNode(jNC); graph.addNode(jN3); graph.addNode(jNE);
        graph.addNode(jSW); graph.addNode(jS2); graph.addNode(jSC); graph.addNode(jS3); graph.addNode(jSE);
        graph.addNode(jW);  graph.addNode(jE);

        // 2. Add Corridor Edges
        // COR-N: North Concourse segments
        graph.addEdge(jNW, jN2, "E-COR-N1", 7.0, 2.5, 60, "COR-N");
        graph.addEdge(jN2, jNC, "E-COR-N2", 8.0, 2.5, 60, "COR-N");
        graph.addEdge(jNC, jN3, "E-COR-N3", 8.0, 2.5, 60, "COR-N");
        graph.addEdge(jN3, jNE, "E-COR-N4", 9.0, 2.5, 60, "COR-N");

        // COR-C: Central Grand Spine
        graph.addEdge(jNC, jSC, "E-COR-C", 20.0, 3.2, 80, "COR-C");

        // COR-S: South Concourse segments
        graph.addEdge(jSW, jS2, "E-COR-S1", 7.5, 2.5, 60, "COR-S");
        graph.addEdge(jS2, jSC, "E-COR-S2", 7.5, 2.5, 60, "COR-S");
        graph.addEdge(jSC, jS3, "E-COR-S3", 8.5, 2.5, 60, "COR-S");
        graph.addEdge(jS3, jSE, "E-COR-S4", 10.5, 2.5, 60, "COR-S");

        // COR-1: North-West Branch
        graph.addEdge(jNW, jW, "E-COR-1", 13.0, 2.0, 40, "COR-1");

        // COR-2: South-West Branch
        graph.addEdge(jW, jSW, "E-COR-2", 13.0, 2.0, 40, "COR-2");

        // COR-3: East Connector Spine
        graph.addEdge(jNE, jE, "E-COR-3A", 10.0, 2.2, 45, "COR-3");
        graph.addEdge(jE, jSE, "E-COR-3B", 10.0, 2.2, 45, "COR-3");

        // 3. Add Exits & Connections
        for (Exit exit : exits) {
            Graph.Node exitNode = new Graph.Node(exit.getExitCode(), exit.getName(), "EXIT", exit.getX(), exit.getY());
            graph.addNode(exitNode);

            if ("EX-1".equalsIgnoreCase(exit.getExitCode())) {
                // Exit A connects to North-Central Junction
                graph.addEdge(exitNode, jNC, "E-EX1", 12.0, exit.getWidthMeters(), exit.getFlowRateCapacity(), null);
            } else if ("EX-2".equalsIgnoreCase(exit.getExitCode())) {
                // Exit B connects to South-Central Junction
                graph.addEdge(exitNode, jSC, "E-EX2", 12.0, exit.getWidthMeters(), exit.getFlowRateCapacity(), null);
            } else if ("EX-3".equalsIgnoreCase(exit.getExitCode())) {
                // Exit C connects to East Hub via COR-E
                graph.addEdge(jE, exitNode, "E-COR-E", 18.0, exit.getWidthMeters(), exit.getFlowRateCapacity(), "COR-E");
            } else if ("EX-4".equalsIgnoreCase(exit.getExitCode())) {
                // Main Entrance connects to West Hub via COR-W
                graph.addEdge(exitNode, jW, "E-COR-W", 15.0, exit.getWidthMeters(), exit.getFlowRateCapacity(), "COR-W");
            }

            if (!Boolean.TRUE.equals(exit.getIsAvailable())) {
                exitNode.setBlocked(true);
            }
        }

        // 4. Add Rooms & Doorways
        for (Room room : rooms) {
            Graph.Node roomNode = new Graph.Node(room.getRoomCode(), room.getName(), "ROOM", room.getCenterX(), room.getCenterY());
            graph.addNode(roomNode);

            // Connect room doors to closest junctions/corridors
            switch (room.getRoomCode()) {
                case "RM-101" -> { // Executive Boardroom
                    graph.addEdge(roomNode, jNW, "E-RM101-JNW", 6.0, 1.2, 30, null);
                }
                case "RM-102" -> { // Software Lab
                    graph.addEdge(roomNode, jN2, "E-RM102-JN2", 6.0, 1.5, 40, null);
                }
                case "RM-103" -> { // Conference Alpha
                    graph.addEdge(roomNode, jN3, "E-RM103-JN3", 6.0, 1.2, 30, null);
                }
                case "RM-104" -> { // Server Room
                    graph.addEdge(roomNode, jNE, "E-RM104-JNE", 6.0, 1.0, 20, null);
                }
                case "RM-105" -> { // Cafeteria
                    graph.addEdge(roomNode, jS2, "E-RM105-JS2", 6.0, 1.8, 50, null);
                }
                case "RM-106" -> { // Operations Center
                    graph.addEdge(roomNode, jSW, "E-RM106-JSW", 6.0, 1.2, 30, null);
                }
                case "RM-107" -> { // Open Workspace Alpha
                    graph.addEdge(roomNode, jS3, "E-RM107-JS3", 6.0, 1.5, 40, null);
                }
                case "RM-108" -> { // Open Workspace Beta
                    graph.addEdge(roomNode, jSE, "E-RM108-JSE", 6.0, 1.5, 40, null);
                }
                case "RM-109" -> { // Medical Bay
                    graph.addEdge(roomNode, jW, "E-RM109-JW", 4.0, 1.2, 25, null);
                }
                case "RM-110" -> { // Archives
                    graph.addEdge(roomNode, jE, "E-RM110-JE", 4.0, 1.0, 20, null);
                }
                default -> {
                    graph.addEdge(roomNode, jNC, "E-" + room.getRoomCode(), 10.0, 1.2, 30, null);
                }
            }
        }

        // 5. Apply Blocked Passages from Scenario
        if (blockedPassages != null) {
            for (BlockedPassage bp : blockedPassages) {
                if ("CORRIDOR".equalsIgnoreCase(bp.getBlockageType()) && bp.getCorridor() != null) {
                    graph.setCorridorBlocked(bp.getCorridor().getCorridorCode(), true);
                } else if ("EXIT".equalsIgnoreCase(bp.getBlockageType()) && bp.getExit() != null) {
                    graph.setExitBlocked(bp.getExit().getExitCode(), true);
                }
            }
        }

        return graph;
    }
}
