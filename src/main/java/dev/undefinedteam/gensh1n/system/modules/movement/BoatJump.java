package dev.undefinedteam.gensh1n.system.modules.movement;

import dev.undefinedteam.gensh1n.events.player.PlayerTickEvent;
import dev.undefinedteam.gensh1n.settings.DoubleSetting;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.vehicle.BoatEntity;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
@StringEncryption
@ControlFlowObfuscation
public class BoatJump extends Module {
    public BoatJump() {
        super(Categories.Movement, "boat-jump", "Long jump on boats");
    }

    private final SettingGroup sgDefault = settings.getDefaultGroup();

    private final Setting<Mode> mode = choice(sgDefault, "mode", Mode.Horizontal);
    private final DoubleSetting hMotion = doubleN(sgDefault, "h-motion", 1.2, 0.01, 10);
    private final DoubleSetting vMotion = doubleN(sgDefault, "v-motion", 0.18, 0.01, 10);

    public enum Mode {
        Vertical, Horizontal, Both
    }

    @EventHandler
    private void onPlayerTick(PlayerTickEvent e) {
        var nearBoat = false;
        var box = mc.player.getBoundingBox().expand(0.5, 1, 0.5);

        for (var entity : mc.world.getEntities()) {
            if (entity instanceof BoatEntity && box.intersects(entity.getBoundingBox())) {
                nearBoat = true;
            }
        }

        if (nearBoat && mc.player.input.jumping) {
            var v = mode.get().equals(Mode.Vertical) || mode.get().equals(Mode.Both);
            var h = mode.get().equals(Mode.Horizontal) || mode.get().equals(Mode.Both);

            var yaw = Math.toRadians(mc.player.getYaw());
            mc.player.addVelocity(h ? -sin(yaw) * hMotion.get() : 0, v ? vMotion.get() : 0, h ? cos(yaw) * hMotion.get() : 0);
        }
    }
}
