package dev.undefinedteam.gensh1n.system.modules.movement;

import dev.undefinedteam.gensh1n.events.player.RotationApplyEvent;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import meteordevelopment.orbit.EventHandler;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@StringEncryption
@ControlFlowObfuscation
public class MoveFix extends Module {
    public MoveFix() {
        super(Categories.Movement, "move-fix", "Fix the rotation movement");
    }

    @EventHandler
    private void onApply(RotationApplyEvent e) {
        e.rotation.movefix();
    }
}
