package dev.undefinedteam.gensh1n.system.modules.movement;

import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Category;
import dev.undefinedteam.gensh1n.system.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.HashMap;
import java.util.Map;

@StringEncryption
@ControlFlowObfuscation
public class AntiWeb extends Module {
    public AntiWeb() {
        super(Categories.Movement,"anti-web","Prevent webs slow you");
    }

    @EventHandler
    public void onTick(TickEvent.Pre e){
        Map<BlockPos, Block> searchBlock = searchBlocks(2);
        for (Map.Entry<BlockPos, Block> block : searchBlock.entrySet()) {
            if (mc.world.getBlockState(block.getKey()).getBlock() == Blocks.COBWEB) {
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, block.getKey(), Direction.DOWN));
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, block.getKey(), Direction.DOWN));
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, block.getKey(), Direction.DOWN));
                mc.world.setBlockState(block.getKey(),Blocks.AIR.getDefaultState());
            }
        }
    }
    public Map<BlockPos, Block> searchBlocks(final int radius) {
        final Map<BlockPos, Block> blocks = new HashMap<BlockPos, Block>();
        for (int x = radius; x > -radius; --x) {
            for (int y = radius; y > -radius; --y) {
                for (int z = radius; z > -radius; --z) {
                    final BlockPos blockPos = new BlockPos(mc.player.getBlockX() + x, mc.player.getBlockY() + y, mc.player.getBlockZ() + z);
                    final Block block = getBlock(blockPos);
                    blocks.put(blockPos, block);
                }
            }
        }
        return blocks;
    }
    public Block getBlock(final BlockPos blockPos) {
        return mc.world.getBlockState(blockPos).getBlock();
    }


}
