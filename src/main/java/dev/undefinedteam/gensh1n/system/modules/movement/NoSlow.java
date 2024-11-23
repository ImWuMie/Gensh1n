package dev.undefinedteam.gensh1n.system.modules.movement;

import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.network.PacketEvent;
import dev.undefinedteam.gensh1n.events.player.OffhandChangedEvent;
import dev.undefinedteam.gensh1n.events.player.SlowdownEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.system.modules.player.Blink;
import dev.undefinedteam.gensh1n.utils.network.PacketUtils;
import icyllis.modernui.view.MotionEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@StringEncryption
@ControlFlowObfuscation
public class NoSlow extends Module {
    public NoSlow() {
        super(Categories.Movement, "no-slow", "Prevents you from slowing down when sprinting with items");
    }

    private final SettingGroup sgDefault = settings.getDefaultGroup();
    public final Setting<Boolean> attackNoSlow = bool(sgDefault, "attack-noslow", true);
    public final Setting<Mode> mode = choice(sgDefault, "mode", Mode.HeypixelTest);
    public final Setting<Integer> eatTime = intN(sgDefault, "eat-time", 12, 0, 25,() -> mode.get().equals(Mode.HeypixelTest));

    public enum Mode {
        HeypixelTest,
        None
    }

    public List<BlinkPacket> pkts = new CopyOnWriteArrayList<>();
    private boolean blinking;
    private int eatTicks;

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {
        if (!pkts.isEmpty()) {
            for (BlinkPacket packet : pkts) {
                Packet pkt = packet.packet;
                pkts.remove(packet);
                PacketUtils.sendNoEvent(pkt);
            }
        }
        blinking = false;
        eatTicks = 0;
    }

    public static boolean isBlinking() {
        var noslow = Modules.get().get(NoSlow.class);
        return noslow.isActive() && noslow.blinking;
    }

    @AllArgsConstructor
    static class BlinkPacket {
        long time;
        Packet packet;
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (mode.get().equals(Mode.HeypixelTest)) {
            if (event.origin == PacketEvent.TransferOrigin.RECEIVE) return;

            if (event.packet instanceof ChatMessageC2SPacket) return;
            if (event.packet instanceof PlayerInteractEntityC2SPacket) return;
            if (event.packet instanceof PlayerPositionLookS2CPacket) {
                blinking = false;

                return;
            }

            if (blinking) {
                BlinkPacket packet = new BlinkPacket(System.currentTimeMillis(), event.packet);
                pkts.add(packet);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onPre(TickEvent.Pre e) {
        if (mode.get().equals(Mode.HeypixelTest)) {
            if (mc.player.getActiveHand().equals(Hand.OFF_HAND) && mc.player.isUsingItem() && this.isEatable(mc.player.getOffHandStack())) {
                eatTicks++;
                if (eatTicks >= eatTime.get() && !blinking) {
                    mc.player.clearActiveItem();
                    blinking = true;
                    eatTicks = 0;
                }
            } else{
                eatTicks = 0;
            }
        }
    }

    @EventHandler
    private void onChange(OffhandChangedEvent e) {
        blinking = false;
        eatTicks = 0;

        if (!pkts.isEmpty()) {
            for (BlinkPacket packet : pkts) {
                Packet pkt = packet.packet;
                pkts.remove(packet);
                PacketUtils.sendNoEvent(pkt);
            }
        }
    }

    private void send() {
        if (mc.player.getItemUseTimeLeft()
            % (
            !(mc.player.getMainHandStack().getItem() instanceof BowItem)
                && !(mc.player.getOffHandStack().getItem() instanceof BowItem)
                && !(mc.player.getMainHandStack().getItem() instanceof CrossbowItem)
                && !(mc.player.getOffHandStack().getItem() instanceof CrossbowItem)
                ? 6
                : 8
        )
            == 0) {
            Int2ObjectMap<ItemStack> modifiedStacks = new Int2ObjectOpenHashMap();
            modifiedStacks.put(36, new ItemStack(Items.BARRIER));
            mc.player.networkHandler.sendPacket(new ClickSlotC2SPacket(0, 0, 36, 0, SlotActionType.SWAP, new ItemStack(Blocks.BARRIER), modifiedStacks));
        }
    }

    private boolean isEatable(ItemStack itemStack) {
        if (itemStack != null && !itemStack.isEmpty()) {
            Item item = itemStack.getItem();
            boolean isFood = item.isFood();
//            boolean isShield = item == Items.SHIELD;
//            boolean isBow = item instanceof BowItem;
//            boolean isCrossBow = item instanceof CrossbowItem;
            return isFood;
        } else {
            return false;
        }
    }

    @EventHandler
    public void onSlow(SlowdownEvent event) {

    }
}
