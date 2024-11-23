package dev.undefinedteam.gensh1n.utils.path;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static dev.undefinedteam.gensh1n.Client.mc;
import static dev.undefinedteam.gensh1n.utils.world.BlockInfo.*;

public class PathFinder {
    protected final Vec3d startVec3;
    protected final Vec3d endVec3;
    protected List<Vec3d> path = new CopyOnWriteArrayList<>();

    public PathFinder(Vec3d startVec3, Vec3d endVec3) {
        this.startVec3 = floor0(addVector(startVec3, 0.0, 0.0, 0.0));
        this.endVec3 = floor0(addVector(endVec3, 0.0, 0.0, 0.0));
    }

    public List<Vec3d> getPath() {
        return this.path;
    }

    public void compute() {
        // 这里是计算路径的代码
        this.path.clear();

    }

    public static boolean checkPositionValidity(Vec3d loc) {
        return checkPositionValidity((int) loc.getX(), (int) loc.getY(), (int) loc.getZ());
    }

    public static boolean checkPositionValidity(BlockPos loc) {
        return checkPositionValidity(loc.getX(), loc.getY(), loc.getZ());
    }

    public static boolean checkPositionValidity(int x, int y, int z) {
        BlockPos block1 = new BlockPos(x, y, z);
        BlockPos block2 = new BlockPos(x, y + 1, z);
        BlockPos block3 = new BlockPos(x, y - 1, z);

        return
            !PathFinder.isBlockSolid(block1) &&
                !PathFinder.isBlockSolid(block2) &&
                PathFinder.isSafeToWalkOn(block3);
    }

    public static boolean canPassThrow(BlockPos pos) {
        Block block = getBlock(new BlockPos(pos.getX(), pos.getY(), pos.getZ()));
        return block instanceof AirBlock || block instanceof PlantBlock || block instanceof VineBlock || block instanceof LadderBlock || block instanceof FluidBlock || block instanceof SignBlock;
    }

    public static boolean isBlockSolid(BlockPos block) {
        var blockState = getBlockState(block);
        var b = getBlock(block);

        return (
            ((blockState.isSolidBlock(mc.world, block) && blockState.isFullCube(mc.world, block))) || b instanceof SlabBlock || b instanceof StairsBlock || b instanceof CactusBlock || b instanceof ChestBlock || b instanceof EnderChestBlock || b instanceof SkullBlock || b instanceof PaneBlock ||
                b instanceof FenceBlock || b instanceof WallBlock || b instanceof TransparentBlock || b instanceof PistonBlock || b instanceof PistonExtensionBlock || b instanceof PistonHeadBlock || b instanceof StainedGlassBlock ||
                b instanceof TrapdoorBlock ||
                // 1.14+
                b instanceof BambooBlock || b instanceof BellBlock ||
                b instanceof CakeBlock || b instanceof RedstoneBlock ||
                b instanceof LeavesBlock || b instanceof SnowBlock ||
                // 1.19+
                b instanceof SculkSensorBlock || b instanceof SculkShriekerBlock ||
                // lag back
                b instanceof DoorBlock
        );
    }

    private static boolean isSafeToWalkOn(BlockPos block) {
        var b = getBlock(block);
        return (!(b instanceof FenceBlock) && !(b instanceof WallBlock));
    }

    public static Vec3d addVector(Vec3d target, double x, double y, double z) {
        return target.add(x, y, z);
    }

    public static Vec3d scale(Vec3d target, double s) {
        return new Vec3d(target.x * s, target.y * s, target.z * s);
    }

    public static Vec3d floor0(Vec3d vec) {
        return new Vec3d(MathHelper.floor(vec.x), MathHelper.floor(vec.y), MathHelper.floor(vec.z));
    }

    public static double squareDistanceTo(Vec3d target, Vec3d vec) {
        return Math.pow(target.x - vec.x, 2.0) + Math.pow(target.y - vec.y, 2.0) + Math.pow(target.z - vec.z, 2.0);
    }
}
