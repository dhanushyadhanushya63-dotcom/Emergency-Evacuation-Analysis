package com.evacuation;

import com.evacuation.service.algorithm.DijkstraPathfinder;
import com.evacuation.service.algorithm.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DijkstraPathfinderTest {

    private Graph graph;
    private DijkstraPathfinder pathfinder;

    @BeforeEach
    public void setup() {
        graph = new Graph();
        pathfinder = new DijkstraPathfinder();

        // Nodes
        Graph.Node room = new Graph.Node("RM-101", "Room 101", "ROOM", 100, 100);
        Graph.Node j1 = new Graph.Node("J-1", "Junction 1", "JUNCTION", 150, 100);
        Graph.Node j2 = new Graph.Node("J-2", "Junction 2", "JUNCTION", 250, 100);
        Graph.Node exitA = new Graph.Node("EX-1", "Exit A", "EXIT", 250, 50);
        Graph.Node exitB = new Graph.Node("EX-2", "Exit B", "EXIT", 350, 100);

        graph.addNode(room);
        graph.addNode(j1);
        graph.addNode(j2);
        graph.addNode(exitA);
        graph.addNode(exitB);

        // Edges
        graph.addEdge(room, j1, "E1", 10.0, 2.0, 50, "COR-1");
        graph.addEdge(j1, j2, "E2", 20.0, 2.0, 50, "COR-2");
        graph.addEdge(j2, exitA, "E3", 10.0, 2.4, 50, null);
        graph.addEdge(j2, exitB, "E4", 25.0, 2.4, 50, null);
    }

    @Test
    @DisplayName("Finds shortest path to closest exit in unblocked graph")
    public void testShortestPathNormal() {
        DijkstraPathfinder.PathResult result = pathfinder.findShortestPathToNearestExit(graph, "RM-101", 1.2);

        assertTrue(result.isFound(), "Path should be found");
        assertEquals("EX-1", result.getTargetExit().getId(), "Closest exit should be EX-1");
        assertEquals(40.0, result.getTotalDistanceMeters(), 0.001, "Distance should be 10 + 20 + 10 = 40m");
        assertTrue(result.getWalkingDurationSeconds() > 0);
    }

    @Test
    @DisplayName("Reroutes to alternative exit when primary exit is blocked")
    public void testReroutingWhenExitBlocked() {
        graph.setExitBlocked("EX-1", true);

        DijkstraPathfinder.PathResult result = pathfinder.findShortestPathToNearestExit(graph, "RM-101", 1.2);

        assertTrue(result.isFound(), "Alternative path should be found");
        assertEquals("EX-2", result.getTargetExit().getId(), "Should reroute to available EX-2");
        assertEquals(55.0, result.getTotalDistanceMeters(), 0.001, "Distance should be 10 + 20 + 25 = 55m");
    }

    @Test
    @DisplayName("Reports trapped when all routes to exits are blocked")
    public void testTrappedWhenCorridorBlocked() {
        graph.setCorridorBlocked("COR-1", true);

        DijkstraPathfinder.PathResult result = pathfinder.findShortestPathToNearestExit(graph, "RM-101", 1.2);

        assertFalse(result.isFound(), "Room should be trapped when connecting corridor is blocked");
    }
}
