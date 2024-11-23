package dev.undefinedteam.gensh1n.system.commands.cmds;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.system.commands.Command;
import dev.undefinedteam.gensh1n.system.commands.args.ModuleArgumentType;
import dev.undefinedteam.gensh1n.system.commands.args.SettingArgumentType;
import dev.undefinedteam.gensh1n.system.commands.args.SettingValueArgumentType;
import net.minecraft.command.CommandSource;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@StringEncryption
@ControlFlowObfuscation
public class SettingCommand extends Command {
    public SettingCommand() {
        super("settings", "Allows you to view and change module settings.", "s");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        // View or change settings
        builder.then(
                argument("module", ModuleArgumentType.create())
                .then(
                        argument("setting", SettingArgumentType.create())
                        .executes(context -> {
                            // Get setting value
                            Setting<?> setting = SettingArgumentType.get(context);

                            ModuleArgumentType.get(context).info("Setting (highlight)%s(default) is (highlight)%s(default).", setting.title, setting.get());

                            return SUCCESS;
                        })
                        .then(
                                argument("value", SettingValueArgumentType.create())
                                .executes(context -> {
                                    // Set setting value
                                    Setting<?> setting = SettingArgumentType.get(context);
                                    String value = SettingValueArgumentType.get(context);

                                    if (setting.parse(value)) {
                                        ModuleArgumentType.get(context).info("Setting (highlight)%s(default) changed to (highlight)%s(default).", setting.title, value);
                                    }

                                    return SUCCESS;
                                })
                        )
                )
        );
    }
}
