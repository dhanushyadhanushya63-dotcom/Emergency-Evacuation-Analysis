package com.evacuation.service.algorithm;

import java.util.*;

/**
 * Breadth-First Search (BFS) Alternative Route Pathfinder.
 * Explores unweighted topological paths to alternative exits, useful for
 * secondary evacuation routes when primary corridors are congested.
 */
public class BfsPathfinder {

    public DijkstraPathfinder.PathResult findAlternativeRoute(Graph graph, String startNodeId,
                                                              String primaryExitId, double walkingSpeed) {
        Graph.Node startNode = graph.getNode(startNodeId);
        if (startNode == null || startNode.isBlocked()) {
            return DijkstraPathfinder.PathResult.notFound(startNode);
        }

        Queue<Graph.Node> queue = new LinkedList<>();
        Map<String, Graph.Node> prevNode = new HashMap<>();
        Map<String, Graph.Edge> prevEdge = new HashMap<>();
        Set<String> visited = new HashSet<>();

        queue.add(startNode);
        visited.add(startNode.getId());

        Graph.Node alternativeExit = null;

        while (!queue.isEmpty()) {
            Graph.Node u = queue.poll();

            // If we hit an exit that is NOT the primary exit and is NOT blocked
            if ("EXIT".equalsIgnoreCase(u.getType()) && !u.isBlocked() && !u.getId().equalsIgnoreCase(primaryExitId)) {
                alternativeExit = u;
                break;
            }

            for (Graph.Edge edge : graph.getNeighbors(u.getId())) {
                if (edge.isBlocked()) continue;

                Graph.Node v = edge.getTarget();
                if (v.isBlocked() || visited.contains(v.getId())) continue;

                visited.add(v.getId());
                prevNode.put(v.getId(), u);
                prevEdge.put(v.getId(), edge);
                queue.add(v);
            }
        }

        if (alternativeExit == null) {
            return DijkstraPathfinder.PathResult.notFound(startNode);
        }

        // Reconstruct alternative path
        List<Graph.Node> pathNodes = new ArrayList<>();
        List<Graph.Edge> pathEdges = new ArrayList<>();
        double totalDistance = 0.0;

        Graph.Node curr = alternativeExit;
        while (curr != null && !curr.equals(startNode)) {
            pathNodes.add(0, curr);
            Graph.Edge edge = prevEdge.get(curr.getId());
            if (edge != null) {
                pathEdges.add(0, edge);
                totalDistance += edge.getDistanceMeters();
            }
            curr = prevNode.get(curr.getId());
        }
        pathNodes.add(0, startNode);

        double walkingTime = walkingSpeed > 0 ? (totalDistance / walkingSpeed) : totalDistance;
        return new DijkstraPathfinder.PathResult(true, startNode, alternativeExit, pathNodes, pathEdges, totalDistance, walkingTime);
    }
}
