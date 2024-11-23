package dev.undefinedteam.gensh1n.utils.world;

import dev.undefinedteam.gensh1n.rotate.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/**
 * @Author KuChaZi
 * @Date 2024/11/13 21:42
 * @ClassName: ScaUtil
 */
public class ScaUtil {
    public static class PlaceRotation {
        private final PlaceInfo placeInfo;
        private final Rotation rotation;

        public PlaceRotation(final PlaceInfo position, final Rotation facing) {
            this.placeInfo = position;
            this.rotation = facing;
        }

        public PlaceInfo getPlaceInfo() {
            return placeInfo;
        }

        public Rotation getRotation() {
            return rotation;
        }
    }

    public static class PlaceInfo {
        private final BlockPos blockPos;
        private final Direction enumFacing;
        private final Vec3d hitVec;

        public PlaceInfo(final BlockPos position, final Direction facing, final Vec3d hitVec) {
            this.blockPos = position;
            this.enumFacing = facing;
            this.hitVec = hitVec;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public Direction getEnumFacing() {
            return enumFacing;
        }

        public Vec3d getHitVec() {
            return hitVec;
        }
    }
}
