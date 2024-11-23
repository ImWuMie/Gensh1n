package dev.undefinedteam.gensh1n.system.commands.cmds;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.undefinedteam.gensh1n.system.commands.Command;
import net.minecraft.command.CommandSource;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static dev.undefinedteam.gensh1n.Client.mc;

@StringEncryption
@ControlFlowObfuscation
public class SetVelocityCommand extends Command {
    public SetVelocityCommand() {
        super("set-velocity", "Sets player velocity", "velocity", "vel");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("y", DoubleArgumentType.doubleArg()).executes(ctx -> {
            var entity = mc.player.hasVehicle() ? mc.player.getVehicle() : mc.player;
            var currentVelocity = entity.getVelocity();
            entity.setVelocity(currentVelocity.x, DoubleArgumentType.getDouble(ctx, "y"), currentVelocity.z);
            return SINGLE_SUCCESS;
        }));

        builder.then(argument("x", DoubleArgumentType.doubleArg()).then(argument("z", DoubleArgumentType.doubleArg()).executes(ctx -> {
            double x = DoubleArgumentType.getDouble(ctx, "x");
            double z = DoubleArgumentType.getDouble(ctx, "z");
            var entity = mc.player.hasVehicle() ? mc.player.getVehicle() : mc.player;
            entity.setVelocity(x, entity.getVelocity().y, z);
            return SINGLE_SUCCESS;
        })));

        builder.then(argument("x", DoubleArgumentType.doubleArg()).then(argument("y", DoubleArgumentType.doubleArg()).then(argument("z", DoubleArgumentType.doubleArg()).executes(ctx -> {
            double x = DoubleArgumentType.getDouble(ctx, "x");
            double y = DoubleArgumentType.getDouble(ctx, "y");
            double z = DoubleArgumentType.getDouble(ctx, "z");
            var entity = mc.player.hasVehicle() ? mc.player.getVehicle() : mc.player;
            entity.setVelocity(x, y, z);
            return SINGLE_SUCCESS;
        }))));
    }
}
