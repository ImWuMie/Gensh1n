package dev.undefinedteam.gensh1n.utils.path;

import net.minecraft.util.math.Vec3d;

public enum FinderType {
    AStar,
    BidirectionalA,
    BestFS;

    public PathFinder get(Vec3d start, Vec3d end, boolean fastAStar) {
        if (this == AStar) {
            var finder = new AStarPathFinder(start, end, fastAStar);
            finder.compute();
            return finder;
        } else if (this == BidirectionalA) {
            var finder = new BidirectionalAPathFinder(start, end);
            finder.compute();
            return finder;
        } else if (this == BestFS) {
            var finder = new GreedyBestFSPathFinder(start, end);
            finder.compute();
            return finder;
        }

        return null;
    }
}
