package dev.undefinedteam.gensh1n.system.modules.world;

import dev.undefinedteam.gensh1n.events.client.MouseButtonEvent;
import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.network.PacketEvent;
import dev.undefinedteam.gensh1n.events.render.Render3DEvent;
import dev.undefinedteam.gensh1n.settings.*;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.utils.input.KeyAction;
import dev.undefinedteam.gensh1n.utils.inventory.FindItemResult;
import dev.undefinedteam.gensh1n.utils.inventory.InvUtils;
import dev.undefinedteam.gensh1n.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.lwjgl.glfw.GLFW;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@StringEncryption
@ControlFlowObfuscation
public class PistonBreaker extends Module {
    public PistonBreaker() {
        super(Categories.World, "piston-breaker", "Destroy blocks with a piston");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");
    private final Setting<Boolean> disableReset = bool(sgGeneral, "disable-reset", true);
    private final Setting<Boolean> autoDirection = bool(sgGeneral, "auto-direction", true);
    private final Setting<Integer> directionTicks = intN(sgGeneral, "direction-ticks", 1, 1,5, autoDirection::get);

    private final Setting<Boolean> onlyRightClick = bool(sgGeneral, "only-rightclick", false);
    private final Setting<Integer> delayTicks = intN(sgGeneral, "delay-ticks", 1, 0, 40, () -> !onlyRightClick.get());

    private final Setting<Boolean> afterBreak = bool(sgGeneral, "after-break", true);
    private final Setting<Integer> afterBreakTicks = intN(sgGeneral, "after-break-delay", 2, 0, 40);

    private final Setting<Boolean> enableRenderSelecting = bool(sgRender, "render-selecting", true);
    private final ColorSettings selectingColor = colors(sgRender, "selecting", enableRenderSelecting::get)
        .side(new SettingColor(255, 0, 255, 20))
        .line(new SettingColor(255, 0, 255, 80));

    private final Setting<Boolean> enableRenderTarget = bool(sgRender, "render-target", true);
    private final ColorSettings targetColor = colors(sgRender, "target", enableRenderTarget::get)
        .side(new SettingColor(255, 20, 0, 20))
        .line(new SettingColor(255, 20, 0, 80));

    private final Setting<Boolean> enableRenderData = bool(sgRender, "render-data", true);
    private final ColorSettings dataColor = colors(sgRender, "data", enableRenderData::get)
        .side(new SettingColor(255, 255, 0, 20))
        .line(new SettingColor(255, 255, 0, 80));
    private final Setting<SettingColor> dataLineColor = color(sgRender, "data-line", new SettingColor(255, 255, 255));


    private final List<BreakData> queueList = new CopyOnWriteArrayList<>();

    private final List<BlockPos> postTasks = new CopyOnWriteArrayList<>();

    private BlockPos tmpBreakPos, tmpPistonPos, tmpTargetPos;
    private boolean selecting;

    private int ticks, afterTicks;

    private boolean modifyYaw = false;
    private boolean modifyPitch = false;
    private float yaw = 0.0F;
    private float pitch = 0.0F;
    private int rotTicks = 0;

    @Override
    public void onDeactivate() {
        if (disableReset.get()) {
            queueList.clear();
            postTasks.clear();
            tmpPistonPos = null;
            tmpBreakPos = null;
            tmpTargetPos = null;
            ticks = 0;
            afterTicks = 0;
        }
    }

    @EventHandler
    private void onMouse(MouseButtonEvent e) {
        if (e.action.equals(KeyAction.Press) && mc.currentScreen == null) {

            selecting = itemInHand();
            var hitResult = mc.crosshairTarget;

            if (selecting) {
                var left = e.button == mc.options.attackKey.boundKey.getCode();
                var right = e.button == mc.options.useKey.boundKey.getCode();

                if (hitResult instanceof BlockHitResult result) {
                    if (right && this.tmpBreakPos == null) {
                        this.pushSet(result.getBlockPos());
                        this.pop();
                        return;
                    }

                    if (left) {
                        if (this.tmpTargetPos == null || this.tmpPistonPos == null) {
                            this.pushSet(result.getBlockPos());
                            this.pop();
                        }
                    }
                }

                return;
            }

            if (onlyRightClick.get() && e.button == GLFW.GLFW_MOUSE_BUTTON_2) {
                if (queueList.isEmpty()) return;

                var slot = InvUtils.findInHotbar(Items.PISTON);

                if (slot.found()) {
                    BreakData target = queueList.getFirst();
                    if (hitResult instanceof BlockHitResult blockHitResult) {
                        for (BreakData breakData : queueList) {
                            if (blockHitResult.getBlockPos().equals(breakData.target)) {
                                target = breakData;
                            }
                        }
                    }

                    run(target, slot);
                } else nWarn("No piston found in hotbar", NSHORT);
            }
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre e) {
        if (this.modifyYaw || this.modifyPitch) {
            if (rotTicks++ > 20) {
                rotTicks = 0;
                resetRot();
            }
        }

        selecting = itemInHand();

        if (selecting) {
            return;
        }

        if (!postTasks.isEmpty() && afterBreak.get()) {
            if (afterTicks >= this.afterBreakTicks.get()) {
                for (BlockPos postTask : postTasks) {
                    mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, postTask, Direction.DOWN));
                    mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, postTask, Direction.DOWN));
                }

                afterTicks = 0;
                postTasks.clear();
            } else afterTicks++;
        }

        if (!mc.options.useKey.isPressed()) return;

        var slot = InvUtils.findInHotbar(Items.PISTON);
        if (!slot.found()) {
            nWarn("No piston found in hotbar", NSHORT);
            return;
        }

        if (this.delayTicks.get() == 0) {
            if (!queueList.isEmpty()) {
                for (BreakData data : queueList) {
                    run(data, slot);
                }
            }
        } else if (!queueList.isEmpty()) {
            if (ticks % this.delayTicks.get() == 0) {
                run(this.queueList.getFirst(), slot);
            }
            ticks++;
        }
    }

    private void run(BreakData data, FindItemResult slot) {
        var direction = data.getDirection();
        assert direction != null;

        if (autoDirection.get() && data.ticks >= 0) {
            setRot(direction);
            data.ticks--;
            return;
        }

        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
            PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, data.breakPos, direction));
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
            PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, data.breakPos, direction));

        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
            PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, data.pistonPos, direction));
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
            PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, data.pistonPos, direction));

        InvUtils.swap(slot.slot(), true);
        var hit = BlockHitResult.createMissed(data.pistonPos.toCenterPos(), direction, data.pistonPos);
        mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hit, 0));
        InvUtils.swapBack();

        this.queueList.remove(data);
        if (this.afterBreak.get())
            this.postTasks.add(data.pistonPos);
    }

    private boolean itemInHand() {
        return mc.player.getMainHandStack().getItem() instanceof SwordItem i && i.getMaterial().equals(ToolMaterials.WOOD);
    }

    @EventHandler
    private void onPacket(PacketEvent e) {
        if (e.packet instanceof PlayerMoveC2SPacket p) {
            p.yaw = onModifyLookYaw(p.yaw);
            p.pitch = onModifyLookPitch(p.pitch);
        }
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (enableRenderSelecting.get()) {
            var rBreakPos = this.tmpBreakPos != null;
            var rPistonPos = this.tmpPistonPos != null;

            var rTargetPos = rPistonPos && this.tmpTargetPos != null;

            if (rBreakPos) {
                event.renderer.box(this.tmpBreakPos, selectingColor.side.get(), selectingColor.line.get(), selectingColor.shape.get(), 0);
            }
            if (rPistonPos) {
                event.renderer.box(this.tmpPistonPos, selectingColor.side.get(), selectingColor.line.get(), selectingColor.shape.get(), 0);
            }

            if (rBreakPos && rTargetPos) {
                event.renderer.line(this.tmpBreakPos.toCenterPos(), this.tmpPistonPos.toCenterPos(), dataLineColor.get());
            }

            if (rTargetPos) {
                var box = new Box(this.tmpPistonPos.toCenterPos(), this.tmpTargetPos.toCenterPos()).expand(0.2);
                event.renderer.box(box, targetColor.side.get(), targetColor.line.get(), targetColor.shape.get(), 0);
            }
        }

        if (enableRenderData.get()) {
            for (BreakData data : queueList) {
                var rBreakPos = data.breakPos != null;
                var rPistonPos = data.pistonPos != null;

                var rTargetPos = rPistonPos && data.target != null && enableRenderTarget.get();

                if (rBreakPos) {
                    event.renderer.box(data.breakPos, dataColor.side.get(), dataColor.line.get(), dataColor.shape.get(), 0);
                }
                if (rPistonPos) {
                    event.renderer.box(data.pistonPos, dataColor.side.get(), dataColor.line.get(), dataColor.shape.get(), 0);
                }

                if (rBreakPos && rTargetPos) {
                    event.renderer.line(data.breakPos.toCenterPos(), data.pistonPos.toCenterPos(), dataLineColor.get());
                }

                if (rTargetPos) {
                    var box = new Box(data.pistonPos.toCenterPos(), data.target.toCenterPos()).expand(0.2);
                    event.renderer.box(box, targetColor.side.get(), targetColor.line.get(), targetColor.shape.get(), 0);
                }
            }
        }
    }

    private void pushSet(BlockPos pos) {
        for (BreakData breakData : queueList) {
            if (breakData.target.equals(pos) || breakData.pistonPos.equals(pos) || breakData.breakPos.equals(pos)) {
                this.tmpBreakPos = null;
                this.tmpPistonPos = null;
                this.tmpTargetPos = null;
                return;
            }
        }

        if (this.tmpBreakPos == null) {
            this.tmpBreakPos = pos;
            return;
        }
        if (this.tmpPistonPos == null) {
            this.tmpPistonPos = pos;
            return;
        }
        if (this.tmpTargetPos == null) {
            this.tmpTargetPos = pos;
        }
    }

    private void pop() {
        if (this.tmpBreakPos != null && this.tmpPistonPos != null && this.tmpTargetPos != null) {
            this.queueList.add(new BreakData(tmpBreakPos, tmpPistonPos, tmpTargetPos));
            this.tmpBreakPos = null;
            this.tmpPistonPos = null;
            this.tmpTargetPos = null;
        }
    }


    public float onModifyLookYaw(float yaw) {
        return modifyYaw ? this.yaw : yaw;
    }

    public float onModifyLookPitch(float pitch) {
        return modifyPitch ? this.pitch : pitch;
    }

    private PlayerMoveC2SPacket getLookAndOnGroundPacket(ClientPlayerEntity player) {
        var yaw = modifyYaw ? this.yaw : player.getYaw();
        var pitch = modifyPitch ? this.pitch : player.getPitch();
        return new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, player.isOnGround());
    }

    public void setRot(float yaw, float pitch) {
        modifyYaw = true;
        this.yaw = yaw;
        modifyPitch = true;
        this.pitch = pitch;
    }

    public void setRot(Direction facing) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        float yaw = switch (facing) {
            case SOUTH -> 180.0F;
            case EAST -> 90.0F;
            case NORTH -> 0.0F;
            case WEST -> -90.0F;
            default -> player == null ? 0.0F : player.getYaw();
        };
        float pitch = switch (facing) {
            case UP -> 90.0F;
            case DOWN -> -90.0F;
            default -> 0.0F;
        };
        setRot(yaw, pitch);
        if (networkHandler != null && player != null) {
            networkHandler.sendPacket(getLookAndOnGroundPacket(player));
        }
    }

    private void resetRot() {
        modifyYaw = false;
        yaw = 0.0F;
        modifyPitch = false;
        pitch = 0.0F;
        // 发送一个还原视角的数据包
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (networkHandler != null && player != null) {
            networkHandler.sendPacket(getLookAndOnGroundPacket(player));
        }
    }

    private class BreakData {
        public BlockPos breakPos, pistonPos, target;
        public int ticks = directionTicks.get();

        public BreakData(BlockPos breakPos, BlockPos pistonPos, BlockPos target) {
            this.breakPos = breakPos;
            this.pistonPos = pistonPos;
            this.target = target;
        }

        public Direction getDirection() {
            if (this.pistonPos == null) {
                return null;
            }

            var dir = Direction.DOWN;
            for (Direction value : Direction.values()) {
                if (target.add(value.getVector()).equals(pistonPos)) {
                    dir = value.getOpposite();
                    break;
                }
            }
            return dir;
        }
    }
}
