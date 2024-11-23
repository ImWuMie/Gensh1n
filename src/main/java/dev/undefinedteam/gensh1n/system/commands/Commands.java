package dev.undefinedteam.gensh1n.system.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.System;
import dev.undefinedteam.gensh1n.system.Systems;
import dev.undefinedteam.gensh1n.system.commands.cmds.*;
import dev.undefinedteam.gensh1n.utils.ResLocation;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

import static dev.undefinedteam.gensh1n.Client.mc;

public class Commands extends System<Commands> {
    public static final CommandRegistry REGISTRY = new CommandRegistry();
    public final CommandDispatcher<CommandSource> DISPATCHER = new CommandDispatcher<>();
    public final CommandSource COMMAND_SOURCE = new ClientCommandSource(null, mc);
    public final List<Command> COMMANDS = new ArrayList<>();

    public Commands() {
        super("commands");
    }

    public static Commands get() {
        return Systems.get(Commands.class);
    }

    @Override
    public void init() {
        add(new BindCommand());
        add(new SettingCommand());
        add(new NotificationCommand());
        add(new FriendsCommand());
        add(new InfTpCommand());
        add(new SetVelocityCommand());
        add(new TppCommand());
        add(new CenterCommand());
        add(new ConfigCommand());

        // 主播太esu了
        add(new rip());

        COMMANDS.sort(Command::compareTo);
    }

    @Override
    public void load(File folder) {
        for (Command cmd : COMMANDS) {
            for (SettingGroup group : cmd.settings) {
                for (Setting<?> setting : group) setting.reset();
            }
        }

        super.load(folder);
    }

    public void add(Command command) {
        COMMANDS.removeIf(existing -> existing.name.equals(command.name));
        command.registerTo(DISPATCHER);
        COMMANDS.add(command);
    }

    public void dispatch(String message) throws CommandSyntaxException {
        DISPATCHER.execute(message, COMMAND_SOURCE);
    }

    public Command get(String name) {
        for (Command command : COMMANDS) {
            if (command.name.equals(name)) {
                return command;
            }
        }

        return null;
    }

    @Override
    public JsonObject toTag() {
        JsonObject tag = new JsonObject();

        JsonArray commandsTag = new JsonArray();
        for (Command cmd : COMMANDS) {
            JsonObject commandTag = new JsonObject();
            JsonObject cmdTagI = cmd.toTag();
            commandTag.addProperty("name", cmd.name);
            if (cmdTagI != null) commandTag.add("settings", cmdTagI);
        }
        tag.add("commands", commandsTag);

        return tag;
    }

    @Override
    public Commands fromTag(JsonObject tag) {
        JsonArray commandsTag = tag.getAsJsonArray("commands");
        for (JsonElement cmdTagI : commandsTag) {
            JsonObject cmdTag = cmdTagI.getAsJsonObject();
            if (cmdTag.has("name")) {
                Command cmd = get(cmdTag.get("name").getAsString());
                if (cmd != null) cmd.fromTag(cmdTag);
            }
        }

        return this;
    }

    public static class CommandRegistry extends SimpleRegistry<Command> {
        public CommandRegistry() {
            super(RegistryKey.ofRegistry(ResLocation.of("commands")), Lifecycle.stable());
        }


        @Override
        public int size() {
            return Commands.get().COMMANDS.size();
        }

        @Override
        public Identifier getId(Command entry) {
            return null;
        }

        @Override
        public Optional<RegistryKey<Command>> getKey(Command entry) {
            return Optional.empty();
        }

        @Override
        public int getRawId(Command entry) {
            return 0;
        }

        @Override
        public Command get(RegistryKey<Command> key) {
            return null;
        }

        @Override
        public Command get(Identifier id) {
            return null;
        }

        @Override
        public Lifecycle getLifecycle() {
            return null;
        }

        @Override
        public Set<Identifier> getIds() {
            return null;
        }
        @Override
        public boolean containsId(Identifier id) {
            return false;
        }

        @Nullable
        @Override
        public Command get(int index) {
            return null;
        }

        @Override
        public @NotNull Iterator<Command> iterator() {
            return new CommandIterator();
        }

        @Override
        public boolean contains(RegistryKey<Command> key) {
            return false;
        }

        @Override
        public Set<Map.Entry<RegistryKey<Command>, Command>> getEntrySet() {
            return null;
        }

        @Override
        public Set<RegistryKey<Command>> getKeys() {
            return null;
        }

        @Override
        public Optional<RegistryEntry.Reference<Command>> getRandom(Random random) {
            return Optional.empty();
        }

        @Override
        public Registry<Command> freeze() {
            return null;
        }

        @Override
        public RegistryEntry.Reference<Command> createEntry(Command value) {
            return null;
        }

        @Override
        public Optional<RegistryEntry.Reference<Command>> getEntry(int rawId) {
            return Optional.empty();
        }

        @Override
        public Optional<RegistryEntry.Reference<Command>> getEntry(RegistryKey<Command> key) {
            return Optional.empty();
        }

        @Override
        public Stream<RegistryEntry.Reference<Command>> streamEntries() {
            return null;
        }

        @Override
        public Optional<RegistryEntryList.Named<Command>> getEntryList(TagKey<Command> tag) {
            return Optional.empty();
        }

        @Override
        public RegistryEntryList.Named<Command> getOrCreateEntryList(TagKey<Command> tag) {
            return null;
        }

        @Override
        public Stream<Pair<TagKey<Command>, RegistryEntryList.Named<Command>>> streamTagsAndEntries() {
            return null;
        }

        @Override
        public Stream<TagKey<Command>> streamTags() {
            return null;
        }

        @Override
        public void clearTags() {}

        @Override
        public void populateTags(Map<TagKey<Command>, List<RegistryEntry<Command>>> tagEntries) {}

        private static class CommandIterator implements Iterator<Command> {
            private final Iterator<Command> iterator = Commands.get().COMMANDS.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Command next() {
                return iterator.next();
            }
        }
    }
}
