package dev.undefinedteam.gensh1n.utils.path;

import dev.undefinedteam.gensh1n.system.ClientConfig;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static dev.undefinedteam.gensh1n.Client.mc;

public class TeleportPath {
    public static void teleport(FinderType type, Vec3d from, Vec3d to) {
        teleport(type, from, to, true, null);
    }

    public static void teleport(FinderType type, Vec3d from, Vec3d to, boolean back, Runnable task) {
        CopyOnWriteArrayList<Vec3d> path = computePath(type, from, to);
        for (Vec3d pathElm : path) {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
        }
        if (!back) mc.player.updatePosition(to.x, to.y, to.z);
        if (task != null) task.run();

        if (back) {
            Collections.reverse(path);
            for (Vec3d pathElm : path) {
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
            }
        }
    }

    public static void teleportTo(FinderType type, Vec3d to, boolean back, Runnable task) {
        teleport(type, mc.player.getPos(), to, back, task);
    }

    public static void teleportTo(FinderType type, Vec3d to) {
        teleport(type, mc.player.getPos(), to, false, null);
    }

    public static void teleportTo(FinderType type, Vec3d from, Vec3d to) {
        teleport(type, from, to, false, null);
    }

    public static CopyOnWriteArrayList<Vec3d> computePath(FinderType type, Vec3d topFrom, Vec3d to) {
//        AStarPathFinder.scanBlocks.clear();
//        long start = System.currentTimeMillis();
        // 检测起点是否可以穿过
        if (!PathFinder.canPassThrow(BlockPos.ofFloored(topFrom))) {
            topFrom = PathFinder.addVector(topFrom, 0, 1, 0);
        }

        int i = 0;
        Vec3d lastLoc = null;
        Vec3d lastDashLoc = null;
        CopyOnWriteArrayList<Vec3d> path = new CopyOnWriteArrayList<>();
        List<Vec3d> pathFinderPath = type.get(topFrom, to, ClientConfig.get().fastAStar.get()).getPath();
        for (Vec3d pathElm : pathFinderPath) {
            if (i == 0 || i == pathFinderPath.size() - 1) {
                if (lastLoc != null) {
                    path.add(PathFinder.addVector(lastLoc, 0.5, 0, 0.5));
                }
                path.add(PathFinder.addVector(pathElm, 0.5, 0, 0.5));
                lastDashLoc = pathElm;
            } else {
                boolean canContinue = true;

                double dashDistance = 5.0D;
                if (PathFinder.squareDistanceTo(pathElm, lastDashLoc) > dashDistance * dashDistance) {
                    canContinue = false;
                } else {
                    double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
                    double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
                    double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
                    double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
                    double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
                    double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());
                    cordsLoop:
                    for (int x = (int) smallX; x <= bigX; x++) {
                        for (int y = (int) smallY; y <= bigY; y++) {
                            for (int z = (int) smallZ; z <= bigZ; z++) {
                                if (!PathFinder.checkPositionValidity(x, y, z)) {
                                    canContinue = false;
                                    break cordsLoop;
                                }
                            }
                        }
                    }
                }


                if (!canContinue) {
                    path.add(PathFinder.addVector(lastLoc, 0.5, 0, 0.5));
                    lastDashLoc = lastLoc;
                }
            }
            lastLoc = pathElm;
            i++;
        }

//        System.out.println("compute: " + (System.currentTimeMillis() - start) + "ms");
//        System.out.println("scan: " + AStarPathFinder.scanBlocks.size() + "blocks");
//        System.out.println("route_size: " + path.size());
//        System.out.println("distance: " + topFrom.distanceTo(to));
        return path;
    }
}
