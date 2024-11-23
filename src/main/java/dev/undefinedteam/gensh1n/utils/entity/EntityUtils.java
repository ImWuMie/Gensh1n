package dev.undefinedteam.gensh1n.utils.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

import static dev.undefinedteam.gensh1n.Client.mc;

public class EntityUtils {
    private static final BlockPos.Mutable testPos = new BlockPos.Mutable();

    public static boolean isAttackable(EntityType<?> type) {
        return type != EntityType.AREA_EFFECT_CLOUD && type != EntityType.ARROW && type != EntityType.FALLING_BLOCK && type != EntityType.FIREWORK_ROCKET && type != EntityType.ITEM && type != EntityType.LLAMA_SPIT && type != EntityType.SPECTRAL_ARROW && type != EntityType.ENDER_PEARL && type != EntityType.EXPERIENCE_BOTTLE && type != EntityType.POTION && type != EntityType.TRIDENT && type != EntityType.LIGHTNING_BOLT && type != EntityType.FISHING_BOBBER && type != EntityType.EXPERIENCE_ORB && type != EntityType.EGG;
    }

    public static boolean isRideable(EntityType<?> type) {
        return type == EntityType.MINECART || type == EntityType.BOAT || type == EntityType.CAMEL || type == EntityType.DONKEY || type == EntityType.HORSE || type == EntityType.LLAMA || type == EntityType.MULE || type == EntityType.PIG || type == EntityType.SKELETON_HORSE || type == EntityType.STRIDER || type == EntityType.ZOMBIE_HORSE;
    }

    public static float getTotalHealth(PlayerEntity target) {
        return target.getHealth() + target.getAbsorptionAmount();
    }

    public static int getPing(PlayerEntity player) {
        if (mc.getNetworkHandler() == null) return 0;

        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
        if (playerListEntry == null) return 0;
        return playerListEntry.getLatency();
    }

    public static GameMode getGameMode(PlayerEntity player) {
        if (player == null) return null;
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
        if (playerListEntry == null) return null;
        return playerListEntry.getGameMode();
    }

    public static boolean isAboveWater(Entity entity) {
        BlockPos.Mutable blockPos = entity.getBlockPos().mutableCopy();

        for (int i = 0; i < 64; i++) {
            BlockState state = mc.world.getBlockState(blockPos);

            if (state.blocksMovement()) break;

            Fluid fluid = state.getFluidState().getFluid();
            if (fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER) {
                return true;
            }

            blockPos.move(0, -1, 0);
        }

        return false;
    }

    public static boolean isInRenderDistance(Entity entity) {
        if (entity == null) return false;
        return isInRenderDistance(entity.getX(), entity.getZ());
    }

    public static boolean isInRenderDistance(BlockEntity entity) {
        if (entity == null) return false;
        return isInRenderDistance(entity.getPos().getX(), entity.getPos().getZ());
    }

    public static boolean isInRenderDistance(BlockPos pos) {
        if (pos == null) return false;
        return isInRenderDistance(pos.getX(), pos.getZ());
    }

    public static boolean isInRenderDistance(double posX, double posZ) {
        double x = Math.abs(mc.gameRenderer.getCamera().getPos().x - posX);
        double z = Math.abs(mc.gameRenderer.getCamera().getPos().z - posZ);
        double d = (mc.options.getViewDistance().getValue() + 1) * 16;

        return x < d && z < d;
    }

    public static String getName(Entity entity) {
        if (entity == null) return null;
        if (entity instanceof PlayerEntity) return entity.getName().getString();
        return entity.getType().getName().getString();
    }
}
