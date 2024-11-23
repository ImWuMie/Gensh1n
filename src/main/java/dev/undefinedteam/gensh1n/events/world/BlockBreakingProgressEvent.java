package dev.undefinedteam.gensh1n.events.world;

import net.minecraft.util.math.BlockPos;

public class BlockBreakingProgressEvent {
    public BlockPos pos;

    public BlockBreakingProgressEvent(BlockPos pos) {
        this.pos = pos;
    }
}
