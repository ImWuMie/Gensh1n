package dev.undefinedteam.gensh1n.system.modules.world;

import dev.undefinedteam.gensh1n.events.player.PlayerTickEvent;
import dev.undefinedteam.gensh1n.events.render.Render2DBeforeHotbar;
import dev.undefinedteam.gensh1n.events.render.Render3DEvent;
import dev.undefinedteam.gensh1n.render.Renderer;
import dev.undefinedteam.gensh1n.render._new.NText;
import dev.undefinedteam.gensh1n.settings.ColorSettings;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.utils.RandomUtils;
import dev.undefinedteam.gensh1n.utils.Utils;
import dev.undefinedteam.gensh1n.utils.entity.PlayerUtils;
import dev.undefinedteam.gensh1n.utils.inventory.FindItemResult;
import dev.undefinedteam.gensh1n.utils.inventory.InvUtils;
import dev.undefinedteam.gensh1n.utils.inventory.SlotUtils;
import dev.undefinedteam.gensh1n.utils.path.block.BlockFinder;
import dev.undefinedteam.gensh1n.utils.render.color.SettingColor;
import dev.undefinedteam.gensh1n.utils.world.BlockInfo;
import icyllis.arc3d.core.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.*;
import net.minecraft.item.AirBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.*;

import static dev.undefinedteam.gensh1n.Client.mc;

