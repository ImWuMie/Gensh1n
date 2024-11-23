package dev.undefinedteam.gensh1n.system.modules.player;

import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.network.PacketEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.utils.inventory.InvUtils;
import dev.undefinedteam.gensh1n.utils.world.BlockInfo;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@StringEncryption
@ControlFlowObfuscation
public class NoFall extends Module {
    public NoFall() {
        super(Categories.Player, "no-fall", "Prevents you from taking fall damage.");
    }

    private final SettingGroup sgDefault = settings.getDefaultGroup();

    public final Setting<Mode> mode = choice(sgDefault, "mode", Mode.Bucket);
    private final Setting<Integer> keepLength = intN(sgDefault, "keep-length", 4, 1, 20,() -> mode.get() != Mode.Vanilla);
    private final Setting<Integer> swapDelay = intN(sgDefault, "swap-delay", 10, 1, 50,() -> mode.get() != Mode.Vanilla);

    public enum Mode {
        Bucket,
        Vanilla
    }

    public int passTicks;
    public boolean place;

    @Override
    public void onActivate() {
        passTicks = 0;
        place = false;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (passTicks > 0) {
            passTicks--;
            return;
        }

        if (mc.player.isCreative() || mc.player.isSpectator()) {
            return;
        }

        switch (mode.get()) {
            case Bucket -> {
                if (mc.player.fallDistance > 2) {
                    var r = InvUtils.findInHotbar(i -> place ? i.getItem().equals(Items.BUCKET) : i .getItem().equals(Items.WATER_BUCKET));

                    if (r.found()) {
                        if (!r.isOffhand()) {
                            hotbar.selectSlot(r.slot(), swapDelay.get());
                            if (passTicks == 0) {
                                passTicks = 1;
                                return;
                            }
                        }

                        var ground = place ? mc.player.getBlockPos() : getGround();

//                        if (!place && mc.player.getBlockPos().getManhattanDistance(ground) > 3) {
//                             return;
//                        }

                        //var box = BlockInfo.box(ground);
                        var set = rotate.rotation(rotM.getRotation(ground.toCenterPos(), mc.player.getCameraPosVec(1))).reach(3).keep(keepLength.get()).set();
                        if (set) {
                            var result = rotM.getRotationOver();
                            if (place) {
                                place = false;
                                if (result instanceof BlockHitResult b) {
                                    var hand = r.isOffhand() ? Hand.OFF_HAND : Hand.MAIN_HAND;
                                    if (mc.interactionManager.interactBlock(mc.player, hand, b).isAccepted()) {
                                        mc.player.swingHand(hand);
                                    }
                                }

                                return;
                            }

                            if (result instanceof BlockHitResult b) {
                                var hand = r.isOffhand() ? Hand.OFF_HAND : Hand.MAIN_HAND;
                                if (mc.interactionManager.interactBlock(mc.player, hand, b).isAccepted()) {
                                    mc.player.swingHand(hand);
                                    place = true;
                                    passTicks = 3;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private BlockPos getGround() {
        var b = mc.player.getBlockPos();

        var state = BlockInfo.getBlockState(b);
        while (state.isAir()) {
            b = b.down();
            state = BlockInfo.getBlockState(b);
        }

        return b.up();
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if(mode.get() != Mode.Vanilla) return;
        if(event.origin == PacketEvent.TransferOrigin.SEND) {
            if (event.packet instanceof PlayerMoveC2SPacket packet) {
                if (mc.player.fallDistance >= 0.1) {
                    packet.onGround = true;
                }
            }
        }
    }
}
