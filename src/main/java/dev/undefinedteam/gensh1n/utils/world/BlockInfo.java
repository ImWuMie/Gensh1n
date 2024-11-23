package dev.undefinedteam.gensh1n.utils.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;
import java.util.List;

import static dev.undefinedteam.gensh1n.Client.mc;

public class BlockInfo {
    public static String getBlockName(Block block) {
        return block.getName().getString();
    }

    public static Identifier getIdentifier(Block block) {
        return Registries.BLOCK.getId(block);
    }

    public static String getId(Block block) {
        return getIdentifier(block).toString();
    }

    public static VoxelShape getShape(BlockPos block) {
        return mc.world.getBlockState(block).getOutlineShape(mc.world, block);
    }

    public static Box getBox(BlockPos block) {
        return getShape(block).getBoundingBox();
    }

    public static BlockState getBlockState(BlockPos block) {
        return mc.world.getBlockState(block);
    }

    public static Block getBlock(BlockPos block) {
        return mc.world.getBlockState(block).getBlock();
    }

    public static float getBlastResistance(BlockPos block) {
        return mc.world.getBlockState(block).getBlock().getBlastResistance();
    }

    public static boolean isSolid(BlockPos block) {
        return mc.world.getBlockState(block).isSolid();
    }

    public static boolean isBurnable(BlockPos block) {
        return mc.world.getBlockState(block).isBurnable();
    }

    public static boolean isLiquid(BlockPos block) {
        return mc.world.getBlockState(block).isLiquid();
    }

    public static float getHardness(BlockPos block) {
        return mc.world.getBlockState(block).getHardness(mc.world, block);
    }

    public static float getHardness(Block block) {
        return block.getHardness();
    }

    public static boolean isBlastResist(BlockPos block) {
        return getBlastResistance(block) >= 600;
    }

    public static boolean isBlastResist(Block block) {
        return block.getBlastResistance() >= 600;
    }

    public static boolean isBreakable(BlockPos pos) {
        return getHardness(pos) > 0;
    }

    public static boolean isBreakable(Block block) {
        return getHardness(block) > 0;
    }

    public static boolean isCombatBlock(BlockPos block) {
        return isBlastResist(block) && isBreakable(block);
    }

    public static boolean isCombatBlock(Block block) {
        return isBlastResist(block) && isBreakable(block);
    }

    public static boolean isFullCube(BlockPos block) {
        return mc.world.getBlockState(block).isFullCube(mc.world, block);
    }

    public static Block getBoxTouchedBlock(Box box) {
        Block block = null;

        for (int x = (int) Math.floor(box.minX); x < Math.ceil(box.maxX); x++) {
            for (int y = (int) Math.floor(box.minY); y < Math.ceil(box.maxY); y++) {
                for (int z = (int) Math.floor(box.minZ); z < Math.ceil(box.maxZ); z++) {
                    block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                }
            }
        }

        return block;
    }

