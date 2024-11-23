package dev.undefinedteam.gensh1n.utils.path;

import lombok.Getter;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AStarPathFinder extends PathFinder {
    private final List<Hub> hubs = new CopyOnWriteArrayList<>();
    private final List<Hub> hubsToWork = new CopyOnWriteArrayList<>();

    private static final CompareHub COMPARE = new CompareHub();

    private static final Vec3d[] flatCardinalDirections;
    private static final Vec3d[] yDirections;

    private static final double minDistanceSquared = Math.pow(3, 2);
    private static final int MAX_XZ_SEARCH = 75;

    public static List<BlockPos> scanBlocks = new ArrayList<>();
    public static List<BlockPos> pathBlocks = new ArrayList<>();

    private final boolean fastMode;

    public static void addScan(BlockPos pos) {
        if (scanBlocks.contains(pos)) return;

        scanBlocks.add(pos);
    }

    public static void addPath(BlockPos pos) {
        if (pathBlocks.contains(pos)) return;

        pathBlocks.add(pos);
    }

    static {
        final var directions = new ArrayList<Vec3d>();
        directions.add(new Vec3d(1, 0.0, 0.0));
        directions.add(new Vec3d(-1.0, 0.0, 0.0));
        directions.add(new Vec3d(0.0, 0.0, 1.0));
        directions.add(new Vec3d(0.0, 0.0, -1.0));

        flatCardinalDirections = directions.toArray(Vec3d[]::new);
        directions.clear();

        for (int y = 1; y < 10; y++) {
            directions.add(new Vec3d(0, y, 0));
            directions.add(new Vec3d(0, -y, 0));
        }

        yDirections = directions.toArray(Vec3d[]::new);
        directions.clear();
    }

    public AStarPathFinder(Vec3d startVec3, Vec3d endVec3, boolean fastMode) {
        super(startVec3, endVec3);
        this.fastMode = fastMode;
    }

    public void compute() {
        this.compute(1000, this.fastMode ? 1 : 4);
    }

    public void compute(int loops, int depth) {
        this.path.clear();
        this.hubsToWork.clear();
        List<Vec3d> initPath = new CopyOnWriteArrayList<>();
        initPath.add(this.startVec3);
        this.hubsToWork.add(new Hub(this.startVec3, null, initPath, squareDistanceTo(this.startVec3, this.endVec3), 0.0, 0.0));
        int loopCount = 0;

        loop:
        while (loopCount < loops) {
            this.hubsToWork.sort(COMPARE);
            int search = 0;
            if (this.hubsToWork.isEmpty()) break;

            int xzSearch = 0;
            for (Hub hub : this.hubsToWork) {
                if (++search > depth) break;

                this.hubsToWork.remove(hub);
                this.hubs.add(hub);
                int len = flatCardinalDirections.length;

                int dirIndex = 0;
                boolean yclip = false;
                while (dirIndex < len) {
                    Vec3d direction = flatCardinalDirections[dirIndex];
                    Vec3d pos = floor0(hub.getLoc().add(direction));
                    if (checkPositionValidity(pos) && this.addHub(hub, pos, 0.0))
                        break loop;
                    else {
                        xzSearch++;
                        if (dirIndex == len - 1) {
                            yclip = true;
                        }
                    }
                    ++dirIndex;
                }

                if (yclip || xzSearch > Math.min(MAX_XZ_SEARCH, hub.loc.distanceTo(endVec3))) {
                    len = yDirections.length;
                    dirIndex = 0;

                    if (checkVPos(hub.loc)) {
                        if (fastMode) break loop;
                        else continue loop;
                    }

                    while (dirIndex < len) {
                        Vec3d direction = yDirections[dirIndex];
                        Vec3d pos = adjustY(hub.loc, floor0(hub.getLoc().add(direction)));
                        if (checkPositionValidity(pos) && this.addHub(hub, pos, 0.0)) break loop;
                        ++dirIndex;
                    }
                }
            }
            ++loopCount;
        }

        this.hubs.sort(COMPARE);
        this.path = this.hubs.getFirst().getPath();
    }

    public Hub isHubExisting(Vec3d loc) {
        return this.hubs.parallelStream()
            .filter(hub -> hub.getLoc().equals(loc))
            .findFirst()
            .orElse(this.hubsToWork.parallelStream()
                .filter(hub -> hub.getLoc().equals(loc))
                .findFirst()
                .orElse(null));
    }

    private Vec3d adjustY(Vec3d loc, Vec3d pos) {
        double distanceToEndY = Math.abs(loc.y - endVec3.y);
        int maxStep = 10; // 最大调整步数
        if (distanceToEndY < maxStep) {
            Vec3d potentialPos = floor0(new Vec3d(pos.x, endVec3.y, pos.z));
            if (checkPositionValidity(potentialPos)) {
                return potentialPos;
            }
        }

        return pos;
    }

    private boolean checkVPos(Vec3d loc) {
        Vec3d point = null;
        for (int i = -10; i < 10; i++) {
            if (i == 0) continue;

            var pos = floor0(loc.add(0, i, 0));
            if (checkPositionValidity(pos)) {
                point = pos;
                break;
            }
        }

        return point == null;
    }


    /**
     * 检测位置是不是到达终点，，不是继续寻路
     *
     * @param parent hub
     * @param loc    cur pos
     * @param cost   cost
     * @return true if path found
     */
    public boolean addHub(Hub parent, Vec3d loc, double cost) {
        Hub existingHub = this.isHubExisting(loc);
        double totalCost = cost;
        if (parent != null) {
            totalCost += parent.getTotalCost();
        }
        if (existingHub == null) {
            if (loc.getX() == this.endVec3.getX() && loc.getY() == this.endVec3.getY() && loc.getZ() == this.endVec3.getZ() || squareDistanceTo(loc, this.endVec3) <= minDistanceSquared) {
                this.path.clear();
                assert parent != null;
                this.path = parent.getPath();
                this.path.add(loc);
                return true;
            }
            assert parent != null;
            List<Vec3d> path = new CopyOnWriteArrayList<>(parent.getPath());
            path.add(loc);
            this.hubsToWork.add(new Hub(loc, parent, path, squareDistanceTo(loc, this.endVec3), cost, totalCost));
        } else if (existingHub.getCost() > cost) {
            assert parent != null;
            List<Vec3d> path = new CopyOnWriteArrayList<>(parent.getPath());
            path.add(loc);
            existingHub.setLoc(loc);
            existingHub.setParent(parent);
            existingHub.setPath(path);
            existingHub.setSquareDistanceToFromTarget(squareDistanceTo(loc, this.endVec3));
            existingHub.setCost(cost);
            existingHub.setTotalCost(totalCost);
        }

        return false;
    }

    public static Vec3d add(Vec3d target, Vec3d v) {
        return addVector(target, v.getX(), v.getY(), v.getZ());
    }

    public static class CompareHub implements Comparator<Hub> {
        @Override
        public int compare(Hub o1, Hub o2) {
            return (int) (o1.getSquareDistanceToFromTarget() + o1.getTotalCost() - (o2.getSquareDistanceToFromTarget() + o2.getTotalCost()));
        }
    }

    @Getter
    private static class Hub {
        private Vec3d loc;
        private Hub parent;
        private List<Vec3d> path;
        private double squareDistanceToFromTarget;
        private double cost;
        private double totalCost;

        public Hub(Vec3d loc, Hub parent, List<Vec3d> path, double squareDistanceToFromTarget, double cost, double totalCost) {
            this.loc = loc;
            this.parent = parent;
            this.path = path;
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
            this.cost = cost;
            this.totalCost = totalCost;
        }

        public void setLoc(Vec3d loc) {
            this.loc = loc;
        }

        public void setParent(Hub parent) {
            this.parent = parent;
        }

        public void setPath(List<Vec3d> path) {
            this.path = path;
        }

        public void setSquareDistanceToFromTarget(double squareDistanceToFromTarget) {
            this.squareDistanceToFromTarget = squareDistanceToFromTarget;
        }

        public void setCost(double cost) {
            this.cost = cost;
        }

        public void setTotalCost(double totalCost) {
            this.totalCost = totalCost;
        }
    }
}
