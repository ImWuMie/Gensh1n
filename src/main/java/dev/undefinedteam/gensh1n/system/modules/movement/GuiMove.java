package dev.undefinedteam.gensh1n.system.modules.movement;

import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.network.PacketEvent;
import dev.undefinedteam.gensh1n.events.player.ClickSlotEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.utils.entity.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author KuChaZi
 * @Date 2024/11/8 15:53
 * @ClassName: GuiMove
 */
//idk
@StringEncryption
@ControlFlowObfuscation
public class GuiMove extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> clickmode = choice(sgGeneral,"Mode", Mode.None);
    private final Setting<Boolean> rotate = bool(sgGeneral,"Rotate", true);
    private final Setting<Boolean> sneak = bool(sgGeneral,"Sneak", false);

    private final Queue<ClickSlotC2SPacket> storedClicks = new LinkedList<>();
    private final AtomicBoolean pause = new AtomicBoolean();

    private enum Mode {
        None, DisableClicks, Swap, Matrix, Delay, Strict
    }

    public GuiMove() {
        super(Categories.Movement, "gui-move", "Gui Move test");
    }

    public boolean isKeyPressed(int button) {
        if (button == -1)
            return false;
        return InputUtil.isKeyPressed(mc.getWindow().getHandle(), button);
    }

    @EventHandler
    public void onUpdate(TickEvent.Pre event) {
        if (mc.currentScreen != null && !(mc.currentScreen instanceof ChatScreen)) {
            for (KeyBinding k : new KeyBinding[]{mc.options.forwardKey, mc.options.backKey, mc.options.leftKey, mc.options.rightKey, mc.options.jumpKey, mc.options.sprintKey})
                k.setPressed(isKeyPressed(InputUtil.fromTranslationKey(k.getBoundKeyTranslationKey()).getCode()));

            float deltaX = 0;
            float deltaY = 0;

            if (rotate.get()) {
                if (isKeyPressed(264))
                    deltaY += 30f;

                if (isKeyPressed(265))
                    deltaY -= 30f;

                if (isKeyPressed(262))
                    deltaX += 30f;

                if (isKeyPressed(263))
                    deltaX -= 30f;

                if(deltaX != 0 || deltaY != 0)
                    mc.player.changeLookDirection(deltaX, deltaY);
            }

            if (sneak.get())
                mc.options.sneakKey.setPressed(isKeyPressed(InputUtil.fromTranslationKey(mc.options.sneakKey.getBoundKeyTranslationKey()).getCode()));
        }
    }

    @EventHandler
    public void onClickSlot(ClickSlotEvent e) {
        if(clickmode.get() == Mode.DisableClicks && (PlayerUtils.isMoving() || mc.options.jumpKey.isPressed()))
            e.cancel();
    }

    @EventHandler
    public void onPacketSend(PacketEvent e) {
        if (e.origin == PacketEvent.TransferOrigin.SEND) {
            if (!PlayerUtils.isMoving() || !mc.options.jumpKey.isPressed() || pause.get()) return;
            if (e.packet instanceof ClickSlotC2SPacket click) {
                switch (clickmode.get()) {
                    case Swap -> {
                        if (click.getActionType() != SlotActionType.PICKUP && click.getActionType() != SlotActionType.PICKUP_ALL)
                            sendPacket(new CloseHandledScreenC2SPacket(0));
                    }
                    case Strict -> {
                        if (mc.player.isOnGround() && !mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().offset(0.0, 0.000000271875, 0.0)).iterator().hasNext()) {
                            if (mc.player.isSprinting())
                                sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
                            sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 0.000000271875, mc.player.getZ(), false));
                        }
                    }
                    case Matrix -> {
                        sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
                        mc.options.forwardKey.setPressed(false);
                        mc.player.input.movementForward = 0;
                        mc.player.input.pressingForward = false;
                    }
                    case Delay -> {
                        storedClicks.add(click);
                        e.cancel();
                    }
                }
            }

            if (e.packet instanceof CloseHandledScreenC2SPacket) {
                if (clickmode.get() == Mode.Delay) {
                    pause.set(true);
                    while (!storedClicks.isEmpty())
                        sendPacket(storedClicks.poll());
                    pause.set(false);
                }
            }
        }
    }
}
