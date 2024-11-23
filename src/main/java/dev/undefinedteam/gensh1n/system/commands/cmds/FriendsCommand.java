package dev.undefinedteam.gensh1n.system.commands.cmds;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.undefinedteam.gensh1n.system.commands.Command;
import dev.undefinedteam.gensh1n.system.commands.args.FriendArgumentType;
import dev.undefinedteam.gensh1n.system.commands.args.PlayerListEntryArgumentType;
import dev.undefinedteam.gensh1n.system.friend.Friend;
import dev.undefinedteam.gensh1n.system.friend.Friends;
import dev.undefinedteam.gensh1n.utils.chat.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

@StringEncryption
@ControlFlowObfuscation
public class FriendsCommand extends Command {
    public FriendsCommand() {
        super("friends", "Manages friends.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("add")
            .then(argument("player", PlayerListEntryArgumentType.create())
                .executes(context -> {
                    GameProfile profile = PlayerListEntryArgumentType.get(context).getProfile();
                    Friend friend = new Friend(profile.getName(), profile.getId());

                    if (Friends.get().add(friend)) {
                        ChatUtils.sendMsg(friend.hashCode(), Formatting.GRAY, "Added (highlight)%s (default)to friends.".formatted(friend.getName()));
                    } else error("Already friends with that player.");

                    return SINGLE_SUCCESS;
                })
            )
        );

        builder.then(literal("remove")
            .then(argument("friend", FriendArgumentType.create())
                .executes(context -> {
                    Friend friend = FriendArgumentType.get(context);
                    if (friend == null) {
                        error("Not friends with that player.");
                        return SINGLE_SUCCESS;
                    }

                    if (Friends.get().remove(friend)) {
                        ChatUtils.sendMsg(friend.hashCode(), Formatting.GRAY, "Removed (highlight)%s (default)from friends.".formatted(friend.getName()));
                    } else error("Failed to remove that friend.");

                    return SINGLE_SUCCESS;
                })
            )
        );

        builder.then(literal("list").executes(context -> {
                info("--- Friends ((highlight)%s(default)) ---", Friends.get().count());
                Friends.get().forEach(friend -> ChatUtils.info("(highlight)%s".formatted(friend.getName())));
                return SINGLE_SUCCESS;
            })
        );
    }
}
