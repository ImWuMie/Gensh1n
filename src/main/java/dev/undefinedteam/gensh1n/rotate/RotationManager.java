package dev.undefinedteam.gensh1n.rotate;

import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.network.PacketEvent;
import dev.undefinedteam.gensh1n.events.player.RotationApplyEvent;
import dev.undefinedteam.gensh1n.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import org.joml.Vector2f;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static dev.undefinedteam.gensh1n.Client.mc;
import static java.lang.Math.*;

@StringEncryption
@NativeObfuscation
@ControlFlowObfuscation
public class RotationManager {
    public URotation targetRotation;

    public Vector2f lastRotations;

    public Rotation lastRotation = new Rotation(0.0F, 0.0F);
    public Rotation serverRotation = new Rotation(0.0F, 0.0F);

    public Rotation getClientRotation() {
        return Utils.canUpdate() ? new Rotation(mc.player.getYaw(), mc.player.getPitch()) : new Rotation(0, 0);
    }

    public Rotation smooth(Rotation lastRotation, Rotation targetRotation, final double speed) {
        float yaw = targetRotation.yaw;
        float pitch = targetRotation.pitch;
        final float lastYaw = lastRotation.yaw;
        final float lastPitch = lastRotation.pitch;

        return new Rotation((float) MathHelper.lerp(speed / 180, lastYaw, yaw), (float) MathHelper.lerp(speed / 180, lastPitch, pitch));
    }

    public double getRotationDifference(final Rotation rotation) {
        return lastRotations == null ? 0D : getRotationDifference(rotation, lastRotations);
    }
    public float getAngleDifference(final float a, final float b) {
        return ((((a - b) % 360F) + 540F) % 360F) - 180F;
    }

    public double getRotationDifference(final Rotation a, final Vector2f b) {
        return Math.hypot(getAngleDifference(a.yaw, b.x), a.pitch - b.y);
    }

    public Rotation getRotation(Vec3d vec, Vec3d eyes) {
        var diffX = vec.x - eyes.x;
        var diffY = vec.y - eyes.y;
        var diffZ = vec.z - eyes.z;

        return new Rotation(
            MathHelper.wrapDegrees((float) Math.toDegrees(atan2(diffZ, diffX)) - 90.0f),
            MathHelper.wrapDegrees(((float) -Math.toDegrees(atan2(diffY, sqrt(diffX * diffX + diffZ * diffZ)))))
        );
    }

    public Rotation getRotationsEntity(Entity e) {
        return getRotation(e.getCameraPosVec(1), mc.player.getCameraPosVec(1));
    }

    @NativeObfuscation.Inline
    public static final List<Rotation> rotationRanges = new ArrayList<>() {
        {
            float step = 1.0f;
            for (float yawStep = -180; yawStep <= 180; yawStep += step) {
                for (float pitchStep = -90; pitchStep <= 90; pitchStep += step) {
                    add(new Rotation(yawStep, pitchStep));
                }
            }
        }
    };


    @NativeObfuscation.Inline
    public boolean apply(URotation rotation) {
        if (rotation == null) {
            return false;
        }

        var rot = Client.EVENT_BUS.post(new RotationApplyEvent(rotation)).rotation;

        if (this.targetRotation != null) {
            if (rot.priority > targetRotation.priority) {
                targetRotation = rot.copy();
                return true;
            }
            if (rot.priority < targetRotation.priority) {
                if (targetRotation.passTicks <= 0) {
                    targetRotation = rot.copy();
                    return true;
                } else return false;
            }
        }

        targetRotation = rot.copy();
        return true;
    }

    @NativeObfuscation.Inline
    public boolean inRaycast(Rotation rotation, Entity entity, double reachDistance) {
        var over = getRotationOver(rotation, reachDistance);
        return over != null && over.getType().equals(HitResult.Type.ENTITY) && ((EntityHitResult) over).getEntity() == entity;
    }

    @NativeObfuscation.Inline
    public boolean inRaycast(Entity entity, double reachDistance) {
        return inRaycast(targetRotation.rot, entity, reachDistance);
    }

    public boolean inRaycast(Entity entity) {
        var over = getRotationOver();
        return over != null && over.getType().equals(HitResult.Type.ENTITY) && ((EntityHitResult) over).getEntity() == entity;
    }

    public HitResult getRotationOver() {
        double reachDistance = targetRotation != null ? targetRotation.reachDistance : 3.0;
        return getRotationOver(targetRotation.rot, reachDistance);
    }

    public Rotation getBestRotation(Entity target, double reach, float step) {
        List<Rotation> allRots = new ArrayList<>();
        Rotation r = getRotationsEntity(target);
        double mix = Math.min(0.15, step / reach);
        reach -= mix;
        Rotation rRotation = targetRotation == null ? getClientRotation() : targetRotation.rot;

        if (inRaycast(rRotation, target, 2.5f)) {
            return rRotation;
        }

        float yawOffset = inRaycast(rRotation, target, reach) ? 180 - rRotation.yaw : 50;
        for (float yawStep = r.yaw - yawOffset; yawStep < r.yaw + yawOffset; yawStep += step) {
            if (yawStep < -180 || yawStep > 180) continue;
            for (float pitchStep = r.pitch - 50; pitchStep < r.pitch + 50; pitchStep += step) {
                if (pitchStep < -90 || pitchStep > 90) continue;
                allRots.add(new Rotation(yawStep, pitchStep));
            }
        }

        Rotation bestRot = null;
        double lastReach = Double.MAX_VALUE;
        for (Rotation rotation : allRots) {
            if (rotation == null) continue;

            HitResult position = getRotationOver(rotation, reach);
            if (position != null && position.getType().equals(HitResult.Type.ENTITY)) {
                var entityHitRay = (EntityHitResult) position;
                var eyePos = mc.player.getCameraPosVec(1);
                double re = position.getPos().distanceTo(eyePos);
                if (entityHitRay.getEntity() != null && re < lastReach) {
                    bestRot = rotation;
                    lastReach = re;
                }
            }
        }
        return Objects.requireNonNullElse(bestRot, r);
    }

