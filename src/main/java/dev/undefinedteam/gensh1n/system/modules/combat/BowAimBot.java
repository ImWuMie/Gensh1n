package dev.undefinedteam.gensh1n.system.modules.combat;

import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Category;
import dev.undefinedteam.gensh1n.system.modules.Module;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@StringEncryption
@ControlFlowObfuscation
public class BowAimBot extends Module {
    public BowAimBot() {
        super(Categories.Combat,"bot-aim-bot","Aim Players");
    }

}
