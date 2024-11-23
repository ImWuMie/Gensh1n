package dev.undefinedteam.gensh1n.system.modules.world;

import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.render.Render3DEvent;
import dev.undefinedteam.gensh1n.render.ShapeMode;
import dev.undefinedteam.gensh1n.settings.*;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.UpdateTiming;
import dev.undefinedteam.gensh1n.utils.inventory.FindItemResult;
import dev.undefinedteam.gensh1n.utils.inventory.InvUtils;
import dev.undefinedteam.gensh1n.utils.render.color.SettingColor;
import dev.undefinedteam.gensh1n.utils.world.BlockInfo;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.AirBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@StringEncryption
@ControlFlowObfuscation
public class InstaNuker extends Module {
    public InstaNuker() {
        super(Categories.World, "insta-nuker", "Breaks blocks around you. (only instamine)");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Integer> blocksPerTick = intN(sgGeneral, "blocks-per-tick", 128, 1, 1024);
    private final Setting<Boolean> autoTool = bool(sgGeneral, "auto-tool", true);
    private final Setting<Boolean> checkSuitable = bool(sgGeneral, "check-suitable", true, autoTool::get);
    private final Setting<Boolean> disableReset = bool(sgGeneral, "disable-reset", false);
    private final Setting<Integer> up = intN(sgGeneral, "up", 2, 0, 5);
    private final Setting<Integer> left = intN(sgGeneral, "left", 0, 0, 5);
    private final Setting<Integer> right = intN(sgGeneral, "right", 0, 0, 5);
    private final Setting<Integer> down = intN(sgGeneral, "down", 0, 0, 5);
    private final Setting<Integer> forward = intN(sgGeneral, "forward", 2, 0, 5);
    private final Setting<Integer> back = intN(sgGeneral, "back", 0, 0, 5);

    private final Setting<UpdateTiming> updateTiming = choice(sgGeneral, "update-timing", "When to update blocks.", UpdateTiming.Pre);
    // Rendering

    private final Setting<Boolean> enableRenderBounding = sgRender.add(new BoolSetting.Builder()
        .name("bounding-box")
        .description("Enable rendering bounding box for Cube and Uniform Cube.")
        .defaultValue(true)
        .build()
    );

    private final Setting<ShapeMode> boxShapeModeBox = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("bounding-box-mode")
        .description("How the shape for the bounding box is rendered.")
        .defaultValue(ShapeMode.Both)
        .visible(enableRenderBounding::get)
        .build()
    );

    private final Setting<SettingColor> boxSideColorBox = sgRender.add(new ColorSetting.Builder()
        .name("bounding-side-color")
        .description("The side color of the bounding box.")
        .defaultValue(new SettingColor(16, 106, 144, 100))
        .visible(enableRenderBounding::get)
        .build()
    );

    private final Setting<SettingColor> boxLineColorBox = sgRender.add(new ColorSetting.Builder()
        .name("bounding-line-color")
        .description("The line color of the bounding box.")
        .defaultValue(new SettingColor(16, 106, 144, 255))
        .visible(enableRenderBounding::get)
        .build()
    );

    private final Setting<Boolean> enableRenderBreakRange = sgRender.add(new BoolSetting.Builder()
        .name("break-range")
        .description("Enable rendering bounding box for break range.")
        .defaultValue(true)
        .build()
    );

