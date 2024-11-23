package dev.undefinedteam.gensh1n.utils.path;

import net.minecraft.util.math.Vec3d;

import java.util.*;

import static dev.undefinedteam.gensh1n.Client.mc;
import static dev.undefinedteam.gensh1n.utils.world.BlockInfo.*;

public class BidirectionalAPathFinder extends PathFinder {
    public BidirectionalAPathFinder(Vec3d startVec3, Vec3d endVec3) {
        super(startVec3, endVec3);
    }

    public void compute() {
        this.path.clear();
        bidirectionalAStar();
    }

    private void bidirectionalAStar() {
        PriorityQueue<Node> openSetStart = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fCost));
        PriorityQueue<Node> openSetEnd = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fCost));
        Map<Vec3d, Node> allNodesStart = new HashMap<>();
        Map<Vec3d, Node> allNodesEnd = new HashMap<>();
        Set<Vec3d> closedSetStart = new HashSet<>();
        Set<Vec3d> closedSetEnd = new HashSet<>();

        Node startNode = new Node(null, startVec3, 0, heuristic(startVec3, endVec3));
        Node endNode = new Node(null, endVec3, 0, heuristic(endVec3, startVec3));
        openSetStart.add(startNode);
        openSetEnd.add(endNode);
        allNodesStart.put(startVec3, startNode);
        allNodesEnd.put(endVec3, endNode);

        while (!openSetStart.isEmpty() && !openSetEnd.isEmpty()) {
            if (searchStep(openSetStart, closedSetStart, allNodesStart, allNodesEnd, true)) {
                return;
            }
            if (searchStep(openSetEnd, closedSetEnd, allNodesEnd, allNodesStart, false)) {
                return;
            }
        }
    }

    private boolean searchStep(PriorityQueue<Node> openSet, Set<Vec3d> closedSet, Map<Vec3d, Node> allNodesThisSide, Map<Vec3d, Node> allNodesOtherSide, boolean fromStart) {
        if (openSet.isEmpty()) return false;

        Node current = openSet.poll();
        closedSet.add(current.position);

        for (Vec3d neighbor : getNeighbors(current.position)) {
            if (!checkPositionValidity(neighbor) || closedSet.contains(neighbor)) continue;

            double tentativeGCost = current.gCost + current.position.distanceTo(neighbor);
            Node neighborNode = allNodesThisSide.getOrDefault(neighbor, new Node(null, neighbor, Double.MAX_VALUE, heuristic(neighbor, fromStart ? endVec3 : startVec3)));

            if (tentativeGCost < neighborNode.gCost) {
                neighborNode.parent = current;
                neighborNode.gCost = tentativeGCost;
                neighborNode.fCost = neighborNode.gCost + neighborNode.hCost;

                if (!openSet.contains(neighborNode)) {
                    openSet.add(neighborNode);
                    allNodesThisSide.put(neighbor, neighborNode);
                }

                if (allNodesOtherSide.containsKey(neighbor)) {
                    reconstructPath(neighborNode, allNodesOtherSide.get(neighbor));
                    return true;
                }
            }
        }
        return false;
    }

    private void reconstructPath(Node meetNodeFromStart, Node meetNodeFromEnd) {
        List<Vec3d> pathFromStart = new ArrayList<>();
        for (Node node = meetNodeFromStart; node != null; node = node.parent) {
            pathFromStart.add(node.position);
        }
        Collections.reverse(pathFromStart);

        List<Vec3d> pathFromEnd = new ArrayList<>();
        for (Node node = meetNodeFromEnd; node != null; node = node.parent) {
            pathFromEnd.add(node.position);
        }

        path.addAll(pathFromStart);
        path.addAll(pathFromEnd.subList(1, pathFromEnd.size())); // avoid duplicating the meeting point
    }

    private double heuristic(Vec3d a, Vec3d b) {
        return a.distanceTo(b);
    }

    private List<Vec3d> getNeighbors(Vec3d position) {
        List<Vec3d> neighbors = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    neighbors.add(addVector(position, x, y, z));
                }
            }
        }
        return neighbors;
    }

    private static class Node {
        Node parent;
        Vec3d position;
        double gCost; // Cost from start node
        double hCost; // Heuristic cost to end node
        double fCost; // Total cost (gCost + hCost)

        Node(Node parent, Vec3d position, double gCost, double hCost) {
            this.parent = parent;
            this.position = position;
            this.gCost = gCost;
            this.hCost = hCost;
            this.fCost = gCost + hCost;
        }
    }
}
