package dev.undefinedteam.gensh1n.system.modules.world;

import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.player.PlaceEvent;
import dev.undefinedteam.gensh1n.events.player.PlayerTickEvent;
import dev.undefinedteam.gensh1n.mixins.ClientPlayerEntityMixin;
import dev.undefinedteam.gensh1n.mixins.MixinClientPlayerInteractionManager;
import dev.undefinedteam.gensh1n.rotate.Rotation;
import dev.undefinedteam.gensh1n.rotate.RotationUtils;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Category;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.utils.entity.PlayerUtils;
import dev.undefinedteam.gensh1n.utils.world.BlockInfo;
import dev.undefinedteam.gensh1n.utils.world.BlockUtil;
import dev.undefinedteam.gensh1n.utils.world.ScaUtil;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.*;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.joml.Vector2f;

import java.security.SecureRandom;
import java.util.*;

/**
 * @Author KuChaZi
 * @Date 2024/11/12 00:06
 * @ClassName: ScaffoldTest
 */
public class ScaffoldTest extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> modeValue = choice(sgGeneral, "Mode", Mode.Normal);

    private final Setting<Boolean> swing = bool(sgGeneral, "Swing", true);
    private final Setting<Boolean> towerMove = bool(sgGeneral, "Tower", false)/*.visible(() -> false)*/;
    private final Setting<Boolean> eagle = bool(sgGeneral, "Eagle", false);
    private final Setting<Boolean> telly = bool(sgGeneral, "Telly", true);
    private final Setting<Boolean> moveFix = bool(sgGeneral, "MoveFix", false);
    private final Setting<Boolean> bugFlyValue = bool(sgGeneral, "BugFly", false);
    private final Setting<Boolean> swap = bool(sgGeneral, "Swap", true);
    private final Setting<Boolean> autojump = bool(sgGeneral, "Jump", true);
    private final Setting<Boolean> keepYValue = bool(sgGeneral, "Keep Y", false);
    private final Setting<Boolean> upValue = bool(sgGeneral, "Up", false, () -> (telly.get() && !keepYValue.get()));

    private final Setting<Integer> tellyTicksSetting = intN(sgGeneral, "telly-ticks", 7, 1, 20, () -> modeValue.get().equals(Mode.Normal));


    private enum Mode {
        Normal,
        WatchdogJump,
        WatchdogGround,
        WatchdogKeepY
    }

    private float y;
    private int idkTick = 0, towerTick = 0, slot = 0;
    private boolean onGround = false;
    private BlockPos data;
    private Direction enumFacing;
    private boolean up, keepY, canTellyPlace;
    private static final List<Block> invalidBlocks = Arrays.asList(Blocks.WALL_TORCH, Blocks.LILY_PAD, Blocks.ENDER_CHEST, Blocks.BREWING_STAND, Blocks.DROPPER, Blocks.ENCHANTING_TABLE, Blocks.FURNACE, Blocks.CARROTS, Blocks.CRAFTING_TABLE, Blocks.TRAPPED_CHEST, Blocks.CHEST, Blocks.DISPENSER, Blocks.AIR, Blocks.WATER, Blocks.LAVA, Blocks.SAND, Blocks.SNOW, Blocks.TORCH, Blocks.JUKEBOX, Blocks.STONE_BUTTON, Blocks.OAK_BUTTON, Blocks.LEVER, Blocks.NOTE_BLOCK, Blocks.STONE_PRESSURE_PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.OAK_PRESSURE_PLATE, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.STONE_SLAB, Blocks.OAK_SLAB, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM, Blocks.DANDELION, Blocks.POPPY, Blocks.ANVIL, Blocks.GLASS_PANE, Blocks.BLACK_STAINED_GLASS_PANE, Blocks.IRON_BARS, Blocks.CACTUS, Blocks.LADDER, Blocks.COBWEB, Blocks.TNT);
    private double keepYCoord;
    private double lastOnGroundPosY;
    private int lastSlot;
    private boolean flyFlag = false;
    private LinkedList<List<Packet<?>>> packets = new LinkedList<>();
    private int c08PacketSize = 0;
    private boolean packetHandlerFlag = true;
    private float[] lastRotation = null;
    private boolean placeFlag = false;
    private boolean lastKeepYMode = false;
    private boolean lastJumpMode = false;
    private int placedAfterTower = 0;
    private boolean wasTowering;
    private int slowTicks;
    private int ticks;
    private int tickCounter;
    private float angle;
    private double targetZ;
    private boolean targetCalculated;
    private int ticks2;
    private int lastY;
    private float keepYaw;
    private Vector2f targetRotation;

    private int OffGroundTicks;

    public ScaffoldTest() {
        super(Categories.World, "scaffold-test", "Scaffold Test");
    }

    @Override
    public void onActivate() {
        idkTick = 5;
        placedAfterTower = 0;

        if (mc.player == null) return;

        mc.player.setSprinting(!canTellyPlace);
        mc.options.sprintKey.setPressed(!canTellyPlace);
        canTellyPlace = false;
        this.data = null;
        this.slot = -1;
        keepY = keepYValue.get();
        up = upValue.get();
        lastSlot = mc.player.getInventory().selectedSlot;
        flyFlag = false;
        c08PacketSize = 0;
        packetHandlerFlag = true;
        lastOnGroundPosY = mc.player.getY();
        lastJumpMode = modeValue.get() == Mode.WatchdogJump;
        lastKeepYMode = modeValue.get() == Mode.WatchdogKeepY;
        targetCalculated = false;
        keepYaw = PlayerUtils.getMoveYaw(mc.player.renderYaw) - 180f;

//        OffGroundTicks = tellyTicksSetting.get();
//
//        if (mc.player.isFallFlying() || BlockInfo.getBlockState(mc.player.getBlockPos().down()).isAir()) {
//            OffGroundTicks = tellyTicksSetting.get();
//        }
    }

    @Override
    public void onDeactivate() {
        if (mc.player == null) return;

//        OffGroundTicks = tellyTicksSetting.get();

        KeyBinding.setKeyPressed(mc.options.sneakKey.getDefaultKey(), false);

        int currentSlot = mc.player.getInventory().selectedSlot;
        if (slot != currentSlot) {
            sendPacketHook(new UpdateSelectedSlotC2SPacket(currentSlot));
        }

        if (bugFlyValue.get()) {
            packets.forEach(this::sendTick);
            packets.clear();
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(currentSlot + 1));
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(currentSlot));
        }

        if (modeValue.get() == Mode.WatchdogGround) {
            if (lastJumpMode || lastKeepYMode) {
                modeValue.set(Mode.valueOf(lastJumpMode ? "WatchdogJump" : "WatchdogKeepY"));
            }
            KeyBinding.setKeyPressed(mc.options.rightKey.getDefaultKey(), false);
            KeyBinding.setKeyPressed(mc.options.leftKey.getDefaultKey(), false);
        }
    }

    public void sendPacketHook(Packet<?> packet) {
        if (packet instanceof PlayerActionC2SPacket) {
            // 有问题吧
            int slotId = getCurrentSlotId((ClientPlayerEntity) packet);
            if (slotId == lastSlot) {
                return;
            }
            mc.getNetworkHandler().sendPacket(packet);
            lastSlot = slotId;
        }
    }

    public static int getCurrentSlotId(ClientPlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        return inventory.selectedSlot;
    }

    public double getYLevel() {
        if (modeValue.get() == Mode.WatchdogKeepY) {
            if (mc.options.jumpKey.isPressed()) return mc.player.getY() - 1;

            // ???????

            if (OffGroundTicks == 4) {
                return mc.player.getY() - 1;
            } else {
                return keepYCoord;
            }
        }
        if (!keepY) {
            return mc.player.getY() - 1.0;
        }
        return !PlayerUtils.isMoving() ? mc.player.getY() - 1.0 : keepYCoord;
    }

    public static Vec3d getVec3(BlockPos pos, Direction face) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        if (face == Direction.UP || face == Direction.DOWN) {
            x += getRandomInRange(0.3, -0.3);
            z += getRandomInRange(0.3, -0.3);
        } else {
            y += 0.08;
        }
        if (face == Direction.WEST || face == Direction.EAST) {
            z += getRandomInRange(0.3, -0.3);
        }
        if (face == Direction.SOUTH || face == Direction.NORTH) {
            x += getRandomInRange(0.3, -0.3);
        }
        return new Vec3d(x, y, z);
    }

    public static double getRandomInRange(double min, double max) {
        SecureRandom random = new SecureRandom();
        return min == max ? min : random.nextDouble() * (max - min) + min;
    }

    private void sendTick(List<Packet<?>> tick) {
        if (mc.getNetworkHandler() != null) {
            tick.forEach(packet -> {
                if (packet instanceof HandSwingC2SPacket) {
                    c08PacketSize -= 1;
                }
                mc.getNetworkHandler().sendPacket(packet);
            });
        }
    }

    private void setMotion(double speed, float yaw) {
        if (mc.player != null) {
            double forward = mc.player.input.movementForward;
            double strafe = mc.player.input.movementSideways;

            if (forward == 0.0D && strafe == 0.0D) {
                mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
            } else {
                if (forward != 0.0D) {
                    if (strafe > 0.0D) {
                        yaw += (forward > 0.0D ? -45 : 45);
                    } else if (strafe < 0.0D) {
                        yaw += (forward > 0.0D ? 45 : -45);
                    }
                    strafe = 0.0D;
                    if (forward > 0.0D) {
                        forward = 1;
                    } else if (forward < 0.0D) {
                        forward = -1;
                    }
                }

                double velocityX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F));
                double velocityZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F));

                mc.player.setVelocity(velocityX, mc.player.getVelocity().y, velocityZ);
            }
        }
    }

    private void setMotion(double speed) {
        setMotion(speed, mc.player != null ? mc.player.getYaw() : 0);
    }

    private float getSpeed() {
        return (float) Math.sqrt(mc.player.getX() * mc.player.getX() + mc.player.getZ() * mc.player.getZ());
    }

    private Block getBlockUnderPlayer(PlayerEntity player, World world) {
        BlockPos positionUnderPlayer = new BlockPos((int) player.getX(), (int) (player.getY() - 1.0), (int) player.getZ());
        return world.getBlockState(positionUnderPlayer).getBlock();
    }

    public int getBlockCount() {
        int count = 0;
        PlayerEntity player = mc.player;

        if (player != null) {
            DefaultedList<ItemStack> inventory = player.getInventory().main;
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.get(i);
                if (!stack.isEmpty()) {
                    Item item = stack.getItem();
                    if (item instanceof BlockItem && this.isValid(item)) {
                        count += stack.getCount();
                    }
                }
            }
        }
        return count;
    }

    private boolean isValid(final Item item) {
        if (item instanceof BlockItem) {
            Block block = ((BlockItem) item).getBlock();
            return !invalidBlocks.contains(block);
        }
        return false;
    }

    public int getAllBlockCount() {
        int count = 0;
        PlayerEntity player = mc.player;

        if (player != null) {
            DefaultedList<ItemStack> inventory = player.getInventory().main;
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.get(i);
                if (!stack.isEmpty()) {
                    Item item = stack.getItem();
                    if (item instanceof BlockItem && this.isValid(item) && stack.getCount() >= 3) {
                        count += stack.getCount() - 2;
                    }
                }
            }
        }
        return count;
    }

    public int getBestSpoofSlot() {
        int spoofSlot = 5;
        PlayerEntity player = mc.player;

        if (player != null) {
            DefaultedList<ItemStack> inventory = player.getInventory().main;
            for (int i = 36; i < 45; ++i) {
                if (inventory.get(i).isEmpty()) {
                    spoofSlot = i - 36;
                    break;
                }
            }
        }

        return spoofSlot;
    }

    private void getBlock(int switchSlot) {
        if (mc.player != null) {
            for (int i = 9; i < 45; ++i) {
                ItemStack itemStack = mc.player.getInventory().getStack(i);
                if (!itemStack.isEmpty() && (mc.currentScreen == null || mc.currentScreen instanceof InventoryScreen)) {
                    if (itemStack.getItem() instanceof BlockItem) {
                        BlockItem block = (BlockItem) itemStack.getItem();
                        if (isValid(block) && swap.get()) {
                            if (36 + switchSlot != i) {
                                mc.player.getInventory().selectedSlot = i;  // Swap slots by setting the selected slot
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    public int getBlockSlot() {

        if (mc.player != null) {
            DefaultedList<ItemStack> inventory = mc.player.getInventory().main;

            for (int i = 0; i < 8; i++) {
                ItemStack itemStack = inventory.get(i);
                if (!itemStack.isEmpty() && itemStack.getItem() instanceof BlockItem blockItem) {
                    if (itemStack.getCount() >= 3) {
                        Block block = blockItem.getBlock();
                        BlockState blockState = block.getDefaultState();

                        BlockPos blockPos = mc.player.getBlockPos();
                        if (blockState.isFullCube(mc.world, blockPos)) {//&& !invalidBlocks.contains(block)
                            return i;
                        }
                    }
                }
            }

            for (int i = 0; i < 8; i++) {
                ItemStack itemStack = inventory.get(i);
                if (!itemStack.isEmpty() && itemStack.getItem() instanceof BlockItem blockItem) {
                    if (itemStack.getCount() >= 3) {
                        Block block = blockItem.getBlock();
                        if (!invalidBlocks.contains(block)) {
                            return i;
                        }
                    }
                }
            }
        }

        return -1;
    }

    private ItemStack switchToBlock() {
        int blockSlot;
        ItemStack itemStack;
        PlayerEntity player = mc.player;

        blockSlot = getBlockSlot();

        if (blockSlot == -1)
            return null;

//        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SELECT_SLOT, blockSlot - 36));

        itemStack = player.getInventory().getStack(blockSlot);
        return itemStack;
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (!mc.player.isOnGround()) {
            OffGroundTicks++;
        } else {
            OffGroundTicks = 0;
        }
    }


    @EventHandler
    public void onMotion(PlayerTickEvent event) {
        if (this.idkTick > 0) {
            --this.idkTick;
        }

        if (mc.player.isOnGround()) {
            lastOnGroundPosY = mc.player.getY();
        }

//        // SmoothCameraComponent 设置
//        if (smoothCamera.get() && !mc.options.jumpKey.isPressed() && !modeValue.get().contains("Jump")) {
//            double yLevel = lastOnGroundPosY + ((telly.get() || modeValue.get().contains("KeepY")) ? 1.0 : 0.0);
//            SmoothCameraComponent.setY(yLevel);
//        }

        // Eagle 模式
        if (eagle.get()) {
            if (mc.world.getBlockState(new BlockPos((int) mc.player.getX(), (int) (mc.player.getY() - 1), (int) mc.player.getZ())).isAir()) {
                KeyBinding.setKeyPressed(mc.options.sneakKey.getDefaultKey(), mc.player.isOnGround());
            } else if (mc.player.isOnGround()) {
                KeyBinding.setKeyPressed(mc.options.sneakKey.getDefaultKey(), false);
            }
        }

        // WatchdogGround 模式
        if (modeValue.get() == Mode.WatchdogGround) {
            mc.player.setSprinting(false);
            PlayerUtils.strafe(getSpeed() / 1.3f);
        }

        // WatchdogKeepY 模式
        if (modeValue.get() == Mode.WatchdogKeepY) {
            if (mc.player.isOnGround() && PlayerUtils.isMoving()) {
                mc.player.setSprinting(false);
                setMotion(0.47F);
                mc.player.jump();
            }
            if (PlayerUtils.isMoving() && OffGroundTicks == 1) {
                setMotion(0.30F);
            }
        }

        if (this.getBlockCount() <= 5 && getAllBlockCount() > 5) {
            int spoofSlot = this.getBestSpoofSlot();
            this.getBlock(spoofSlot);
        }

        ItemStack itemStack = switchToBlock();
        if (itemStack == null) return;

        if (modeValue.get() == Mode.WatchdogJump) {
            mc.player.setSprinting(false);
            if (mc.player.isOnGround() && PlayerUtils.isMoving()) {
                PlayerUtils.strafe(0.44);
                mc.player.jump();
            }
        }

        if (mc.options.jumpKey.isPressed() && PlayerUtils.isMoving()) {
            onGround = mc.player.isOnGround();
        } else {
            onGround = false;
        }

        if (towerMove.get()) {
            tickCounter++;
            ticks++;

            if (tickCounter >= 23) {
                tickCounter = 1;
                ticks = 100;
            }

            if (mc.player.isOnGround()) {
                ticks = 0;
            }

            if (!PlayerUtils.isMoving()) {
                if (!targetCalculated) {
                    targetZ = Math.floor(mc.player.getZ()) + 0.99999999999998;
                    targetCalculated = true;
                }
                ticks2++;

                if (Math.abs(lastOnGroundPosY - mc.player.getY()) >= 1) {
                    if (ticks2 == 1) {
                        PlayerUtils.stop();
                        mc.player.setPos(mc.player.getX(), mc.player.getY(), (mc.player.getZ() + targetZ) / 2);
                    } else if (ticks2 == 2) {
                        PlayerUtils.stop();
                        mc.player.setPos(mc.player.getX(), mc.player.getY(), targetZ);
                        ticks2 = 0;
                        targetCalculated = false;
                    }
                } else {
                    ticks2 = 0;
                    targetCalculated = false;
                }
            }
        }

//        if (mc.player == null || mc.world == null) return;

        if (mc.options.jumpKey.isPressed() && towerMove.get() && PlayerUtils.isMoving()) {
            mc.player.jump();
        }

        if ((up || keepY) && mc.player.isOnGround() && PlayerUtils.isMoving() && !mc.options.jumpKey.isPressed() && autojump.get()) {
            mc.player.jump();
        }
    }

    @EventHandler
    public void onJump(TickEvent.Pre event) {
        if (mc.player == null) return;

//        if (getBlockSlot() < 0) return;
        if (!telly.get()) {
            canTellyPlace = true;
        }
        if (bugFlyValue.get()) {
            packets.add(new ArrayList<>());

            if (c08PacketSize >= 12 && !flyFlag) {
                flyFlag = true;
                while (c08PacketSize > 2) {
                    poll();
                }
            }

            while (flyFlag && c08PacketSize > 2) {
                poll();
            }
        }

        final ItemStack itemStack = switchToBlock();
        if (itemStack == null) return;

        if (telly.get()) {
            up = mc.options.jumpKey.isPressed();
            keepY = !up;
        } else {
            up = upValue.get();
            keepY = modeValue.get() == Mode.WatchdogKeepY || keepYValue.get();
        }

        if (mc.player.isOnGround()) {
            keepYCoord = Math.floor(mc.player.getY() - 1.0);
        }

//        if (getBlockSlot() < 0) {
//            return;
//        }

        this.findBlock();

        if (telly.get()) {
            mc.options.sprintKey.setPressed(true);
            if (canTellyPlace && !mc.player.isOnGround() && PlayerUtils.isMoving()) {
                mc.player.setSprinting(false);
            }
//            canTellyPlace = mc.player.getOffGroundTicks() >= (up ? (mc.player.age % 16 == 0 ? 2 : 1) : 2.9);
            canTellyPlace = OffGroundTicks >= (up ? (mc.player.age % 16 == 0 ? 2 : 1) : 2.9);

        }

        if (!modeValue.equals(Mode.Normal) && data != null && !modeValue.equals(Mode.WatchdogGround)) {
            try {
                float[] rotations = lastRotation = faceBlock(data);
                rotM.getRotationVec(rotations[0], rotations[1]);
//                rotM.isApplyToPlayer();
            } catch (Exception e) {
                if (lastRotation != null) {
//                    Faiths.INSTANCE.getRotationManager().setRotation(new Rotation(lastRotation[0], lastRotation[1]), 180, moveFix.get());
                } else {
                    e.printStackTrace();
                }
            }
        } else if (modeValue.get() == Mode.WatchdogKeepY) {
            if (mc.player.isOnGround()) {
                keepYaw = PlayerUtils.getMoveYaw(mc.player.getYaw()) - 180f;
            }
            float[] rotations = new float[]{keepYaw, y};
            rotM.getRotationVec(rotations[0], rotations[1]);
//            Faiths.INSTANCE.getRotationManager().setRotation(new Rotation(rotations[0], rotations[1]), 180, moveFix.get());
        }

        if (!canTellyPlace) return;

        if (data != null && modeValue.get() == Mode.Normal) {
            float[] rot = RotationUtils.getRotationBlock(data);
            float yaw = rot[0];
            float pitch = rot[1];
            rotM.getRotationVec(yaw, pitch);
//            Faiths.INSTANCE.getRotationManager().setRotation(new Rotation(yaw, pitch), 180, moveFix.get());
        }

    }

    private void findBlock() {
        if (PlayerUtils.isMoving() && keepY) {
            boolean shouldGoDown = false;
            BlockPos blockPosition = new BlockPos((int) mc.player.getX(), (int) getYLevel(), (int) mc.player.getZ());

            if (BlockUtil.isValidBlock(blockPosition) || search(blockPosition, !shouldGoDown)) {
                return;
            }

            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (search(blockPosition.add(x, 0, z), !shouldGoDown)) {
                        return;
                    }
                }
            }
        } else {
            this.data = getBlockPos();
        }
    }

    private double calcStepSize(double range) {
        double accuracy = 6;
        accuracy += accuracy % 2; // If it is set to uneven it changes it to even. Fixes a bug
        return Math.max(range / accuracy, 0.01);
    }

    private boolean search(final BlockPos blockPosition, final boolean checks) {
        final Vec3d eyesPos = new Vec3d(mc.player.getX(), mc.player.getBoundingBox().minY + mc.player.getEyeHeight(EntityPose.STANDING), mc.player.getZ());

        ScaUtil.PlaceRotation placeRotation = null;

        double xzRV = 0.5;
        double yRV = 0.5;
        double xzSSV = calcStepSize(xzRV);
        double ySSV = calcStepSize(xzRV);
        for (final Direction side : Direction.values()) {
            final BlockPos neighbor = blockPosition.offset(side);

            if (!BlockUtil.isValidBlock(neighbor)) continue;

            final Vec3d dirVec = new Vec3d(side.getOffsetX(), side.getOffsetY(), side.getOffsetZ());
            for (double xSearch = 0.5 - xzRV / 2; xSearch <= 0.5 + xzRV / 2; xSearch += xzSSV) {
                for (double ySearch = 0.5 - yRV / 2; ySearch <= 0.5 + yRV / 2; ySearch += ySSV) {
                    for (double zSearch = 0.5 - xzRV / 2; zSearch <= 0.5 + xzRV / 2; zSearch += xzSSV) {
                        final Vec3d posVec = new Vec3d(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ()).add(xSearch, ySearch, zSearch);
                        final double distanceSqPosVec = eyesPos.squaredDistanceTo(posVec);
                        final Vec3d hitVec = posVec.add(dirVec.multiply(0.5));

                        if (checks && (eyesPos.squaredDistanceTo(hitVec) > 18.0 || distanceSqPosVec > eyesPos.squaredDistanceTo(posVec.add(dirVec)) || mc.world.raycast(new RaycastContext(eyesPos, hitVec, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, (Entity) null)) != null))
                            continue;

                        final Rotation rotation = getRotation(hitVec, eyesPos);

                        final Vec3d vecRot = RotationUtils.getVectorForRotation(rotation);
                        final Vec3d rotationVector = new Vec3d(vecRot.x, vecRot.y, vecRot.z);
                        final Vec3d vector = eyesPos.add(rotationVector.multiply(4));

                        BlockHitResult obj = mc.world.raycast(new RaycastContext(eyesPos, vector, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, (Entity) null));
                        if (obj == null || obj.getType() != BlockHitResult.Type.BLOCK || !obj.getBlockPos().equals(neighbor)) {
                            continue;
                        }

                        if (placeRotation == null || rotM.getRotationDifference(rotation) < rotM.getRotationDifference(placeRotation.getRotation())) {
                            placeRotation = new ScaUtil.PlaceRotation(new ScaUtil.PlaceInfo(neighbor, side.getOpposite(), hitVec), rotation);
                        }
                    }
                }
            }
        }

        if (placeRotation == null) return false;

        data = placeRotation.getPlaceInfo().getBlockPos();
        enumFacing = placeRotation.getPlaceInfo().getEnumFacing();

        return true;
    }


    private Rotation getRotation(Vec3d hitVec, Vec3d eyesPos) {
        final double diffX = hitVec.x - eyesPos.x;
        final double diffY = hitVec.y - eyesPos.y;
        final double diffZ = hitVec.z - eyesPos.z;

        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F);

        float pitch = MathHelper.wrapDegrees((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)));

        return new Rotation(yaw, pitch);
    }


    public Vec3d getVec3(BlockPos blockPos, Block block) {
        BlockState blockState = block.getDefaultState();

        double ex = MathHelper.clamp(mc.player.getX(), blockPos.getX(), blockPos.getX() + 1);
        double ey = MathHelper.clamp(keepY ? getYLevel() : mc.player.getY(), blockPos.getY(), blockPos.getY() + 1);
        double ez = MathHelper.clamp(mc.player.getZ(), blockPos.getZ(), blockPos.getZ() + 1);

        if (blockState.getBlock() instanceof SlabBlock) {
            ey = MathHelper.clamp(keepY ? getYLevel() : mc.player.getY(), blockPos.getY(), blockPos.getY() + 0.5);
        }

        return new Vec3d(ex, ey, ez);
    }

    private BlockPos getBlockPos() {
        BlockPos playerPos = new BlockPos((int) mc.player.getX(), (int) getYLevel(), (int) mc.player.getZ());

        ArrayList<Vec3d> positions = new ArrayList<>();
        HashMap<Vec3d, BlockPos> hashMap = new HashMap<>();

        for (int x = playerPos.getX() - 5; x <= playerPos.getX() + 5; ++x) {
            for (int y = playerPos.getY() - 1; y <= playerPos.getY(); ++y) {
                for (int z = playerPos.getZ() - 5; z <= playerPos.getZ() + 5; ++z) {
                    BlockPos blockPos = new BlockPos(x, y, z);

                    if (isValidBlock(blockPos)) {
                        Block block = mc.world.getBlockState(blockPos).getBlock();
                        Vec3d vec3 = getVec3(blockPos, block);
                        positions.add(vec3);
                        hashMap.put(vec3, blockPos);
                    }
                }
            }
        }

        if (!positions.isEmpty()) {
            positions.sort(Comparator.comparingDouble(this::getBestBlock));
            return hashMap.get(positions.get(0));
        } else {
            return null;
        }
    }

    private boolean isValidBlock(final BlockPos blockPos) {// !(block instanceof LiquidBlock) &&
        Block block = mc.world.getBlockState(blockPos).getBlock();
        return !(block instanceof AirBlock) && !(block instanceof ChestBlock) &&
            !(block instanceof FurnaceBlock) && !(block instanceof LadderBlock) && !(block instanceof TntBlock);
    }


    private double getBestBlock(Vec3d vec3) {
        return mc.player.squaredDistanceTo(vec3.x, vec3.y, vec3.z);
    }


    public float[] faceBlock(final BlockPos blockPos) {
        if (blockPos == null)
            return null;

        Rotation vecRotation = null;

        final Vec3d eyesPos = new Vec3d(mc.player.getX(), mc.player.getBoundingBox().minY + mc.player.getEyeHeight(EntityPose.STANDING), mc.player.getZ());
        final Vec3d predictEyesPos = eyesPos.add(mc.player.getVelocity());

        for (double xSearch = 0.1D; xSearch < 0.9D; xSearch += 0.1D) {
            for (double ySearch = 0.1D; ySearch < 0.9D; ySearch += 0.1D) {
                for (double zSearch = 0.1D; zSearch < 0.9D; zSearch += 0.1D) {
                    final Vec3d posVec = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()).add(xSearch, ySearch, zSearch);
                    final double dist = eyesPos.squaredDistanceTo(posVec);

                    final double diffX = posVec.getX() - eyesPos.getX();
                    final double diffY = posVec.getY() - eyesPos.getY();
                    final double diffZ = posVec.getZ() - eyesPos.getZ();

                    final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

                    final Rotation rotation = new Rotation(
                        MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F),
                        MathHelper.wrapDegrees((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)))
                    );

                    final Vec3d rotationVector = rotM.getRotationVec(rotation);
                    final Vec3d vector = eyesPos.add(rotationVector.multiply(dist));
                    final Vec3d predictVector = predictEyesPos.add(rotationVector.multiply(dist));

                    final BlockHitResult obj = mc.world.raycast(new RaycastContext(eyesPos, vector, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));
                    final BlockHitResult predictObj = mc.world.raycast(new RaycastContext(predictEyesPos, predictVector, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));

                    if (obj.getType() == HitResult.Type.BLOCK && obj.getBlockPos().equals(blockPos) &&
                        predictObj.getType() == HitResult.Type.BLOCK && predictObj.getBlockPos().equals(blockPos)) {
                        final Rotation currentVec = rotation;

                        if (vecRotation == null || rotM.getRotationDifference(vecRotation, new Vector2f(PlayerUtils.getMoveYaw(mc.player.getYaw()) - 180f, rotM.lastRotation.yaw)) > rotM.getRotationDifference(currentVec, new Vector2f(PlayerUtils.getMoveYaw(mc.player.getYaw()) - 180f, rotM.lastRotation.yaw))) {
                            vecRotation = currentVec;
                        }
                    }
                }
            }
        }

        final Rotation rotation = new Rotation(PlayerUtils.getMoveYaw(mc.player.getYaw()) - 180f, (float) (Math.random() * (83.5f - 79.5f) + 79.5f)); // 修正 MathUtils.getRandomInRange
        final Vec3d rotationVector = rotM.getRotationVec(rotation);
        final Vec3d vector = eyesPos.add(rotationVector.multiply(100.0));
        final Vec3d predictVector = predictEyesPos.add(rotationVector.multiply(100.0));

        final BlockHitResult obj = mc.world.raycast(new RaycastContext(eyesPos, vector, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));
        final BlockHitResult predictObj = mc.world.raycast(new RaycastContext(predictEyesPos, predictVector, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));

        if (obj.getType() == HitResult.Type.BLOCK && obj.getBlockPos().equals(blockPos) &&
            predictObj.getType() == HitResult.Type.BLOCK && predictObj.getBlockPos().equals(blockPos)) {
            final Rotation currentVec = rotation;

            if (vecRotation == null || rotM.getRotationDifference(vecRotation, new Vector2f(PlayerUtils.getMoveYaw(mc.player.getYaw()) - 180f, rotM.lastRotation.yaw)) > rotM.getRotationDifference(currentVec, new Vector2f(PlayerUtils.getMoveYaw(mc.player.getYaw()) - 180f, rotM.lastRotation.yaw))) {
                vecRotation = currentVec;
            }
        }

        return new float[]{vecRotation.yaw, vecRotation.pitch};
    }


    private void poll() {
        if (packets.isEmpty()) return;
        this.sendTick(packets.getFirst());
        packets.removeFirst();
    }

    @EventHandler
    public void onPlace(PlaceEvent event) {
        if (!telly.get()) {
            mc.options.sprintKey.setPressed(false);
        }

        if (mc.player == null) return;

        final ItemStack itemStack = switchToBlock();
        if (itemStack == null) return;

        place(itemStack);
        event.cancel();

    }

    private void place(final ItemStack block) {
        if (!canTellyPlace) return;
        if (data != null) {
            Direction enumFacing = keepY ? this.enumFacing : this.getPlaceSide(this.data);
            if (enumFacing == null) return;

//            if (mc.player.interactionManager.interactBlock(mc.player, mc.world, block, Hand.MAIN_HAND, this.data, enumFacing, getVec3(data, enumFacing))) {
                if ((lastJumpMode || lastKeepYMode) && modeValue.get() == Mode.WatchdogGround) {
                    placedAfterTower++;
                    if (placedAfterTower >= 2) {
                        modeValue.set(Mode.valueOf(lastJumpMode ? "WatchdogJump" : "WatchdogKeepY"));
                    }
                }
                y = 80.8964f;

                if (swing.get()) {
                    mc.player.swingHand(Hand.MAIN_HAND);
//                } else {
//                    mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, this.data));
                }
//            }
        }
    }

    private Direction getPlaceSide(BlockPos blockPos) {
        ArrayList<Vec3d> positions = new ArrayList<>();
        HashMap<Vec3d, Direction> hashMap = new HashMap<>();
        BlockPos playerPos = new BlockPos((int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ());
        BlockPos bp;
        Vec3d vec3;

        if (isAirBlock(blockPos.add(0, 1, 0)) && !blockPos.add(0, 1, 0).equals(playerPos) && !mc.player.isOnGround()) {
            bp = blockPos.add(0, 1, 0);
            vec3 = this.getBestHitFeet(bp);
            if (vec3 != null) {
                positions.add(vec3);
                hashMap.put(vec3, Direction.UP);
            }
        }

        if (isAirBlock(blockPos.add(1, 0, 0)) && !blockPos.add(1, 0, 0).equals(playerPos)) {
            bp = blockPos.add(1, 0, 0);
            vec3 = this.getBestHitFeet(bp);
            if (vec3 != null) {
                positions.add(vec3);
                hashMap.put(vec3, Direction.EAST);
            }
        }

        if (isAirBlock(blockPos.add(-1, 0, 0)) && !blockPos.add(-1, 0, 0).equals(playerPos)) {
            bp = blockPos.add(-1, 0, 0);
            vec3 = this.getBestHitFeet(bp);
            if (vec3 != null) {
                positions.add(vec3);
                hashMap.put(vec3, Direction.WEST);
            }
        }

        if (isAirBlock(blockPos.add(0, 0, 1)) && !blockPos.add(0, 0, 1).equals(playerPos)) {
            bp = blockPos.add(0, 0, 1);
            vec3 = this.getBestHitFeet(bp);
            if (vec3 != null) {
                positions.add(vec3);
                hashMap.put(vec3, Direction.SOUTH);
            }
        }

        if (isAirBlock(blockPos.add(0, 0, -1)) && !blockPos.add(0, 0, -1).equals(playerPos)) {
            bp = blockPos.add(0, 0, -1);
            vec3 = this.getBestHitFeet(bp);
            if (vec3 != null) {
                positions.add(vec3);
                hashMap.put(vec3, Direction.NORTH);
            }
        }

        positions.removeIf(Objects::isNull);

        positions.sort(Comparator.comparingDouble((vec3x) -> mc.player.squaredDistanceTo(vec3x.x, vec3x.y, vec3x.z)));

        if (!positions.isEmpty()) {
            vec3 = this.getBestHitFeet(this.data);
            if (vec3 != null && mc.player.squaredDistanceTo(vec3.x, vec3.y, vec3.z) >= mc.player.squaredDistanceTo(positions.get(0).x, positions.get(0).y, positions.get(0).z)) {
                return hashMap.get(positions.get(0));
            }
        }

        return null;
    }


    private Vec3d getBestHitFeet(BlockPos blockPos) {
        BlockState blockState = mc.world.getBlockState(blockPos);
        Block block = blockState.getBlock();

        VoxelShape voxelShape = blockState.getCollisionShape(mc.world, blockPos);
        if (voxelShape.isEmpty()) {
            return null;
        }

        double ex = MathHelper.clamp(mc.player.getX(), blockPos.getX(), blockPos.getX() + 1.0);
        double ey = MathHelper.clamp(keepY ? getYLevel() : mc.player.getY(), blockPos.getY(), blockPos.getY() + 1.0);
        double ez = MathHelper.clamp(mc.player.getZ(), blockPos.getZ(), blockPos.getZ() + 1.0);

        return new Vec3d(ex, ey, ez);
    }


    private boolean isAirBlock(final BlockPos blockPos) {
        final Block block = mc.world.getBlockState(blockPos).getBlock();
        return block == Blocks.AIR;
    }



}
