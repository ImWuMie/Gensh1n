package dev.undefinedteam.gensh1n.system.commands.cmds;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.undefinedteam.gensh1n.system.commands.Command;
import dev.undefinedteam.gensh1n.system.commands.args.ConfigsArgumentType;
import dev.undefinedteam.gensh1n.system.config.Config;
import dev.undefinedteam.gensh1n.system.config.Configs;
import net.minecraft.command.CommandSource;

public class ConfigCommand extends Command {
    public ConfigCommand() {
        super("config", "load save configs", "cfg");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("load").then(argument("config", ConfigsArgumentType.create()).executes(context -> {
            var cfg = ConfigsArgumentType.get(context);
            cfg.load();
            info("Loaded config '" + cfg.name + "'");
            return SUCCESS;
        })));

        builder.then(literal("save").then(argument("name", StringArgumentType.string()).executes(context -> {
            var name = StringArgumentType.getString(context,"name");
            Configs.get().add(new Config(name));
            info("Saved config '" + name + "'");
            return SUCCESS;
        })));
    }
}
