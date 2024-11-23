package dev.undefinedteam.gensh1n.utils.world;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BreakUtils {
    public static boolean simpleBreakBlock(BlockPos pos) {
        return breakBlock(pos, Direction.UP);
    }

    public static boolean breakBlock(BlockPos blockPos, Direction direction) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        ClientWorld world = client.world;
        ClientPlayerInteractionManager interactionManager = client.interactionManager;
        if (player == null || world == null || interactionManager == null || blockPos == null) {
            return false;
        }
        PlayerInventory playerInventory = player.getInventory();

        // 预检查
        if (world.getBlockState(blockPos).isReplaceable()) return true;
        if (world.getBlockState(blockPos).getBlock().getHardness() < 0) return false;

        // InventoryManagerUtils.autoSwitch();

        // interactionManager.attackBlock(blockPos, direction);

        /*client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
            PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos, direction));
        client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
            PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction));*/

        // 更新方块正在破坏进程
        if (interactionManager.updateBlockBreakingProgress(blockPos, direction)) {
            client.particleManager.addBlockBreakingParticles(blockPos, direction);
        }
        // 检查是否已经成功
        return world.getBlockState(blockPos).isReplaceable();
    }
}