@StringEncryption
@ControlFlowObfuscation
public class Scaffold extends Module {
    public Scaffold() {
        super(Categories.World, "scaffold", "Scaffold Walking");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRotation = settings.createGroup("Rotation");
    private final SettingGroup sgHotbar = settings.createGroup("Hotbar");
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Mode> mode = choice(sgGeneral, "mode", Mode.Telly);
    private final Setting<Boolean> allowTnt = bool(sgGeneral, "allow-tnt", false);

    public enum Mode {
        Normal,
        Telly
    }

    // General
    private final Setting<Integer> range = intN(sgGeneral, "range", 4, 1, 6);
    private final Setting<Integer> tellyTicksSetting = intN(sgGeneral, "telly-ticks", 7, 1, 20, () -> mode.get().equals(Mode.Telly));

    // Rotation
    private final Setting<Integer> keepLength = intN(sgRotation, "keep-length", 2, 1, 10);

    // Hotbar
    private final Setting<Integer> swapDelay = intN(sgHotbar, "swap-delay", 8, 1, 25);

    private final Setting<Boolean> renderPath = bool(sgRender, "render-path", true);
    private final ColorSettings colors = colors(sgRender, "block", renderPath::get)
        .side(new SettingColor(0, 80, 255, 30))
        .line(new SettingColor(0, 80, 255, 80));

    private final Setting<Boolean> renderBlocks = bool(sgRender, "render-blocks", true);

    private int startY = -1;

    private BlockFinder finder;
    private int ticks;
    private int tellyTicks, ticks1;

    private Vec3d aimPos = Vec3d.ZERO;

    public List<BlockPos> paths = new ArrayList<>();
    public List<BlockStacks> stacks = new ArrayList<>();

    private static class BlockStacks {
        public ItemStack stack;
        public int count;

        public BlockStacks(ItemStack stack, int count) {
            this.stack = stack;
            this.count = count;
        }
    }

    @Override
    public String getInfoString() {
        return mode.get().toString();
    }

    @Override
    public void onDeactivate() {
        startY = -1;
        ticks = tellyTicksSetting.get();
        ticks1 = 0;
        tellyTicks = tellyTicksSetting.get();
        hotbar.reset();
    }

    @Override
    public void onActivate() {
        if (mode.get().equals(Mode.Telly) && (mc.player.isFallFlying() || BlockInfo.getBlockState(mc.player.getBlockPos().down()).isAir())) {
            ticks1 = tellyTicksSetting.get();
        }
    }

    private void loadStacks() {
        int slot = -1, count;

        for (int i = 0; i <= 8; i++) {
            ItemStack itemStack = mc.player.getInventory().getStack(i);

            if (this.validItemIsBlock(itemStack)) {
                if (slot == -1) slot = i;
                count = itemStack.getCount();
                var stack = getStack(itemStack);
                if (stack != null) {
                    stack.count += count;
                } else {
                    stack = new BlockStacks(itemStack, count);
                    this.stacks.add(stack);
                }
            }
        }
    }

    private BlockStacks getStack(ItemStack stack) {
        for (BlockStacks b : stacks) {
            if (b.stack.equals(stack)) {
                return b;
            }
        }
        return null;
    }

    @EventHandler
    public void onMotion(PlayerTickEvent e) {
        stacks.clear();
        loadStacks();

        this.paths.clear();
        int telly = tellyTicksSetting.get();

        if (mode.get().equals(Mode.Telly)) {
            if (PlayerUtils.isMoving() && mc.player.isOnGround() && !mc.options.jumpKey.isPressed() && ticks > telly) {
                startY = mc.player.getBlockPos().down().getY();
                mc.player.jump();
                ticks = 0;
                tellyTicks = 0;
            }
        }

        ticks++;

        finder = new BlockFinder(mode.get().equals(Mode.Telly) && !mc.options.jumpKey.isPressed() ? mc.player.getBlockPos().withY(startY) : mc.player.getBlockPos().down(), range.get());
        finder.compute();

        tellyTicks++;

        if (mc.options.jumpKey.isPressed() || mc.player.hurtTime != 0) {
            ticks1 = tellyTicks;
        }

        if (ticks1 > 0) {
            ticks1--;
            startY = mc.player.getBlockPos().down().getY();
            tellyTicks = telly + 1;
            ticks = telly + 1;
        }

        var blocks = finder.getPath();
        if (blocks.isEmpty()) return;

        paths = finder.getRenderPath();

        BlockPos bp = blocks.getLast();
        if (bp == null) return;

        if (mode.get().equals(Mode.Telly)) {
            if (tellyTicks <= telly && !mc.options.jumpKey.isPressed() && mc.player.hurtTime == 0) return;
        }

        BlockPos finalBp = bp;
        FindItemResult result = InvUtils.findInHotbar(itemStack -> validItem(itemStack, finalBp));

        if (!result.found()) return;

        if (result.isOffhand()) {
            place(bp, Hand.OFF_HAND, hotbar.getSlot(), true, true, true);
        } else if (result.isHotbar()) {
            //InvUtils.swap(result.slot(), false);

            hotbar.selectSlot(result.slot(), swapDelay.get());
            place(bp, Hand.MAIN_HAND, result.slot(), true, true, true);
        }
    }

    @EventHandler
    private void onRender2D(Render2DBeforeHotbar e) {
        if (renderBlocks.get() && !this.stacks.isEmpty()) {
            var renderer = Renderer.MAIN;
            var font = NText.regular;

            int count = 0;
            for (BlockStacks stack : this.stacks) {
                count += stack.count;
            }

            var s = stacks.getFirst();
            if (s.stack.getItem() instanceof AirBlockItem) return;

            var name = BlockInfo.getBlockName(Block.getBlockFromItem(s.stack.getItem()));

            var suffix = "";
            var nameHeight = font.getHeight(name);
            var nameWidth = font.getWidth(name);
            var countHeight = font.getHeight(count + suffix);
            var countWidth = font.getWidth(count + suffix);

            var height = nameHeight + 4 + countHeight + 4;

            var width = Math.max(nameWidth + 6, countWidth + 6);

            {
                var paint = renderer._paint();
                paint.setRGBA(0, 0, 0, 80);
                paint.setSmoothWidth(5.0f);

                var r = renderer._renderer();

                var x = (Utils.getWindowWidth() - width) / 2;
                var y = Utils.getWindowHeight() / 2 + 20;

                r.drawRoundRect(
                    x,
                    y,
                    x + width,
                    y + height,
                    7.0f,
                    paint
                );

                font.draw(name, x + (width - nameWidth) / 2, y + 3, Color.WHITE);
                font.draw(count + suffix, x + (width - countWidth) / 2, y + 3 + nameHeight + 3, Color.WHITE);
            }
        }
    }

    @EventHandler
    private void onRender3D(Render3DEvent e) {
        if (renderPath.get()) {
            if (this.paths != null && !this.paths.isEmpty()) {
                for (BlockPos path : this.paths) {
                    e.renderer.box(path, colors.side.get(), colors.line.get(), colors.shape.get(), 0);
                }
            }
        }

        if (this.aimPos != null) {
            var box = new Box(aimPos, aimPos);
            e.renderer.box(box.expand(0.1), colors.side.get(), colors.line.get(), colors.shape.get(), 0);
        }
    }

    public boolean canPlaceBlock(BlockPos blockPos, boolean checkEntities, Block block) {
        if (blockPos == null) return false;
        if (!World.isValid(blockPos)) return false;
        if (!mc.world.getBlockState(blockPos).isReplaceable()) return false;
        return !checkEntities || mc.world.canPlace(block.getDefaultState(), blockPos, ShapeContext.absent());
    }

    public Direction getPlaceSide(BlockPos blockPos) {
        assert mc.player != null;
        Vec3d lookVec = blockPos.toCenterPos().subtract(mc.player.getEyePos());
        double bestRelevancy = -Double.MAX_VALUE;
        Direction bestSide = null;
        for (Direction side : Direction.values()) {
            BlockPos neighbor = blockPos.offset(side);
            assert mc.world != null;
            BlockState state = mc.world.getBlockState(neighbor);
            if (state.isAir() || isUnClickable(state.getBlock())) continue;
            if (!state.getFluidState().isEmpty()) continue;
            double relevancy = side.getAxis().choose(lookVec.getX(), lookVec.getY(), lookVec.getZ()) * side.getDirection().offset();
            if (relevancy > bestRelevancy) {
                bestRelevancy = relevancy;
                bestSide = side;
            }
        }

        return bestSide;
    }


    public boolean isUnClickable(Block block) {
        return block instanceof CraftingTableBlock
            || block instanceof AnvilBlock
            || block instanceof LoomBlock
            || block instanceof CartographyTableBlock
            || block instanceof GrindstoneBlock
            || block instanceof StonecutterBlock
            || block instanceof ButtonBlock
            || block instanceof AbstractPressurePlateBlock
            || block instanceof BlockWithEntity
            || block instanceof BedBlock
            || block instanceof FenceGateBlock
            || block instanceof DoorBlock
            || block instanceof NoteBlock
            || block instanceof TrapdoorBlock;
    }

    public boolean place(BlockPos pos, Hand hand, int slot, boolean rot, boolean swing, boolean checkIfEntities) {
        if (slot < SlotUtils.HOTBAR_START || slot > SlotUtils.HOTBAR_END) return false;
        Block block = Blocks.OBSIDIAN;
        ItemStack i = hand == Hand.MAIN_HAND ? mc.player.getInventory().getStack(slot) : mc.player.getInventory().getStack(SlotUtils.OFFHAND);
        if (i.getItem() instanceof BlockItem blockItem) block = blockItem.getBlock();
        if (!canPlaceBlock(pos, checkIfEntities, block)) return false;

        Vec3d hitPos = Vec3d.ofCenter(pos);

        BlockPos neighbour;
        Direction side = getPlaceSide(pos);

        if (side == null) {
            side = Direction.UP;
            neighbour = pos;
        } else {
            //var ray = RayTraceUtils.raycast(mc.player.getCameraPosVec(1), hitPos.add(side.getOffsetX() * 0.5, side.getOffsetY() * 0.5, side.getOffsetZ() * 0.5));

            neighbour = pos.offset(side);
            var x = side.getOffsetX() * 0.5;
            var z = side.getOffsetZ() * 0.5;
            hitPos = hitPos.add(x, side.getOffsetY() * 0.5, z);


            var axis = switch (side) {
                case UP, DOWN -> Direction.Axis.Y;
                case NORTH, SOUTH -> Direction.Axis.X;
                case WEST, EAST -> Direction.Axis.Z;
            };

            var oBox = BlockInfo.box(pos);
            var box = BlockInfo.box(pos).expand(axis.choose(0.1, 0, 0.1), 0, axis.choose(0.1, 0, 0.1));

            var addV = 0;
            switch (axis) {
                case X -> {
                    var playerX = mc.player.getX();

                    if (oBox.getMin(axis) < playerX && oBox.getMax(axis) > playerX) {
                        hitPos.x = mc.player.getX();
                    }

                    var add = hitPos.add(addV, 0, 0);
                    if (box.contains(add)) {
                        hitPos = add;
                    } else {
                        add = hitPos.add(-addV, 0, 0);

                        if (box.contains(add)) {
                            hitPos = add;
                        }
                    }
                }
                case Z -> {
                    var playerZ = mc.player.getZ();

                    if (oBox.getMin(axis) < playerZ && oBox.getMax(axis) > playerZ) {
                        hitPos.z = mc.player.getZ();
                    }
                    var add = hitPos.add(0, 0, addV);
                    if (box.contains(add)) {
                        hitPos = add;
                    } else {
                        add = hitPos.add(0, 0, -addV);
                        if (box.contains(add)) {
                            hitPos = add;
                        }
                    }
                }
            }

//            if (ray instanceof BlockHitResult b) {
//                hitPos.x = b.getPos().x;
//                hitPos.z = b.getPos().z;
//            }
            //hitPos = getVec3d(pos, side);
        }
        //BlockHitResult bhr = new BlockHitResult(hitPos, side.getOpposite(), neighbour, false);
        if (rot) {
            //转头 到hitpos dir为side
            //BlockPos apos = neighbour.offset(side.getOpposite());
            //var rotation = new Rotation((RotationUtils.getRotationBlock(apos)[0] + 180) % 360,RotationUtils.getRotationBlock(apos)[1]); //rotM.getRotation(hitPos, mc.player.getEyePos());
            //var eye = mc.player.getCameraPosVec(1).add(Vec3d.of(mc.player.getHorizontalFacing().getVector()));

            this.aimPos = hitPos;
            var rotation = rotM.getRotation(hitPos, mc.player.getCameraPosVec(1));

            //rotation.yaw = (rotation.yaw + 180) % 360;
//            BlockHitResult result = RayTraceUtils.raycast(range.get(),rotation,true,mc.getTickDelta());
//            if(result.getBlockPos() == neighbour && result.getSide() == side.getOpposite()) {
//            var rota = RotationUtils.getRotationBlock(neighbour);
//            var rotation = new Rotation(rota[0],rota[1]);

            var set = rotate.rotation(rotation).keep(this.keepLength.get()).reach(range.get()).set();
            var ray = rotM.getRotationOver();
//            if (raycast.get() && ray.getType().equals(HitResult.Type.BLOCK)) {
//                var blockRay = (BlockHitResult) ray;
//                var rayPos = blockRay.getBlockPos();
//                if (!rayPos.add(blockRay.getSide().getOpposite().getVector()).equals(pos)) {
//                    set = false;
//                }
//            }

            if (set && ray.getType().equals(HitResult.Type.BLOCK))
                interact((BlockHitResult) ray, hand, swing);
//            if (set) {
//                ((IMinecraft) mc).genshin$rightClick();
//            }
        }
        return true;
    }

    public void interact(BlockHitResult blockHitResult, Hand hand, boolean swing) {
        this.aimPos = blockHitResult.getPos();
        boolean wasSneaking = mc.player.input.sneaking;
        mc.player.input.sneaking = false;

        ActionResult result = mc.interactionManager.interactBlock(mc.player, hand, blockHitResult);

        if (result.shouldSwingHand()) {
            if (swing) mc.player.swingHand(hand);
            else mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(hand));
        }

        mc.player.input.sneaking = wasSneaking;
    }

