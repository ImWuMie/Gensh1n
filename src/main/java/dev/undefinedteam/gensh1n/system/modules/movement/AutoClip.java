package dev.undefinedteam.gensh1n.system.modules.movement;

import dev.undefinedteam.gensh1n.events.player.PlayerTickEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.TransparentBlock;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
public class AutoClip extends Module {
    public AutoClip() {
        super(Categories.Movement, "auto-clip", "Automatically clips through blocks.");
    }

    private final SettingGroup sGroup = settings.getDefaultGroup();

    private final Setting<Boolean> checkBlocks = bool(sGroup, "check-blocks", true);
    private final Setting<Integer> clipBlocks = intN(sGroup, "clip-blocks", 7, 0, 10);
    private final Setting<Integer> repeatCount = intN(sGroup, "repeat-count", 5, 1, 20);
    private final Setting<Integer> maxTicks = intN(sGroup, "max-ticks", 60, 1, 200);

    private final Setting<Boolean> updatePos = bool(sGroup, "update-pos", true);

    private boolean fly = false;

    @Override
    public void onActivate() {
        this.fly = false;
    }

    private boolean waiting = false;
    private int ticks = 0;

    @EventHandler
    private void eventPlayerTick(PlayerTickEvent e) {
        if (waiting) {
            ticks++;

            var hasGlass = false;
            var bpos = mc.player.getBlockPos();
            for (int i = 0; i < 4; i++) {
                bpos = bpos.up();
                if (mc.world.getBlockState(bpos).getBlock() instanceof TransparentBlock) {
                    hasGlass = true;
                }
            }

            if (hasGlass || ticks >= this.maxTicks.get()) {
                waiting = false;
                ticks = 0;
                nWarn("妖猫拒绝了你的出笼申请.", NSHORT);
            } else {
                waiting = false;
                ticks = 0;
                nInfo("妖猫接受了你的出笼申请.", NSHORT);
            }
        }

        if (mc.player.getAbilities().flying != this.fly) {
            this.fly = mc.player.getAbilities().flying;
            var hasGlass = !checkBlocks.get();

            if (!hasGlass) {
                var bpos = mc.player.getBlockPos();
                for (int i = 0; i < 5; i++) {
                    bpos = bpos.up();
                    if (mc.world.getBlockState(bpos).getBlock() instanceof TransparentBlock) {
                        hasGlass = true;
                    }
                }
            }

            if (hasGlass && mc.player.getAbilities().flying) {
                double currentY = mc.player.getY();
                double x = mc.player.getX();
                double z = mc.player.getZ();
                for (int i = 0; i < repeatCount.get(); i++) {
                    currentY += clipBlocks.get();
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, currentY, z, false));
                }

                if (updatePos.get())
                    mc.player.updatePosition(x, currentY, z);

                waiting = true;
                ticks = 0;
            }
        }
    }

}
