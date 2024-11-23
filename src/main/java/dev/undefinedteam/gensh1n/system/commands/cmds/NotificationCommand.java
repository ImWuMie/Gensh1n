package dev.undefinedteam.gensh1n.system.commands.cmds;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.undefinedteam.gensh1n.gui.overlay.TNotifications;
import dev.undefinedteam.gensh1n.system.commands.Command;
import net.minecraft.command.CommandSource;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

@StringEncryption
@ControlFlowObfuscation
public class NotificationCommand extends Command {
    public NotificationCommand() {
        super("noti", "", "n");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("a").then(argument("info", StringArgumentType.greedyString()).executes(context -> {
            var text = StringArgumentType.getString(context, "info");
            TNotifications.INSTANCE.push(text, TNotifications.Type.INFO, 3000);
            return SUCCESS;
        })));

        builder.then(literal("b").then(argument("warn", StringArgumentType.greedyString()).executes(context -> {
            var text = StringArgumentType.getString(context, "warn");
            TNotifications.INSTANCE.push(text, TNotifications.Type.WARN, 3000);
            return SUCCESS;
        })));
        builder.then(literal("c").then(argument("error", StringArgumentType.greedyString()).executes(context -> {
            var text = StringArgumentType.getString(context, "error");
            TNotifications.INSTANCE.push(text, TNotifications.Type.ERROR, 3000);
            return SUCCESS;
        })));
        builder.then(literal("d").then(argument("idk", StringArgumentType.greedyString()).executes(context -> {
            var text = StringArgumentType.getString(context, "idk");
            TNotifications.INSTANCE.push(text, TNotifications.Type.IDK, 3000);
            return SUCCESS;
        })));

    }
}