    public HitResult getRotationOver(Rotation rotation, double reachDistance) {
        if (rotation == null) return null;
        return raycast(rotation, mc.player, reachDistance, reachDistance);
    }

    private HitResult raycast(Rotation rotation, Entity camera, double blockInteractionRange, double entityInteractionRange) {
        double range = Math.max(blockInteractionRange, entityInteractionRange);
        double squaredRange = MathHelper.square(range);
        Vec3d cameraPos = camera.getCameraPosVec(1);
        HitResult hitResult = overRaycast(rotation, camera, range, false);
        double squaredDistanceTo = hitResult.getPos().squaredDistanceTo(cameraPos);
        if (hitResult.getType() != net.minecraft.util.hit.HitResult.Type.MISS) {
            squaredRange = squaredDistanceTo;
            range = Math.sqrt(squaredRange);
        }

        Vec3d rotationVec = this.getRotationVec(rotation);
        Vec3d endVec = cameraPos.add(rotationVec.x * range, rotationVec.y * range, rotationVec.z * range);
        Box box = camera.getBoundingBox().stretch(rotationVec.multiply(range)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(camera, cameraPos, endVec, box, (entity) -> !entity.isSpectator() && entity.canHit(), squaredRange);
        return entityHitResult != null && entityHitResult.getPos().squaredDistanceTo(cameraPos) < squaredDistanceTo ? ensureTargetInRange(entityHitResult, cameraPos, entityInteractionRange) : ensureTargetInRange(hitResult, cameraPos, blockInteractionRange);
    }

    private HitResult overRaycast(Rotation rotation, Entity camera, double range, boolean includeFluids) {
        Vec3d cameraPos = camera.getCameraPosVec(1);
        Vec3d rotVec = this.getRotationVec(rotation);
        Vec3d vec3d3 = cameraPos.add(rotVec.x * range, rotVec.y * range, rotVec.z * range);
        return mc.world.raycast(
            new RaycastContext(
                cameraPos,
                vec3d3,
                RaycastContext.ShapeType.OUTLINE,
                includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE,
                camera
            ));
    }

    private HitResult ensureTargetInRange(HitResult hitResult, Vec3d cameraPos, double interactionRange) {
        Vec3d pos = hitResult.getPos();
        if (!pos.isInRange(cameraPos, interactionRange)) {
            Vec3d resultPos = hitResult.getPos();
            Direction direction = Direction.getFacing(resultPos.x - cameraPos.x, resultPos.y - cameraPos.y, resultPos.z - cameraPos.z);
            return BlockHitResult.createMissed(resultPos, direction, BlockPos.ofFloored(resultPos));
        } else {
            return hitResult;
        }
    }

    public Vec3d getRotationVec(Rotation rot) {
        return getRotationVec(rot.yaw, rot.pitch);
    }

    public Vec3d getRotationVec(URotation rot) {
        return getRotationVec(rot.yaw(), rot.pitch());
    }

    public Vec3d getRotationVec(float yaw, float pitch) {
        float f = pitch * 0.017453292F;
        float g = -yaw * 0.017453292F;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }

    public boolean shouldOverride() {
        return targetRotation != null && targetRotation.passTicks >= 0;
    }

    public boolean isApplyToPlayer() {
        return shouldOverride() && targetRotation.applyToPlayer;
    }

    public void setHeadYaw(Args args) {
        if (!shouldOverride()) {
            return;
        }

        args.set(1, lastRotation.yaw);
        args.set(2, targetRotation.yaw());
    }

    public void setBodyYaw(Args args) {
        if (!shouldOverride()) {
            return;
        }

        args.set(1, lastRotation.yaw);
        args.set(2, targetRotation.yaw());
    }

    public void setPitch(Args args) {
        if (!shouldOverride()) {
            return;
        }

        args.set(1, lastRotation.pitch);
        args.set(2, targetRotation.pitch());
    }

    @EventHandler
    private void onTick(TickEvent.Pre e) {
        if (!Utils.canUpdate() && shouldOverride()) targetRotation.passTicks = -1;

        if (this.targetRotation != null) {
            lastRotation.setYaw(targetRotation.yaw());
            lastRotation.setPitch(targetRotation.pitch());
        }
    }

    @EventHandler(priority = -999)
    private void onPacket(PacketEvent e) {
        if (e.packet instanceof PlayerMoveC2SPacket packet) {
            serverRotation.setYaw(packet.yaw);
            serverRotation.setPitch(packet.pitch);
        }
    }
}
