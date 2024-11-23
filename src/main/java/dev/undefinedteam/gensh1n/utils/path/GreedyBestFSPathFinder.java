package dev.undefinedteam.gensh1n.utils.path;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import static dev.undefinedteam.gensh1n.Client.mc;
import static dev.undefinedteam.gensh1n.utils.world.BlockInfo.*;

public class GreedyBestFSPathFinder extends PathFinder {
    private static final long TIME_LIMIT_MS = 10000; // ms
    private static final int MAX_ITERATIONS = 50000;

    public GreedyBestFSPathFinder(Vec3d startVec3, Vec3d endVec3) {
        super(startVec3, endVec3);
    }

    @Override
    public void compute() {
        this.path.clear();

        PriorityQueue<Node> frontier = new PriorityQueue<>((a, b) -> Double.compare(a.priority, b.priority));
        Node startNode = new Node(this.startVec3, null, 0, heuristic(this.startVec3, this.endVec3));
        frontier.add(startNode);

        long startTime = System.currentTimeMillis();
        int iterations = 0;

        while (!frontier.isEmpty()) {
            Node current = frontier.poll();

            // 检查是否超时或超过最大迭代次数
            if (System.currentTimeMillis() - startTime > TIME_LIMIT_MS || iterations > MAX_ITERATIONS) {
                reconstructPath(current);
                return;
            }

            if (current != null && current.position.equals(this.endVec3)) {
                reconstructPath(current);
                return;
            }

            for (Vec3d neighborPos : getNeighbors(current.position)) {
                if (checkPositionValidity(neighborPos)) {
                    double newCost = current.cost + current.position.distanceTo(neighborPos);  // 假设移动到邻居的成本为1
                    Node neighborNode = new Node(neighborPos, current, newCost, heuristic(neighborPos, this.endVec3));
                    frontier.add(neighborNode);
                    AStarPathFinder.addScan(BlockPos.ofFloored(neighborPos));
                }
            }
            iterations++;
        }
    }

    private void reconstructPath(Node endNode) {
        Node current = endNode;
        while (current != null) {
            this.path.addFirst(current.position);
            current = current.previous;
        }
    }

    private double heuristic(Vec3d a, Vec3d b) {
        return a.distanceTo(b);
    }

    private List<Vec3d> getNeighbors(Vec3d pos) {
        List<Vec3d> neighbors = new CopyOnWriteArrayList<>();
        final int[][] directions = {
            {1, 0, 0}, {-1, 0, 0},
            {0, 1, 0}, {0, -1, 0},
            {0, 0, 1}, {0, 0, -1}
        };

        for (int[] direction : directions) {
            Vec3d neighbor = addVector(pos, direction[0], direction[1], direction[2]);
            neighbors.add(neighbor);
        }

        for (int y = 2; y <= 10;y++) {
            Vec3d neighbor = addVector(pos, 0, y, 0);
            Vec3d neighbor1 = addVector(pos, 0, -y, 0);
            neighbors.add(neighbor);
            neighbors.add(neighbor1);
        }

        return neighbors;
    }

    private static class Node {
        Vec3d position;
        Node previous;
        double cost;
        double priority;

        Node(Vec3d position, Node previous, double cost, double priority) {
            this.position = position;
            this.previous = previous;
            this.cost = cost;
            this.priority = priority;
        }
    }

}
