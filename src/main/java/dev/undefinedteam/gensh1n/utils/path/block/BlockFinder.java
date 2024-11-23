package dev.undefinedteam.gensh1n.utils.path.block;

import dev.undefinedteam.gensh1n.utils.Utils;
import dev.undefinedteam.gensh1n.utils.raytrace.RayTraceUtils;
import dev.undefinedteam.gensh1n.utils.world.BlockInfo;
import lombok.AllArgsConstructor;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import static dev.undefinedteam.gensh1n.Client.mc;

public class BlockFinder {
    static final BlockPos[] directions = {
        new BlockPos(1, 0, 0),
        new BlockPos(-1, 0, 0),
        new BlockPos(0, 1, 0),
        new BlockPos(0, -1, 0),
        new BlockPos(0, 0, 1),
        new BlockPos(0, 0, -1),
    };

    public final BlockPos startPos;
    public final int range;

    private final PriorityQueue<Node> frontier = new PriorityQueue<>(Comparator.comparingDouble(a -> a.cost));
    private final List<BlockPos> path = new CopyOnWriteArrayList<>();

    private final List<BlockPos> tPath = new CopyOnWriteArrayList<>();

    public List<BlockPos> getPath() {
        return path;
    }

    public List<BlockPos> getRenderPath() {
        if (tPath.isEmpty()) {
            for (BlockPos blockPos : path) {
                tPath.add(blockPos);

                if (hasNeighbors(blockPos)) break;
            }
        }

        return tPath;
    }

    public BlockFinder(BlockPos startPos, int range) {
        this.startPos = startPos;
        this.range = range;
    }

    public void compute() {
        Node startNode = new Node(this.startPos, null, 0, 0);
        frontier.add(startNode);

        int maxIterations = range * range * range;

        int iterations = 0;
        while (!frontier.isEmpty()) {
            Node current = frontier.poll();
            if (current.distance > range) {
                return;
            }

            if (hasNeighbors(current.pos)) {
                reconstructPath(current);
                return;
            }

            for (var neighborPos : getNeighbors(current.pos)) {
                var distanceToStart = Utils.distance(neighborPos, startPos);
                if (distanceToStart > range) {
                    continue;
                }

                if (checkPosValid(neighborPos)) {
                    double newCost = current.cost + Utils.distance(neighborPos, current.pos);
                    Node neighborNode = new Node(neighborPos, current, newCost, distanceToStart);
                    frontier.add(neighborNode);
                }
            }

            iterations++;
            if (iterations >= maxIterations) {
                break;
            }
        }
    }

    private void reconstructPath(Node endNode) {
        Node current = endNode;
        while (current != null) {
            this.path.addFirst(current.pos);
            current = current.previous;
        }
    }

    public static boolean hasNeighbors(BlockPos pos) {
        final BlockPos[] directions = {
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, -1, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
        };

        for (BlockPos direction : directions) {
            var d = pos.add(direction);
            var state = mc.world.getBlockState(d);
            if (!state.isAir() && !state.isReplaceable() && !isUnClickable(state.getBlock())) {
                return true;
            }
        }

        return false;
    }


    private boolean checkPosValid(BlockPos pos) {
        if (RayTraceUtils.canSeePointFrom(startPos.toCenterPos(), pos.toCenterPos())) {
            if (isUnClickable(mc.world.getBlockState(pos).getBlock())) return false;
            if (!canPlaceBlock(pos, true, BlockInfo.getBlock(pos))) return false;

            return true;
        }

        return false;
    }

    public static boolean canPlaceBlock(BlockPos blockPos, boolean checkEntities, Block block) {
        if (blockPos == null) return false;
        if (!World.isValid(blockPos)) return false;
        if (!mc.world.getBlockState(blockPos).isReplaceable()) return false;
        return !checkEntities || mc.world.canPlace(block.getDefaultState(), blockPos, ShapeContext.absent());
    }

    public static boolean isUnClickable(Block block) {
        return block instanceof CraftingTableBlock
            || block instanceof AnvilBlock
            || block instanceof LoomBlock
            || block instanceof CartographyTableBlock
            || block instanceof GrindstoneBlock
            || block instanceof StonecutterBlock
            || block instanceof ButtonBlock
            || block instanceof AbstractPressurePlateBlock
            || block instanceof BlockWithEntity
            || block instanceof BedBlock
            || block instanceof FenceGateBlock
            || block instanceof DoorBlock
            || block instanceof NoteBlock
            || block instanceof TrapdoorBlock;
    }

    private List<BlockPos> getNeighbors(BlockPos pos) {
        List<BlockPos> neighbors = new CopyOnWriteArrayList<>();


        for (var direction : directions) {
            var neighbor = pos.add(direction);
            neighbors.add(neighbor);
        }

        return neighbors;
    }

    @AllArgsConstructor
    static class Node {
        public BlockPos pos;
        public Node previous;
        public double cost;
        public double distance;
    }
}
