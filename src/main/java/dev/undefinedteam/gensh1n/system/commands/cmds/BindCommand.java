package dev.undefinedteam.gensh1n.system.commands.cmds;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.undefinedteam.gensh1n.system.commands.Command;
import dev.undefinedteam.gensh1n.system.commands.args.ModuleArgumentType;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.utils.input.Keybind;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.CommandSource;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.Locale;

@StringEncryption
@ControlFlowObfuscation
public class BindCommand extends Command {
    public BindCommand() {
        super("bind", "Bound a module to a key");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("module", ModuleArgumentType.create()).executes(context -> {
            Module module = context.getArgument("module", Module.class);
            Modules.get().setModuleToBind(module);
            module.info("Press a key to bind the module to.");
            return SUCCESS;
        }));

        builder.then(argument("module", ModuleArgumentType.create()).then(argument("bind", StringArgumentType.string()).executes(context -> {
            Module module = context.getArgument("module", Module.class);
            String bind = context.getArgument("bind", String.class);
            if (bind.equalsIgnoreCase("none")) {
                module.keybind.set(true, -1);
            }
            module.keybind.set(true, InputUtil.fromTranslationKey("key.keyboard." + bind.toLowerCase(Locale.ROOT)).getCode());
            module.info("Bound module '" + module.title + "' to " + bind.toUpperCase());
            return SUCCESS;
        })));
    }
}
