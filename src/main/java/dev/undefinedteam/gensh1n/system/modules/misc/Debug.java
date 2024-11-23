package dev.undefinedteam.gensh1n.system.modules.misc;

import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@StringEncryption
@ControlFlowObfuscation
public class Debug extends Module {
    public Debug() {
        super(Categories.Misc, "debug", "Shows debug(player) information.");
    }

    private boolean allowF = false;
    private boolean fly = false,invulnerable = false,creativeMode = false,allowModifyWorld = false;

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (Utils.canUpdate()) {
            var abilities = mc.player.getAbilities();

            if (abilities.allowFlying != this.allowF) {
                this.allowF = abilities.allowFlying;
                info("Abilities.allowFlying: " + this.allowF);
            }

            if (abilities.flying != this.fly) {
                this.fly = abilities.flying;
                info("Abilities.flying: " + this.fly);
            }

            if (abilities.invulnerable != this.invulnerable) {
                this.invulnerable = abilities.invulnerable;
                info("Abilities.invulnerable: " + this.invulnerable);
            }
            if (abilities.creativeMode != this.creativeMode) {
                this.creativeMode = abilities.creativeMode;
                info("Abilities.creativeMode: " + this.creativeMode);
            }

            if (abilities.allowModifyWorld != this.allowModifyWorld) {
                this.allowModifyWorld = abilities.allowModifyWorld;
                info("Abilities.allowModifyWorld: " + this.allowModifyWorld);
            }
        }
    }
}
