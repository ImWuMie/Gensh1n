package dev.undefinedteam.gensh1n.system.commands.args;

import com.google.common.collect.Streams;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.undefinedteam.gensh1n.system.friend.Friend;
import dev.undefinedteam.gensh1n.system.friend.Friends;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.command.CommandSource.suggestMatching;

public class FriendArgumentType implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = List.of("Fin_LemonKee", "ImWuMie");

    public static FriendArgumentType create() {
        return new FriendArgumentType();
    }

    public static Friend get(CommandContext<?> context) {
        return Friends.get().get(context.getArgument("friend", String.class));
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return suggestMatching(Streams.stream(Friends.get()).map(Friend::getName), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
