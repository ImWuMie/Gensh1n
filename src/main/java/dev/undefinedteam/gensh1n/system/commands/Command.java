package dev.undefinedteam.gensh1n.system.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dev.undefinedteam.gensh1n.settings.Settings;
import dev.undefinedteam.gensh1n.system.ChatAdapter;
import dev.undefinedteam.gensh1n.system.SettingAdapter;
import dev.undefinedteam.gensh1n.utils.Utils;
import dev.undefinedteam.gensh1n.utils.misc.ISerializable;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.server.command.CommandManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class Command extends ChatAdapter implements ISerializable<Command>, Comparable<Command>, SettingAdapter {
    protected static final CommandRegistryAccess REGISTRY_ACCESS = CommandManager.createRegistryAccess(BuiltinRegistries.createWrapperLookup());

    public final String name;
    public final String description;
    public final List<String> aliases = new ArrayList<>();

    public final Settings settings = new Settings();

    protected static final int SUCCESS = com.mojang.brigadier.Command.SINGLE_SUCCESS;

    public Command(String name, String description, String... aliases) {
        super(Utils.nameToTitle(name));
        this.name = name;
        this.description = description;
        Collections.addAll(this.aliases, aliases);
    }

    protected static <T> RequiredArgumentBuilder<CommandSource, T> argument(final String name, final ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    protected static LiteralArgumentBuilder<CommandSource> literal(final String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public final void registerTo(CommandDispatcher<CommandSource> dispatcher) {
        register(dispatcher, name);
        for (String alias : aliases) register(dispatcher, alias);
    }

    public void register(CommandDispatcher<CommandSource> dispatcher, String name) {
        LiteralArgumentBuilder<CommandSource> builder = LiteralArgumentBuilder.literal(name);
        build(builder);
        dispatcher.register(builder);
    }

    public abstract void build(LiteralArgumentBuilder<CommandSource> builder);

    @Override
    public JsonObject toTag() {
        return settings.toTag();
    }


    @Override
    public Command fromTag(JsonObject tag) {
        settings.fromTag(tag.getAsJsonObject("settings"));
        return this;
    }

    @Override
    public int compareTo(@NotNull Command o) {
        return name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command module = (Command) o;
        return Objects.equals(name, module.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
