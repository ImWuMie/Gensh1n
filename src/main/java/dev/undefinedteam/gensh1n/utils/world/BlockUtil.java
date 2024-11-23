package dev.undefinedteam.gensh1n.utils.world;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;

import static dev.undefinedteam.gensh1n.Client.mc;

/**
 * @Author KuChaZi
 * @Date 2024/11/13 21:46
 * @ClassName: BlockUtil
 */
public class BlockUtil {
    public static boolean isValidBlock(final BlockPos blockPos) {
        BlockState blockState = mc.world.getBlockState(blockPos);
        Block block = blockState.getBlock();

        return
//            !(block instanceof LiquidBlock) &&
            !(block instanceof AirBlock) &&
            !(block instanceof ChestBlock) &&
            !(block instanceof FurnaceBlock) &&
            !(block instanceof LadderBlock) &&
            !(block instanceof TntBlock);
    }
}