    private final Setting<ShapeMode> rangeShapeModeBox = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("range-box-mode")
        .description("How the shape for the bounding box is rendered.")
        .defaultValue(ShapeMode.Both)
        .visible(enableRenderBreakRange::get)
        .build()
    );

    private final Setting<SettingColor> rangeSideColorBox = sgRender.add(new ColorSetting.Builder()
        .name("range-side-color")
        .description("The side color of the bounding box.")
        .defaultValue(new SettingColor(16, 106, 144, 100))
        .visible(enableRenderBreakRange::get)
        .build()
    );

    private final Setting<SettingColor> rangeLineColorBox = sgRender.add(new ColorSetting.Builder()
        .name("range-line-color")
        .description("The line color of the bounding box.")
        .defaultValue(new SettingColor(16, 106, 144, 255))
        .visible(enableRenderBreakRange::get)
        .build()
    );

    private final Setting<Boolean> enableRenderBreaking = sgRender.add(new BoolSetting.Builder()
        .name("broken-blocks")
        .description("Enable rendering bounding box for Cube and Uniform Cube.")
        .defaultValue(true)
        .build()
    );

    private final Setting<ShapeMode> blockShapeModeBreak = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("render-block-mode")
        .description("How the shapes for broken blocks are rendered.")
        .defaultValue(ShapeMode.Both)
        .visible(enableRenderBreaking::get)
        .build()
    );

    private final Setting<SettingColor> blockSideColor = sgRender.add(new ColorSetting.Builder()
        .name("block-side-color")
        .description("The side color of the target block rendering.")
        .defaultValue(new SettingColor(255, 0, 0, 80))
        .visible(enableRenderBreaking::get)
        .build()
    );

    private final Setting<SettingColor> blockLineColor = sgRender.add(new ColorSetting.Builder()
        .name("block-line-color")
        .description("The line color of the target block rendering.")
        .defaultValue(new SettingColor(255, 0, 0, 255))
        .visible(enableRenderBreaking::get)
        .build()
    );


    private final Setting<Boolean> enableRenderTesting = sgRender.add(new BoolSetting.Builder()
        .name("testing-blocks")
        .description("Enable rendering bounding box for Cube and Uniform Cube.")
        .defaultValue(true)
        .build()
    );

    private final Setting<ShapeMode> testShapeModeBreak = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("testing-block-mode")
        .description("How the shapes for broken blocks are rendered.")
        .defaultValue(ShapeMode.Both)
        .visible(enableRenderTesting::get)
        .build()
    );

    private final Setting<SettingColor> testSideColor = sgRender.add(new ColorSetting.Builder()
        .name("testing-side-color")
        .description("The side color of the target block rendering.")
        .defaultValue(new SettingColor(255, 0, 0, 80))
        .visible(enableRenderTesting::get)
        .build()
    );

    private final Setting<SettingColor> testLineColor = sgRender.add(new ColorSetting.Builder()
        .name("testing-line-color")
        .description("The line color of the target block rendering.")
        .defaultValue(new SettingColor(255, 0, 0, 255))
        .visible(enableRenderTesting::get)
        .build()
    );

    @Override
    public void onDeactivate() {
        if (disableReset.get()) {
            this.mineBox = null;
            this.pos1 = null;
            this.pos2 = null;
            this.rangeBox = null;
            this.breakingPos.clear();
            this.blockTestBox = null;
        }
    }

    private BlockPos pos1 = null, pos2 = null;

    private Box mineBox;
    private Box rangeBox;
    private Box blockTestBox;

    private BlockPos raycastPos;

    private boolean selecting;

    private final List<BlockPos> breakingPos = new ArrayList<>();

    @EventHandler
    private void pre(TickEvent.Pre event) {
        if (updateTiming.get().equals(UpdateTiming.Pre) || updateTiming.get().equals(UpdateTiming.Both)) {
            update();
        }
    }

    @EventHandler
    private void post(TickEvent.Post e) {
        if (updateTiming.get().equals(UpdateTiming.Post) || updateTiming.get().equals(UpdateTiming.Both)) {
            update();
        }
    }

    private void update() {
        this.selecting = itemInHand();

        if (selecting) {
            rangeBox = null;
            blockTestBox = null;
            mineBox = null;
            breakingPos.clear();

            var leftClick = mc.options.attackKey.isPressed();
            var rightClick = mc.options.useKey.isPressed();

            HitResult hitResult = mc.getCameraEntity().raycast(2, 0, false);
            if (hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getBlockPos() != null) {
                if (leftClick) {
                    pos1 = blockHitResult.getBlockPos();
                    nInfo("坐标1设置为 ({},{},{})", NSHORT, pos1.getX(), pos1.getY(), pos1.getZ());
                }

                if (rightClick) {
                    pos2 = blockHitResult.getBlockPos();
                    nInfo("坐标2设置为 ({},{},{})", NSHORT, pos2.getX(), pos2.getY(), pos2.getZ());
                }

                raycastPos = blockHitResult.getBlockPos();
            }

            if (pos1 != null || pos2 != null) {
                if (pos1 != null && pos2 == null) {
                    if (hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getBlockPos() != null) {
                        mineBox = genBox(pos1, blockHitResult.getBlockPos());
                    }
                } else if (pos1 == null) {
                    if (hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getBlockPos() != null) {
                        mineBox = genBox(blockHitResult.getBlockPos(), pos2);
                    }
                } else {
                    mineBox = genBox(pos1, pos2);
                }
            }
            return;
        }

        if (mineBox != null) {
            BlockPos.Mutable pos1 = new BlockPos.Mutable(), pos2 = new BlockPos.Mutable();

            double pX = mc.player.getX();
            double pY = mc.player.getY();
            double pZ = mc.player.getZ();

            double pX_ = pX;
            double pZ_ = pZ;

            {
                int direction = Math.round((mc.player.getRotationClient().y % 360) / 90);
                direction = Math.floorMod(direction, 4);

                // direction == 1
                pos1.set(pX_ - forward.get(), Math.ceil(pY) - down.get(), pZ_ - right.get()); // down
                pos2.set(pX_ + back.get() + 1, Math.ceil(pY + up.get() + 1), pZ_ + left.get() + 1); // up

                // Only change me if you want to mess with 3D rotations:
                // I messed with it
                switch (direction) {
                    case 0 -> {
                        pZ_ += 1;
                        pX_ += 1;
                        pos1.set(pX_ - (right.get() + 1), Math.ceil(pY) - down.get(), pZ_ - (back.get() + 1)); // down
                        pos2.set(pX_ + left.get(), Math.ceil(pY + up.get() + 1), pZ_ + forward.get()); // up
                    }
                    case 2 -> {
                        pX_ += 1;
                        pZ_ += 1;
                        pos1.set(pX_ - (left.get() + 1), Math.ceil(pY) - down.get(), pZ_ - (forward.get() + 1)); // down
                        pos2.set(pX_ + right.get(), Math.ceil(pY + up.get() + 1), pZ_ + back.get()); // up
                    }
                    case 3 -> {
                        pX_ += 1;
                        pos1.set(pX_ - (back.get() + 1), Math.ceil(pY) - down.get(), pZ_ - left.get()); // down
                        pos2.set(pX_ + forward.get(), Math.ceil(pY + up.get() + 1), pZ_ + right.get() + 1); // up
                    }
                }
            }

            this.rangeBox = adjustBox(mineBox, genBox(pos1, pos2));
            var blocks = getAllInBox(pos1, pos2);
            breakingPos.clear();

            if (!blocks.isEmpty()) {
                for (var map : blocks.entrySet()) {
                    var item = map.getKey();

                    if (autoTool.get() && item.slot() != mc.player.getInventory().selectedSlot) {
                        InvUtils.swap(item.slot(), false);
                    }

                    for (var pos : map.getValue()) {
                        var block = BlockInfo.getBlock(pos);

                        if (block instanceof AirBlock || block instanceof FluidBlock) continue;

                        var posVec = pos.toCenterPos();
                        var eyesPos = mc.player.getEyePos();

                        double distanceSqPosVec = eyesPos.squaredDistanceTo(posVec);

                        for (Direction side : Direction.values()) {
                            Vec3d hitVec = posVec.add(Vec3d.of(side.getVector()).multiply(0.5));

                            // check if side is facing towards player
                            if (eyesPos.squaredDistanceTo(hitVec) >= distanceSqPosVec)
                                continue;

                            // break block
                            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                                PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, side));
                            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                                PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, side));

                            break;
                        }
                        breakingPos.add(pos);
                    }
                }
            }
        }
    }

    private Box genBox(BlockPos p1, BlockPos p2) {
        //return new Box(p1.getX(), p1.getY(), p1.getZ(), p2.getX() + 1, p2.getY() + 1, p2.getZ() + 1);
        return Box.enclosing(p1, p2);
    }

    private boolean itemInHand() {
        return mc.player.getMainHandStack().getItem() instanceof SwordItem i && i.getMaterial().equals(ToolMaterials.WOOD);
    }

    public Map<FindItemResult, List<BlockPos>> getAllInBox(BlockPos from, BlockPos to) {
        Map<FindItemResult, List<BlockPos>> blocksMap = new HashMap<>();
        int blocks = 0;
        BlockPos min = new BlockPos(Math.min(from.getX(), to.getX()),
            Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        BlockPos max = new BlockPos(Math.max(from.getX(), to.getX()),
            Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));

        for (int x = min.getX(); x <= max.getX(); x++)
            for (int y = min.getY(); y <= max.getY(); y++)
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    var pos = new BlockPos(x, y, z);
                    var best = findBest(pos);
                    if (testBlock(pos, best) && blocks <= blocksPerTick.get()) {
                        if (blocksMap.containsKey(best)) {
                            blocksMap.get(best).add(pos);
                        } else {
                            List<BlockPos> blockPosList = new ArrayList<>();
                            blockPosList.add(pos);
                            blocksMap.put(best, blockPosList);
                        }
                        blocks++;
                    }
                }

        return blocksMap;
    }

    private Box adjustBox(Box target, Box current) {
        if (!current.intersects(target)) return null;

        double minX = current.minX, minY = current.minY, minZ = current.minZ;
        double maxX = current.maxX, maxY = current.maxY, maxZ = current.maxZ;

        if (current.minX < target.minX) minX = target.minX;
        if (current.minY < target.minY) minY = target.minY;
        if (current.minZ < target.minZ) minZ = target.minZ;
        if (current.maxX > target.maxX) maxX = target.maxX;
        if (current.maxY > target.maxY) maxY = target.maxY;
        if (current.maxZ > target.maxZ) maxZ = target.maxZ;

        var finalBox = new Box(minX, minY, minZ, maxX, maxY, maxZ);

        if (!target.contains(
            finalBox.getMin(Direction.Axis.X),
            finalBox.getMin(Direction.Axis.Y),
            finalBox.getMin(Direction.Axis.Z)
        ) || !target.contains(
            finalBox.getMax(Direction.Axis.X),
            finalBox.getMax(Direction.Axis.Y),
            finalBox.getMax(Direction.Axis.Z)
        )) {
            if (current.minX > target.minX) minX = current.minX;
            if (current.minY > target.minY) minY = current.minY;
            if (current.minZ > target.minZ) minZ = current.minZ;
            if (current.maxX < target.maxX) maxX = current.maxX;
            if (current.maxY < target.maxY) maxY = current.maxY;
            if (current.maxZ < target.maxZ) maxZ = current.maxZ;

            finalBox = new Box(minX, minY, minZ, maxX, maxY, maxZ);
            if (!finalBox.intersects(target)) {
                finalBox = null;
            }
        }
        return finalBox;
    }

    private boolean testBlock(BlockPos pos, FindItemResult result) {
        if (this.mineBox == null) return false;
        this.blockTestBox = new Box(pos);

        if (!result.found() && autoTool.get()) return false;

        return this.mineBox.contains(pos.toCenterPos());
    }

    private FindItemResult findBest(BlockPos pos) {
        if (!autoTool.get()) {
            return new FindItemResult(mc.player.getInventory().selectedSlot, 1);
        }
        return InvUtils.findInstaMineTool(pos, this.checkSuitable.get());
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (enableRenderBounding.get()) {
            if (this.mineBox != null) {
                event.renderer.box(mineBox, boxSideColorBox.get(), boxLineColorBox.get(), boxShapeModeBox.get(), 0);
            }
        }

        if (enableRenderBreakRange.get()) {
            if (this.rangeBox != null) {
                event.renderer.box(rangeBox, rangeSideColorBox.get(), rangeLineColorBox.get(), rangeShapeModeBox.get(), 0);
            }
        }

        if (enableRenderBreaking.get()) {
            if (!this.breakingPos.isEmpty()) {
                for (BlockPos pos : this.breakingPos) {
                    event.renderer.box(pos, blockSideColor.get(), blockLineColor.get(), blockShapeModeBreak.get(), 0);
                }
            }
        }

        if (enableRenderTesting.get()) {
            if (this.blockTestBox != null) {
                event.renderer.box(blockTestBox, testSideColor.get(), testLineColor.get(), testShapeModeBreak.get(), 0);
            }
        }

        if (selecting && raycastPos != null) {
            event.renderer.box(raycastPos, boxSideColorBox.get(), boxLineColorBox.get(), boxShapeModeBox.get(), 0);
        }
    }
}
