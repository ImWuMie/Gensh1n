package dev.undefinedteam.gensh1n.system.modules.player;

import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.mixin_interface.IMinecraft;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.hit.HitResult;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@StringEncryption
@ControlFlowObfuscation
public class FastClick extends Module {
    public FastClick() {
        super(Categories.Player, "fast-click", "Allows you to click faster");
    }

    private final SettingGroup sgDefault = settings.getDefaultGroup();
    private final Setting<Boolean> left = bool(sgDefault, "left", true);
    private final Setting<Integer> left_cps = intN(sgDefault, "left-pts", 16, 1, 256, left::get);
    private final Setting<Boolean> right = bool(sgDefault, "right", true);
    private final Setting<Integer> right_cps = intN(sgDefault, "right-pts", 16, 1, 256, right::get);
    private final Setting<Boolean> onlyBlock = bool(sgDefault, "only-block", false);

    @Override
    public void onActivate() {
        mc.options.useKey.setPressed(false);
        mc.options.attackKey.setPressed(false);
    }

    @EventHandler
    private void onTick(TickEvent.Pre e) {
        if (onlyBlock.get()) {
            if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                if (left.get() && mc.options.attackKey.isPressed()) {
                    for (int i = 0; i < left_cps.get(); ++i) {
                        ((IMinecraft) mc).genshin$setAttackCooldown(0);
                        ((IMinecraft) mc).genshin$leftClick();
                    }
                }

                if (right.get() && mc.options.useKey.isPressed()) {
                    for (int i = 0; i < right_cps.get(); ++i) {
                        ((IMinecraft) mc).genshin$setItemUseCooldown(0);
                        ((IMinecraft) mc).genshin$rightClick();
                    }
                }
            }
        } else {
            if (left.get() && mc.options.attackKey.isPressed()) {
                for (int i = 0; i < left_cps.get(); ++i) {
                    ((IMinecraft) mc).genshin$setAttackCooldown(0);
                    ((IMinecraft) mc).genshin$leftClick();
                }
            }

            if (right.get() && mc.options.useKey.isPressed()) {
                for (int i = 0; i < right_cps.get(); ++i) {
                    ((IMinecraft) mc).genshin$setItemUseCooldown(0);
                    ((IMinecraft) mc).genshin$rightClick();
                }
            }
        }
    }
}
