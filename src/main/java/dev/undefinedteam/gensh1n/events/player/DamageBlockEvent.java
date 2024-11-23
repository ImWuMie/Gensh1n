package dev.undefinedteam.gensh1n.events.player;

import lombok.AllArgsConstructor;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@AllArgsConstructor
public class DamageBlockEvent {
    public BlockHitResult blockHitResult;
    public BlockPos blockPos;
    public Direction direction;
}
