package dev.undefinedteam.gensh1n.utils.raytrace;

import dev.undefinedteam.gensh1n.rotate.Rotation;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import static dev.undefinedteam.gensh1n.Client.mc;

public class RayTraceUtils{
    public static BlockHitResult rayTraceCollidingBlocks(Vec3d start, Vec3d end) {
        BlockHitResult result = mc.world.raycast(new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.ANY,
                mc.player
        ));

        if (result == null || result.getType() != HitResult.Type.BLOCK)
            return null;

        return result;
    }

    public static Entity raytraceEntity(double range, Rotation rotation, EntityFilter filter) {
        Entity entity = mc.cameraEntity;
        if (entity == null) return null;

        Vec3d cameraVec = entity.getEyePos();
        Vec3d rotationVec = rotation.getRotationVec();

        Vec3d vec3d3 = cameraVec.add(rotationVec.x * range, rotationVec.y * range, rotationVec.z * range);
        Box box = entity.getBoundingBox().stretch(rotationVec.multiply(range)).expand(1.0, 1.0, 1.0);

        HitResult entityHitResult = ProjectileUtil.raycast(
                entity,
                cameraVec,
                vec3d3,
                box,
                e -> !e.isSpectator() && e.canHit() && filter.test(e),
                range * range
        );

        return entityHitResult == null ? null : ((EntityHitResult) entityHitResult).getEntity();
    }

    public static BlockHitResult raytraceBlock(double range, Rotation rotation, BlockPos pos, BlockState state) {
        Entity entity = mc.cameraEntity;
        if (entity == null) return null;

        Vec3d start = entity.getEyePos();
        Vec3d rotationVec = rotation.getRotationVec();

        Vec3d end = start.add(rotationVec.x * range, rotationVec.y * range, rotationVec.z * range);

        return mc.world.raycastBlock(
                start,
                end,
                pos,
                state.getOutlineShape(mc.world, pos, ShapeContext.of(mc.player)),
                state
        );
    }

    public static BlockHitResult raycast(double range, Rotation rotation, boolean includeFluids, float tickDelta) {
        Entity entity = mc.cameraEntity;
        if (entity == null) return null;

        Vec3d start = entity.getCameraPosVec(tickDelta);
        Vec3d rotationVec = rotation.getRotationVec();

        Vec3d end = start.add(rotationVec.x * range, rotationVec.y * range, rotationVec.z * range);

        return mc.world.raycast(new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.OUTLINE,
                includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE,
                entity
        ));
    }

    public static boolean canSeePointFrom(Vec3d eyes, Vec3d vec3) {
        return mc.world.raycast(new RaycastContext(
                eyes,
                vec3,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                mc.player
        )).getType() == HitResult.Type.MISS;
    }

    public static HitResult raycast(Vec3d eyes, Vec3d vec3) {
        return mc.world.raycast(new RaycastContext(
            eyes,
            vec3,
            RaycastContext.ShapeType.OUTLINE,
            RaycastContext.FluidHandling.NONE,
            mc.player
        ));
    }

    public static boolean facingEnemy(Entity toEntity, double range, Rotation rotation) {
        return raytraceEntity(range, rotation, e -> e == toEntity) != null;
    }

    public static boolean facingEnemy(Entity fromEntity, Entity toEntity, Rotation rotation, double range, double wallsRange) {
        Vec3d cameraVec = fromEntity.getEyePos();
        Vec3d rotationVec = rotation.getRotationVec();

        double rangeSquared = range * range;
        double wallsRangeSquared = wallsRange * wallsRange;

        Vec3d vec3d3 = cameraVec.add(rotationVec.x * range, rotationVec.y * range, rotationVec.z * range);
        Box box = fromEntity.getBoundingBox().stretch(rotationVec.multiply(range)).expand(1.0, 1.0, 1.0);

        HitResult entityHitResult = ProjectileUtil.raycast(
                fromEntity,
                cameraVec,
                vec3d3,
                box,
                e -> !e.isSpectator() && e.canHit() && e == toEntity,
                rangeSquared
        );

        if (entityHitResult == null) return false;

        double distance = cameraVec.squaredDistanceTo(entityHitResult.getPos());

        return distance <= rangeSquared && canSeePointFrom(cameraVec, entityHitResult.getPos())
                || distance <= wallsRangeSquared;
    }

    public static boolean facingBlock(Vec3d eyes, Vec3d vec3, BlockPos blockPos, Direction expectedSide, Double expectedMaxRange) {
        BlockHitResult searchedPos = mc.world.raycast(new RaycastContext(
                eyes,
                vec3,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                mc.player
        ));

        if (searchedPos.getType() != HitResult.Type.BLOCK || (expectedSide != null && searchedPos.getSide() != expectedSide)) {
            return false;
        }

        if (expectedMaxRange != null && searchedPos.getPos().squaredDistanceTo(eyes) > expectedMaxRange * expectedMaxRange) {
            return false;
        }

        return searchedPos.getBlockPos().equals(blockPos);

    }

    @FunctionalInterface
    public interface EntityFilter {
        boolean test(Entity entity);
    }
}