    public static boolean doesBoxTouchBlock(Box box, Block block) {
        for (int x = (int) Math.floor(box.minX); x < Math.ceil(box.maxX); x++) {
            for (int y = (int) Math.floor(box.minY); y < Math.ceil(box.maxY); y++) {
                for (int z = (int) Math.floor(box.minZ); z < Math.ceil(box.maxZ); z++) {
                    if (mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() == block) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static Vec3d closestVec3d(BlockPos blockPos) {
        if (blockPos == null) return new Vec3d(0.0, 0.0, 0.0);
        double x = MathHelper.clamp((mc.player.getX() - blockPos.getX()), 0.0, 1.0);
        double y = MathHelper.clamp((mc.player.getY() - blockPos.getY()), 0.0, 0.6);
        double z = MathHelper.clamp((mc.player.getZ() - blockPos.getZ()), 0.0, 1.0);
        return new Vec3d(blockPos.getX() + x, blockPos.getY() + y, blockPos.getZ() + z);
    }

    private static Vec3d closestVec3d(Box box) {
        if (box == null) return new Vec3d(0.0, 0.0, 0.0);
        Vec3d eyePos = new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());

        double x = MathHelper.clamp(eyePos.getX(), box.minX, box.maxX);
        double y = MathHelper.clamp(eyePos.getY(), box.minY, box.maxY);
        double z = MathHelper.clamp(eyePos.getZ(), box.minZ, box.maxZ);

        return new Vec3d(x, y, z);
    }

    public static Vec3d closestVec3d2(BlockPos pos) {
        return closestVec3d(box(pos));
    }

    public static List<BlockPos> getSphere(BlockPos centerPos, int radius, int height) {
        final ArrayList<BlockPos> blocks = new ArrayList<>();

        for (int i = centerPos.getX() - radius; i < centerPos.getX() + radius; i++) {
            for (int j = centerPos.getY() - height; j < centerPos.getY() + height; j++) {
                for (int k = centerPos.getZ() - radius; k < centerPos.getZ() + radius; k++) {
                    BlockPos pos = new BlockPos(i, j, k);
                    if (distanceBetween(centerPos, pos) <= radius && !blocks.contains(pos)) {
                        blocks.add(pos);
                    }
                }
            }
        }

        return blocks;
    }

    public static List<BlockPos> getSphere(BlockPos centerPos, double radius, double height) {
        ArrayList<BlockPos> blocks = new ArrayList<>();

        for (int i = centerPos.getX() - (int) radius; i < centerPos.getX() + radius; i++) {
            for (int j = centerPos.getY() - (int) height; j < centerPos.getY() + height; j++) {
                for (int k = centerPos.getZ() - (int) radius; k < centerPos.getZ() + radius; k++) {
                    BlockPos pos = new BlockPos(i, j, k);

                    if (distanceBetween(centerPos, pos) <= radius && !blocks.contains(pos)) blocks.add(pos);
                }
            }
        }

        return blocks;
    }

    public static double distanceBetween(BlockPos blockPos1, BlockPos blockPos2) {
        double d = blockPos1.getX() - blockPos2.getX();
        double e = blockPos1.getY() - blockPos2.getY();
        double f = blockPos1.getZ() - blockPos2.getZ();
        return MathHelper.sqrt((float) (d * d + e * e + f * f));
    }

    public static Box box(BlockPos blockPos) {
        return new Box(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1);
    }

    public static BlockPos roundBlockPos(Vec3d vec) {
        return BlockPos.ofFloored(vec.x, Math.round(vec.y), vec.z);
    }

    public static boolean canBreak(BlockPos blockPos, BlockState state) {
        if (!mc.player.isCreative() && state.getHardness(mc.world, blockPos) < 0) return false;
        return state.getOutlineShape(mc.world, blockPos) != VoxelShapes.empty();
    }

    public static boolean canBreak(BlockPos blockPos) {
        return canBreak(blockPos, mc.world.getBlockState(blockPos));
    }

    public static boolean canInstaBreak(BlockPos blockPos, float breakSpeed) {
        return mc.player.isCreative() || calcBlockBreakingDelta2(blockPos, breakSpeed) >= 1;
    }

    public static boolean canInstaBreak(BlockPos blockPos) {
        BlockState state = mc.world.getBlockState(blockPos);
        return canInstaBreak(blockPos, mc.player.getBlockBreakingSpeed(state));
    }

    public static float calcBlockBreakingDelta2(BlockPos blockPos, float breakSpeed) {
        BlockState state = mc.world.getBlockState(blockPos);
        float f = state.getHardness(mc.world, blockPos);
        if (f == -1.0F) {
            return 0.0F;
        } else {
            int i = mc.player.canHarvest(state) ? 30 : 100;
            return breakSpeed / f / (float) i;
        }
    }

    // Finds the best block direction to get when interacting with the block.
    public static Direction getDirection(BlockPos pos) {
        Vec3d eyesPos = new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());
        if ((double) pos.getY() > eyesPos.y) {
            if (mc.world.getBlockState(pos.add(0, -1, 0)).isReplaceable()) return Direction.DOWN;
            else return mc.player.getHorizontalFacing().getOpposite();
        }
        if (!mc.world.getBlockState(pos.add(0, 1, 0)).isReplaceable())
            return mc.player.getHorizontalFacing().getOpposite();
        return Direction.UP;
    }


    public static boolean breakBlock(BlockPos blockPos, boolean swing) {
        if (!canBreak(blockPos, mc.world.getBlockState(blockPos))) return false;

        // Creating new instance of block pos because minecraft assigns the parameter to a field, and we don't want it to change when it has been stored in a field somewhere
        BlockPos pos = blockPos instanceof BlockPos.Mutable ? new BlockPos(blockPos) : blockPos;

        if (mc.interactionManager.isBreakingBlock())
            mc.interactionManager.updateBlockBreakingProgress(pos, getDirection(blockPos));
        else mc.interactionManager.attackBlock(pos, getDirection(blockPos));

        if (swing) mc.player.swingHand(Hand.MAIN_HAND);
        else mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
        return true;
    }

    public static float getBlockBreakingSpeed(BlockState block, int slot) {
        float speed = mc.player.getInventory().main.get(slot).getMiningSpeedMultiplier(block);
        if (speed > 1.0F) {
//            speed += (float) mc.player.getAttributeValue(EntityAttributes.PLAYER_MINING_EFFICIENCY);
            int i = EnchantmentHelper.getEfficiency(mc.player);
            ItemStack itemStack = mc.player.getInventory().getStack(slot);
            if (i > 0 && !itemStack.isEmpty()) {
                speed += (float) (i * i + 1);
            }
        }

        if (StatusEffectUtil.hasHaste(mc.player)) {
            speed *= 1.0F + (float) (StatusEffectUtil.getHasteAmplifier(mc.player) + 1) * 0.2F;
        }

        if (mc.player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            float effect = switch (mc.player.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) {
                case 0 -> 0.3F;
                case 1 -> 0.09F;
                case 2 -> 0.0027F;
                default -> 8.1E-4F;
            };

            speed *= effect;
        }

//        speed *= (float) mc.player.getAttributeValue(EntityAttributes.PLAYER_BLOCK_BREAK_SPEED);
//        if (mc.player.isSubmergedIn(FluidTags.WATER)) {
//            speed *= (float) mc.player.getAttributeInstance(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED).getValue();
//        }

        if (mc.player.isSubmergedIn(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(mc.player)) {
            speed /= 5.0F;
        }

        if (!mc.player.isOnGround()) {
            speed /= 5.0F;
        }

        return speed;
    }
}
