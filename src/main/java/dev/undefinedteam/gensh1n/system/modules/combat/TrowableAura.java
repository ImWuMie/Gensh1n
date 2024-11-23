package dev.undefinedteam.gensh1n.system.modules.combat;

import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.world.WorldChangeEvent;
import dev.undefinedteam.gensh1n.rotate.Rotation;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.system.modules.misc.Teams;
import dev.undefinedteam.gensh1n.system.modules.player.Blink;
import dev.undefinedteam.gensh1n.system.modules.world.Scaffold;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2f;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @Author KuChaZi
 * @Date 2024/11/10 00:51
 * @ClassName: TrowableAura
 */
@StringEncryption
public class TrowableAura extends Module {
    private final SettingGroup sgDefault = settings.getDefaultGroup();
    private final Setting<Double> range = doubleN(sgDefault,"Range", 8.0, 4.0, 8.0);
    private final Setting<Boolean> fishRod = bool(sgDefault,"FishRod", true);
    public static final List<PlayerEntity> targets = new ArrayList<>();
    public static PlayerEntity target;
    public static int tick = 0;
    public static boolean isThrowOut = false;

    public TrowableAura() {
        super(Categories.Combat,"trowable-aura", "test");
    }

//    @Override
//    public void onActivate() {
//
//    }

//    @Override
//    public void onDeactivate() {
//
//    }

    @EventHandler
    public void onWorld(WorldChangeEvent event) {
        target = null;
    }

    @EventHandler
    public void onMotionEvent(TickEvent.Pre event) {
        ClientPlayerEntity player = mc.player;

        if (player == null || mc.world == null) {
            return;
        }

        targets.sort(Comparator.comparingDouble(player::distanceTo));
        if (!targets.isEmpty()) {
            target = targets.get(0);
        } else {
            target = null;
        }

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof FishingBobberEntity fishHook && isThrowOut) {
                if ((fishHook.getOwner() != null && fishHook.getOwner() == target) || entity.isOnGround()) {
                    isThrowOut = false;
                    player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, player.getBlockPos(), mc.player.getHorizontalFacing()));
                }
            }
        }
        if (isThrowOut) {
            if (Modules.get().get(Scaffold.class).isActive() || Modules.get().get(Blink.class).isActive()) {
                isThrowOut = false;
                player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, player.getBlockPos(), mc.player.getHorizontalFacing()));
            }
        }

        if (isThrowOut || findBall() == -1 && (findFishRod() == -1 || !fishRod.get())) {
            return;
        }

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity && player.distanceTo(entity) <= range.get() && player != entity && !Teams.isInTeam((LivingEntity) entity)) {
                targets.add((PlayerEntity) entity);
            }
        }

        long delay = 3;

        if (target != null && player.distanceTo(target) <= range.get() && getRotationDifference(target) <= 180 && player.canSee(target)) {
            Rotation rotation = getThrowRotation(target, range.get());
//            RotationComponent.setRotations(rotation, rotspeed.getValue(), MovementFix.NORMAL);
            rotate.rotation(rotation).keep(1).set();

            if (++tick > delay) {
                if (findBall() != -1) {
                    player.swingHand(Hand.MAIN_HAND);
                    ItemStack ball = mc.player.getMainHandStack();
                    if (ball != null && (ball.getItem() == Items.SNOWBALL || ball.getItem() == Items.EGG)) {
//                        mc.player.useItemOnEntity(player, target, Hand.MAIN_HAND);
                        mc.player.isUsingItem();
                    }
                } else if (findFishRod() != -1 && !isThrowOut && fishRod.get()) {
                    player.swingHand(Hand.MAIN_HAND);
                    ItemStack fishRodItem = mc.player.getMainHandStack();
                    if (fishRodItem != null && fishRodItem.getItem() == Items.FISHING_ROD) {
//                        mc.player.useItemOnEntity(player, target, Hand.MAIN_HAND);
                        mc.player.isUsingItem();
                        isThrowOut = true;
                    }
                }
                target = null;
                targets.clear();
                tick = 0;
            }
        } else {
            tick = 0;
        }
    }

    public double getRotationDifference(Entity entity) {
        Vector2f rotation = toRotation(getCenter(entity.getBoundingBox()), true);
        return getRotationDifference(rotation, new Vector2f(mc.player.renderYaw, mc.player.renderPitch));
    }

    public static Vec3d getCenter(Box box) {
        return new Vec3d(
            box.minX + (box.maxX - box.minX) * 0.5,
            box.minY + (box.maxY - box.minY) * 0.5,
            box.minZ + (box.maxZ - box.minZ) * 0.5
        );
    }

    public Vector2f toRotation(Vec3d targetVec, boolean predict) {
        if (mc.player == null) return null;

        Vec3d eyesPos = new Vec3d(
            mc.player.getX(),
            mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()),
            mc.player.getZ()
        );

        if (predict) {
            eyesPos = eyesPos.add(mc.player.getVelocity());
        }

        double diffX = targetVec.x - eyesPos.x;
        double diffY = targetVec.y - eyesPos.y;
        double diffZ = targetVec.z - eyesPos.z;

        float yaw = MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f);
        float pitch = MathHelper.wrapDegrees((float) -Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))));

        return new Vector2f(yaw, pitch);
    }

    public double getRotationDifference(Vector2f a, Vector2f b2) {
        return Math.hypot(getAngleDifference(a.x, b2.x), (a.y - b2.y));
    }

    public float getAngleDifference(float a, float b2) {
        return ((a - b2) % 360.0f + 540.0f) % 360.0f - 180.0f;
    }

    public Rotation getThrowRotation(Entity entity, double maxRange) {
        if (entity == null) {
            return null;
        }
        double deltaX = entity.getX() - mc.player.getX() - mc.player.getVelocity().x;
        double deltaY = entity.getEyeY() - mc.player.getEyeY();
        double deltaZ = entity.getZ() - mc.player.getZ() - mc.player.getVelocity().z;
        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        float yaw = (float) (Math.atan2(deltaZ, deltaX) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) -(Math.atan2(deltaY, horizontalDistance) * 180.0 / Math.PI);
        float finalYaw = mc.player.getYaw() + MathHelper.wrapDegrees(yaw - mc.player.getYaw());
        float finalPitch = mc.player.getPitch() + MathHelper.wrapDegrees(pitch - mc.player.getPitch());
        return new Rotation(finalYaw, finalPitch);
    }


    public int findFishRod() {
        for (int i = 0; i < mc.player.getInventory().size(); ++i) {
            ItemStack itemStack = mc.player.getInventory().getStack(i);
            if (itemStack != null && itemStack.getItem() == Items.FISHING_ROD) {
                return i;
            }
        }
        return -1;
    }

    public int findBall() {
        for (int i = 0; i < mc.player.getInventory().size(); ++i) {
            ItemStack itemStack = mc.player.getInventory().getStack(i);
            if (itemStack != null && (itemStack.getItem() == Items.SNOWBALL || itemStack.getItem() == Items.EGG)) {
                return i;
            }
        }
        return -1;
    }
}
