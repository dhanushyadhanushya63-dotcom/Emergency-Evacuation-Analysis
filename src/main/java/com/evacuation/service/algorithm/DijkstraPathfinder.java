package com.evacuation.service.algorithm;

import java.util.*;

/**
 * Dijkstra's Shortest Path Algorithm for Emergency Evacuation.
 * Computes the optimal evacuation route from an origin room to the nearest
 * available exit, strictly bypassing any blocked corridors or disabled exits.
 */
public class DijkstraPathfinder {

    public static class PathResult {
        private final boolean found;
        private final Graph.Node startNode;
        private final Graph.Node targetExit;
        private final List<Graph.Node> pathNodes;
        private final List<Graph.Edge> pathEdges;
        private final double totalDistanceMeters;
        private final double walkingDurationSeconds;

        public PathResult(boolean found, Graph.Node startNode, Graph.Node targetExit,
                          List<Graph.Node> pathNodes, List<Graph.Edge> pathEdges,
                          double totalDistanceMeters, double walkingDurationSeconds) {
            this.found = found;
            this.startNode = startNode;
            this.targetExit = targetExit;
            this.pathNodes = pathNodes != null ? pathNodes : Collections.emptyList();
            this.pathEdges = pathEdges != null ? pathEdges : Collections.emptyList();
            this.totalDistanceMeters = totalDistanceMeters;
            this.walkingDurationSeconds = walkingDurationSeconds;
        }

        public static PathResult notFound(Graph.Node startNode) {
            return new PathResult(false, startNode, null, null, null, 0.0, 0.0);
        }

        public boolean isFound() { return found; }
        public Graph.Node getStartNode() { return startNode; }
        public Graph.Node getTargetExit() { return targetExit; }
        public List<Graph.Node> getPathNodes() { return pathNodes; }
        public List<Graph.Edge> getPathEdges() { return pathEdges; }
        public double getTotalDistanceMeters() { return totalDistanceMeters; }
        public double getWalkingDurationSeconds() { return walkingDurationSeconds; }
    }

    private static class NodeDistance implements Comparable<NodeDistance> {
        final Graph.Node node;
        final double distance;

        NodeDistance(Graph.Node node, double distance) {
            this.node = node;
            this.distance = distance;
        }

        @Override
        public int compareTo(NodeDistance other) {
            return Double.compare(this.distance, other.distance);
        }
    }

    /**
     * Finds the shortest reachable route from startNode to the closest available Exit.
     *
     * @param graph        The building graph
     * @param startNodeId  The origin room ID
     * @param walkingSpeed Configured walking speed in meters per second
     * @return PathResult containing the path and distance, or notFound if trapped
     */
    public PathResult findShortestPathToNearestExit(Graph graph, String startNodeId, double walkingSpeed) {
        Graph.Node startNode = graph.getNode(startNodeId);
        if (startNode == null || startNode.isBlocked()) {
            return PathResult.notFound(startNode);
        }

        Map<String, Double> dist = new HashMap<>();
        Map<String, Graph.Node> prevNode = new HashMap<>();
        Map<String, Graph.Edge> prevEdge = new HashMap<>();
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        for (Graph.Node node : graph.getAllNodes()) {
            dist.put(node.getId(), Double.POSITIVE_INFINITY);
        }

        dist.put(startNodeId, 0.0);
        pq.add(new NodeDistance(startNode, 0.0));

        Graph.Node nearestExit = null;

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            Graph.Node u = current.node;

            if (visited.contains(u.getId())) continue;
            visited.add(u.getId());

            // Check if we reached an available exit
            if ("EXIT".equalsIgnoreCase(u.getType()) && !u.isBlocked()) {
                nearestExit = u;
                break;
            }

            for (Graph.Edge edge : graph.getNeighbors(u.getId())) {
                if (edge.isBlocked()) continue;

                Graph.Node v = edge.getTarget();
                if (v.isBlocked() || visited.contains(v.getId())) continue;

                double newDist = dist.get(u.getId()) + edge.getDistanceMeters();
                if (newDist < dist.get(v.getId())) {
                    dist.put(v.getId(), newDist);
                    prevNode.put(v.getId(), u);
                    prevEdge.put(v.getId(), edge);
                    pq.add(new NodeDistance(v, newDist));
                }
            }
        }

        if (nearestExit == null) {
            return PathResult.notFound(startNode);
        }

        // Reconstruct path
        List<Graph.Node> pathNodes = new ArrayList<>();
        List<Graph.Edge> pathEdges = new ArrayList<>();

        Graph.Node curr = nearestExit;
        while (curr != null && !curr.equals(startNode)) {
            pathNodes.add(0, curr);
            Graph.Edge edge = prevEdge.get(curr.getId());
            if (edge != null) {
                pathEdges.add(0, edge);
            }
            curr = prevNode.get(curr.getId());
        }
        pathNodes.add(0, startNode);

        double totalDistance = dist.get(nearestExit.getId());
        double walkingTime = walkingSpeed > 0 ? (totalDistance / walkingSpeed) : totalDistance;

        return new PathResult(true, startNode, nearestExit, pathNodes, pathEdges, totalDistance, walkingTime);
    }
}
