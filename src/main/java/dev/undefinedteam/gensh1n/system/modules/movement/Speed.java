package dev.undefinedteam.gensh1n.system.modules.movement;

import dev.undefinedteam.gclient.Timer;
import dev.undefinedteam.gensh1n.events.player.PlayerTickEvent;
import dev.undefinedteam.gensh1n.events.player.PlayerTravelEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Category;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.combat.KillAura;
import dev.undefinedteam.gensh1n.system.modules.player.Blink;
import dev.undefinedteam.gensh1n.system.modules.player.Blink2;
import dev.undefinedteam.gensh1n.utils.entity.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.potion.Potions;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3d;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @Author KuChaZi
 * @Date 2024/10/27 10:31
 * @ClassName: Speed
 */
@StringEncryption
@ControlFlowObfuscation
public class Speed extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<SpeedMode> mode = choice(sgGeneral, "Mode", SpeedMode.Grim);
    private final Setting<Boolean> boost = bool(sgGeneral, "Boost", "IDK", false, () -> mode.get().equals(SpeedMode.Hypixel));
    private final Setting<Boolean> lagback = bool(sgGeneral, "LagBack", "lag check", true, () -> mode.get().equals(SpeedMode.Hypixel));
    private final Timer timer = new Timer();
    private boolean prevOnGround;
    private Vector3d prevPosition;

    private enum SpeedMode {
        Hypixel,
        Grim
    }

    public Speed() {
        super(Categories.Movement, "speed", "player speed");
    }

    @Override
    public String getInfoString() {
        return mode.get().toString();
    }

    @EventHandler
    public void modifyVelocity(PlayerTravelEvent e) {
        if (!e.isPre() && timer.getPassedTimeMs() > 1000 && isMoving() && mode.get().equals(SpeedMode.Grim)) {
            if (mc.player != null && (Blink.isFakePlayer(mc.player) || Blink2.isFakePlayer(mc.player))) {
                return;
            }
            int collisions = 0;
            for (Entity ent : mc.world.getEntities())
                if (ent != mc.player && (ent instanceof LivingEntity || ent instanceof BoatEntity) && mc.player.getBoundingBox().expand(1.0).intersects(ent.getBoundingBox()))
                    collisions++;

            double[] motion = forward(0.08 * collisions);
            mc.player.addVelocity(motion[0], 0.0, motion[1]);
        }
        if (e.isPre() && mode.get().equals(SpeedMode.Hypixel)) {
            if (isInLiquid()) return;
            if (mc.player.isOnGround()) {
                prevOnGround = true;
                if (isMoving()) {
                    mc.player.setVelocity(mc.player.getVelocity().x, 0.42, mc.player.getVelocity().z);
                    mc.player.jump();

                    double s = 0.2;
                    double s2 = 0.23;
                    PlayerUtils.strafe(Math.max(getBaseMoveSpeed() + s2, getHorizontalMotion() + s));
                }

            } else {
                mc.player.timeUntilRegen = (int) 1.0;

                if (!isMoving() && lagback.get()) {
                    mc.player.setVelocity(0.0, mc.player.getVelocity().y, 0.0);
                }

                if (prevOnGround) {
                    double mult = 1.0;
                    if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
                        mult += 0.04;
                    }
                    mc.player.setVelocity(mc.player.getVelocity().x * mult, mc.player.getVelocity().y, mc.player.getVelocity().z * mult);
                    prevOnGround = false;
                }

                if (!mc.player.isSprinting()) {
                    float f = getPlayerDirection() * ((float) Math.PI / 180);
                    mc.player.setVelocity(mc.player.getVelocity().x - MathHelper.sin(f) * 0.02, mc.player.getVelocity().y, mc.player.getVelocity().z + MathHelper.cos(f) * 0.02);
                }
            }
        }
    }

    private float getPlayerDirection() {
        PlayerEntity sb = mc.player;
        if (sb == null) {
            return 0.0f;
        }

        float direction = sb.getYaw();

        if (sb.forwardSpeed > 0.0f) {
            if (sb.sidewaysSpeed > 0.0f) {
                direction -= 45.0f;
            } else if (sb.sidewaysSpeed < 0.0f) {
                direction += 45.0f;
            }
        } else if (sb.forwardSpeed < 0.0f) {
            if (sb.sidewaysSpeed > 0.0f) {
                direction -= 135.0f;
            } else if (sb.sidewaysSpeed < 0.0f) {
                direction += 135.0f;
            } else {
                direction -= 180.0f;
            }
        } else if (sb.sidewaysSpeed > 0.0f) {
            direction -= 90.0f;
        } else if (sb.sidewaysSpeed < 0.0f) {
            direction += 90.0f;
        }

        return direction;
    }

    private double getHorizontalMotion() {
        return Math.hypot(mc.player.getVelocity().x, mc.player.getVelocity().z);
    }

    private double getBaseMoveSpeed() {
        double baseSpeed = 0.2875;

        if (mc.player != null) {
            StatusEffectInstance se = mc.player.getStatusEffect(StatusEffects.SPEED);
            if (se != null) {
                int amplifier = se.getAmplifier();
                baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
            }
        }

        return baseSpeed;
    }

    @EventHandler
    public void onUpdate(PlayerTickEvent event) {
        if (boost.get()) {
            if (this.mc.player.getVelocity().y < 0.1 && this.mc.player.getVelocity().y > 0.01) {
                this.mc.player.getVelocity().x *= 1.005;
                this.mc.player.getVelocity().z *= 1.005;
            }
            if (this.mc.player.getVelocity().y < 0.005 && this.mc.player.getVelocity().y > 0.0) {
                this.mc.player.getVelocity().x *= 1.005;
                this.mc.player.getVelocity().z *= 1.005;
            }
            if (this.mc.player.getVelocity().y < 0.001 && this.mc.player.getVelocity().y > -0.03) {
                if (this.mc.player.hasStatusEffect(StatusEffects.SPEED)) {
                    this.mc.player.getVelocity().x *= 1.005;
                    this.mc.player.getVelocity().z *= 1.005;
                } else {
                    this.mc.player.getVelocity().x *= 1.002;
                    this.mc.player.getVelocity().z *= 1.002;
                }
            }
        }
    }

    public void onMove() {

    }


    private double strafe(double d) {
        if (mc.player == null || !PlayerUtils.isMoving()) {
            return mc.player != null ? mc.player.getYaw() : 0;
        }
        double yaw = getDirection();
        mc.player.setVelocity(-MathHelper.sin((float) yaw) * d, mc.player.getVelocity().y, MathHelper.cos((float) yaw) * d);

        return yaw;
    }

    private double getDirection() {
        return Math.toRadians(mc.player.getYaw() + 90);
    }


    private boolean isInLiquid() {
        if (mc.player == null) return false;

        BlockPos minPos = new BlockPos((int) mc.player.getBoundingBox().minX, (int) mc.player.getBoundingBox().minY, (int) mc.player.getBoundingBox().minZ);
        BlockPos maxPos = new BlockPos((int) mc.player.getBoundingBox().maxX, (int) mc.player.getBoundingBox().maxY, (int) mc.player.getBoundingBox().maxZ);

        for (int x = minPos.getX(); x < maxPos.getX(); x++) {
            for (int y = minPos.getY(); y < maxPos.getY(); y++) {
                for (int z = minPos.getZ(); z < maxPos.getZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = mc.world.getBlockState(pos);
                    Block block = state.getBlock();

                    if (block == Blocks.WATER || block == Blocks.LAVA) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isMoving() {
        return mc.player.input.movementForward != 0.0 || mc.player.input.movementSideways != 0.0;
    }

    public double[] forward(final double d) {
        float f = mc.player.input.movementForward;
        float f2 = mc.player.input.movementSideways;
        float f3 = mc.player.getYaw();
        if (f != 0.0f) {
            if (f2 > 0.0f) {
                f3 += ((f > 0.0f) ? -45 : 45);
            } else if (f2 < 0.0f) {
                f3 += ((f > 0.0f) ? 45 : -45);
            }
            f2 = 0.0f;
            if (f > 0.0f) {
                f = 1.0f;
            } else if (f < 0.0f) {
                f = -1.0f;
            }
        }
        final double d2 = Math.sin(Math.toRadians(f3 + 90.0f));
        final double d3 = Math.cos(Math.toRadians(f3 + 90.0f));
        final double d4 = f * d * d3 + f2 * d * d2;
        final double d5 = f * d * d2 - f2 * d * d3;
        return new double[]{d4, d5};
    }
}
