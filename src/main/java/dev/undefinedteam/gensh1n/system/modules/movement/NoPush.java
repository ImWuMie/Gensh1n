package dev.undefinedteam.gensh1n.system.modules.movement;

import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Category;
import dev.undefinedteam.gensh1n.system.modules.Module;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@StringEncryption
@ControlFlowObfuscation
public class NoPush extends Module {
    public NoPush() {
        super(Categories.Movement, "no-push", "Prevents you from being pushed by other players.");
    }
}