    private boolean validItem(ItemStack itemStack, BlockPos pos) {
        if (!(itemStack.getItem() instanceof BlockItem)) return false;

        Block block = ((BlockItem) itemStack.getItem()).getBlock();
        if (!allowTnt.get() && block instanceof TntBlock) return false;

        if (!Block.isShapeFullCube(block.getDefaultState().getCollisionShape(mc.world, pos))) return false;
        return !(block instanceof FallingBlock) || !FallingBlock.canFallThrough(mc.world.getBlockState(pos));
    }

    public static boolean validItemIsBlock(ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof BlockItem)) return false;

        Block block = ((BlockItem) itemStack.getItem()).getBlock();
        if (BlockFinder.isUnClickable(block)) return false;

        if (!Modules.get().get(Scaffold.class).allowTnt.get() && block instanceof TntBlock) return false;

        return !(block instanceof FallingBlock);
    }

    public static Vec3d getVec3d(BlockPos pos, Direction face) {
        double x = (double) pos.getX() + 0.5;
        double y = (double) pos.getY() + 0.5;
        double z = (double) pos.getZ() + 0.5;
        if (face == Direction.UP || face == Direction.DOWN) {
            x += RandomUtils.nextDouble(0.3, -0.3);
            z += RandomUtils.nextDouble(0.3, -0.3);
        } else {
            y += 0.08;
        }
        if (face == Direction.WEST || face == Direction.EAST) {
            z += RandomUtils.nextDouble(0.3, -0.3);
        }
        if (face == Direction.SOUTH || face == Direction.NORTH) {
            x += RandomUtils.nextDouble(0.3, -0.3);
        }
        return new Vec3d(x, y, z);
    }


}
