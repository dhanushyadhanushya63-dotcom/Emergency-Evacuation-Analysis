package com.evacuation.service.algorithm;

import java.util.*;

/**
 * Graph data structure representing the building's navigable topological network.
 * Vertices are rooms, intersections/junctions, and exits.
 * Edges represent corridors, doorways, and pathways with physical lengths and capacities.
 */
public class Graph {

    public static class Node {
        private final String id;
        private final String label;
        private final String type; // ROOM, JUNCTION, EXIT
        private final double x;
        private final double y;
        private boolean blocked;

        public Node(String id, String label, String type, double x, double y) {
            this.id = id;
            this.label = label;
            this.type = type;
            this.x = x;
            this.y = y;
            this.blocked = false;
        }

        public String getId() { return id; }
        public String getLabel() { return label; }
        public String getType() { return type; }
        public double getX() { return x; }
        public double getY() { return y; }
        public boolean isBlocked() { return blocked; }
        public void setBlocked(boolean blocked) { this.blocked = blocked; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(id, node.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return id + " (" + type + ")";
        }
    }

    public static class Edge {
        private final String id;
        private final Node source;
        private final Node target;
        private final double distanceMeters;
        private final double widthMeters;
        private final int flowCapacity; // max occupants per minute
        private final String corridorCode; // null if interior room door
        private boolean blocked;

        public Edge(String id, Node source, Node target, double distanceMeters,
                    double widthMeters, int flowCapacity, String corridorCode) {
            this.id = id;
            this.source = source;
            this.target = target;
            this.distanceMeters = distanceMeters;
            this.widthMeters = widthMeters;
            this.flowCapacity = flowCapacity;
            this.corridorCode = corridorCode;
            this.blocked = false;
        }

        public String getId() { return id; }
        public Node getSource() { return source; }
        public Node getTarget() { return target; }
        public double getDistanceMeters() { return distanceMeters; }
        public double getWidthMeters() { return widthMeters; }
        public int getFlowCapacity() { return flowCapacity; }
        public String getCorridorCode() { return corridorCode; }
        public boolean isBlocked() { return blocked; }
        public void setBlocked(boolean blocked) { this.blocked = blocked; }
    }

    private final Map<String, Node> nodes = new LinkedHashMap<>();
    private final Map<String, List<Edge>> adjacencyList = new LinkedHashMap<>();
    private final List<Edge> allEdges = new ArrayList<>();

    public void addNode(Node node) {
        nodes.put(node.getId(), node);
        adjacencyList.putIfAbsent(node.getId(), new ArrayList<>());
    }

    public void addEdge(Node u, Node v, String edgeId, double distance, double width, int capacity, String corridorCode) {
        addNode(u);
        addNode(v);

        Edge e1 = new Edge(edgeId + "-fwd", u, v, distance, width, capacity, corridorCode);
        Edge e2 = new Edge(edgeId + "-rev", v, u, distance, width, capacity, corridorCode);

        adjacencyList.get(u.getId()).add(e1);
        adjacencyList.get(v.getId()).add(e2);

        allEdges.add(e1);
        allEdges.add(e2);
    }

    public Node getNode(String id) {
        return nodes.get(id);
    }

    public Collection<Node> getAllNodes() {
        return nodes.values();
    }

    public List<Edge> getNeighbors(String nodeId) {
        return adjacencyList.getOrDefault(nodeId, Collections.emptyList());
    }

    public List<Edge> getAllEdges() {
        return allEdges;
    }

    public void setCorridorBlocked(String corridorCode, boolean blocked) {
        for (Edge edge : allEdges) {
            if (corridorCode.equalsIgnoreCase(edge.getCorridorCode())) {
                edge.setBlocked(blocked);
            }
        }
    }

    public void setExitBlocked(String exitCode, boolean blocked) {
        Node node = nodes.get(exitCode);
        if (node != null) {
            node.setBlocked(blocked);
        }
    }

    public void clearAllBlockages() {
        for (Node node : nodes.values()) {
            node.setBlocked(false);
        }
        for (Edge edge : allEdges) {
            edge.setBlocked(false);
        }
    }
}
