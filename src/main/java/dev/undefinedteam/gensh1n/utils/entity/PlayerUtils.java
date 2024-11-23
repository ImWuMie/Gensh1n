package dev.undefinedteam.gensh1n.utils.entity;

import dev.undefinedteam.gensh1n.rotate.Rotation;
import dev.undefinedteam.gensh1n.system.friend.Friends;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.system.modules.render.ESP;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathConstants;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import static dev.undefinedteam.gensh1n.Client.mc;

public class PlayerUtils {
    private static final Color color = new Color();
    public static float getMoveYaw(float yaw) {
        Rotation from = new Rotation((float) mc.player.lastX, (float) mc.player.lastZ),
            to = new Rotation((float) mc.player.getX(), (float) mc.player.getZ()),
            diff = new Rotation(to.yaw - from.yaw, to.pitch - from.pitch);

        double x = diff.yaw, z = diff.pitch;
        if (x != 0 && z != 0) {
            yaw = (float) Math.toDegrees((Math.atan2(-x, z) + MathConstants.PI) % MathConstants.PI);
        }
        return yaw;
    }

    public static void stop() {
        if (mc.player != null) {
            mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
        }
    }

    public static boolean inFov(Entity entity, double fov) {
        if (fov >= 360) return true;
        float[] angle = PlayerUtils.calculateAngle(entity.getBoundingBox().getCenter());
        double xDist = MathHelper.angleBetween(angle[0], mc.player.getYaw());
        double yDist = MathHelper.angleBetween(angle[1], mc.player.getPitch());
        double angleDistance = Math.hypot(xDist, yDist);
        return angleDistance <= fov;
    }

    public static float getClosestDistanceToEntity(Entity entityIn) {
        Vec3d eyes = mc.player.getEyePos();
        Box boundingBox = entityIn.getBoundingBox();
        Vec3d closestPoint = getClosestPoint(eyes, boundingBox);

        double xDist = Math.abs(closestPoint.x - eyes.x);
        double yDist = Math.abs(closestPoint.y - eyes.y);
        double zDist = Math.abs(closestPoint.z - eyes.z);

        return (float) Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
    }

    public static Vec3d getClosestPoint(final Vec3d start, final Box boundingBox) {
        final double closestX = start.x >= boundingBox.maxX ? boundingBox.maxX : start.x <= boundingBox.minX ? boundingBox.minX : boundingBox.minX + (start.x - boundingBox.minX);
        final double closestY = start.y >= boundingBox.maxY ? boundingBox.maxY : start.y <= boundingBox.minY ? boundingBox.minY : boundingBox.minY + (start.y - boundingBox.minY);
        final double closestZ = start.z >= boundingBox.maxZ ? boundingBox.maxZ : start.z <= boundingBox.minZ ? boundingBox.minZ : boundingBox.minZ + (start.z - boundingBox.minZ);

        return new Vec3d(closestX, closestY, closestZ);
    }

    public static float[] calculateAngle(Vec3d target) {
        Vec3d eyesPos = new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());

        double dX = target.x - eyesPos.x;
        double dY = (target.y - eyesPos.y) * -1.0D;
        double dZ = target.z - eyesPos.z;

        double dist = Math.sqrt(dX * dX + dZ * dZ);

        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dZ, dX)) - 90.0D), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dY, dist)))};
    }

    public static Color getPlayerColor(PlayerEntity entity, Color defaultColor) {
        if (Friends.get().isFriend(entity)) {
            return color.set(Modules.get().get(ESP.class).friendColor.get()).a(defaultColor.a);
        }

        return defaultColor;
    }

    public static Color getPlayerColor(PlayerEntity entity, Color defaultColor,boolean isFriend) {
        if (isFriend) {
            return color.set(Modules.get().get(ESP.class).friendColor.get()).a(defaultColor.a);
        }

        return defaultColor;
    }

    public static GameMode getGameMode() {
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        if (playerListEntry == null) return GameMode.SPECTATOR;
        return playerListEntry.getGameMode();
    }

    public static double distanceToPlayer(double x, double y, double z) {
        return Math.sqrt(squaredDistanceToPlayer(x, y, z));
    }

    public static double distanceToPlayer(Entity entity) {
        return distanceToPlayer(entity.getX(), entity.getY(), entity.getZ());
    }

    public static double squaredDistanceToPlayer(double x, double y, double z) {
        Vec3d playerPos = mc.player.getPos();
        return squaredDistance(playerPos.x, playerPos.y, playerPos.z, x, y, z);
    }

    public static double squaredDistanceToPlayer(Entity entity) {
        return squaredDistanceToPlayer(entity.getX(), entity.getY() + entity.getEyeHeight(entity.getPose()), entity.getZ());
    }

    public static double distanceToCamera(double x, double y, double z) {
        return Math.sqrt(squaredDistanceToCamera(x, y, z));
    }

    public static double distanceToCamera(Entity entity) {
        return distanceToCamera(entity.getX(), entity.getY() + entity.getEyeHeight(entity.getPose()), entity.getZ());
    }

    public static double squaredDistanceToCamera(double x, double y, double z) {
        Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();
        return squaredDistance(cameraPos.x, cameraPos.y, cameraPos.z, x, y, z);
    }

    public static double squaredDistanceToCamera(Entity entity) {
        return squaredDistanceToCamera(entity.getX(), entity.getY() + entity.getEyeHeight(entity.getPose()), entity.getZ());
    }

    public static double squaredDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double f = x1 - x2;
        double g = y1 - y2;
        double h = z1 - z2;
        return org.joml.Math.fma(f, f, org.joml.Math.fma(g, g, h * h));
    }

    public static boolean isMoving() {
        return mc.player.sidewaysSpeed != 0 || mc.player.forwardSpeed != 0;
    }

    public static double getDirection() {
        PlayerEntity player = mc.player;
        if (player == null) return 0;
        return Math.toRadians(player.getYaw() + 90); // 转换为弧度，Minecraft 中的方向角度
    }

    public static boolean isMovings() {
        PlayerEntity player = mc.player;
        return player != null && (player.horizontalSpeed > 0.0 || player.forwardSpeed != 0.0);
    }

    public static void strafe1(double d) {
        PlayerEntity player = mc.player;
        if (player == null || !isMovings()) {
            return;
        }

        double yaw = getDirection();

        double motionX = -Math.sin(yaw) * d;
        double motionZ = Math.cos(yaw) * d;

        player.setVelocity(motionX, player.getVelocity().y, motionZ);
    }

    public static void strafe(double speed) {
        if (!isMoving()) {
            return;
        }
        double yaw = Math.toRadians(mc.player.getYaw());

        mc.player.setVelocity(
            -Math.sin(yaw) * speed,
            mc.player.getVelocity().y,
            Math.cos(yaw) * speed
        );
    }

}
