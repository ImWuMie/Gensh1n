package dev.undefinedteam.gensh1n.system.commands.args;


import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.undefinedteam.gensh1n.system.config.Config;
import dev.undefinedteam.gensh1n.system.config.Configs;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ConfigsArgumentType implements ArgumentType<Config> {
    private static final ConfigsArgumentType INSTANCE = new ConfigsArgumentType();
    private static final DynamicCommandExceptionType NO_SUCH_MODULE = new DynamicCommandExceptionType(name -> Text.literal("Config with name " + name + " doesn't exist."));

    private static final Collection<String> EXAMPLES = Configs.get().getAll()
        .stream()
        .limit(3)
        .map(c -> c.name)
        .collect(Collectors.toList());

    public static ConfigsArgumentType create() {
        return INSTANCE;
    }

    public static Config get(CommandContext<?> context) {
        return context.getArgument("config", Config.class);
    }

    private ConfigsArgumentType() {}

    @Override
    public Config parse(StringReader reader) throws CommandSyntaxException {
        String argument = reader.readString();
        Config module = Configs.get().get(argument);
        if (module == null) throw NO_SUCH_MODULE.create(argument);

        return module;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(Configs.get().getAll().stream().map(c -> c.name), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

