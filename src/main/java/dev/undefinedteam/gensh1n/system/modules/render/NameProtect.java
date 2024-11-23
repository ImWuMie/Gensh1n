package dev.undefinedteam.gensh1n.system.modules.render;

import dev.undefinedteam.gclient.GCClient;
import dev.undefinedteam.gclient.data.UserList;
import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.render.Render2DEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.TextReplacements;
import dev.undefinedteam.gensh1n.system.friend.Friend;
import dev.undefinedteam.gensh1n.system.friend.Friends;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Category;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import dev.undefinedteam.gensh1n.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.ArrayList;
import java.util.List;

@StringEncryption
@ControlFlowObfuscation
public class NameProtect extends Module {
    public NameProtect() {
        super(Categories.Render, "name-protect", "Hides the player's real username client-side");
    }

    private final SettingGroup sGroup = settings.getDefaultGroup();
    public final Setting<String> replacement = text(sGroup, "replacement", "Hidden");

    public final Setting<SettingColor> color = color(sGroup, "color", new SettingColor(Formatting.AQUA.getColorValue()));
    public final Setting<Boolean> protectIRC = bool(sGroup, "protect-irc", true);
    public final Setting<Boolean> showIrcName = bool(sGroup, "show-own-irc-name", true);
    private final Setting<Boolean> protectFriend = bool(sGroup, "protect-friend", true);

    public List<TextReplacements.ReplacementMapping> replacements = new ArrayList<>();

    @EventHandler
    private void onRender(TickEvent.Post e) {
        replacements.clear();

        replacements.add(new TextReplacements.ReplacementMapping(
            mc.player.getGameProfile().getName(),
            replacement.get(),
            List.of(new Pair<>(new int[]{0, replacement.get().length()}, color.get().getPacked()))
        ));

        if (isActive()) {
            if (protectFriend.get()) {
                for (Friend friend : Friends.get()) {
                    var name = friend.name;
                    var replacement = this.replacement.get();
                    replacements.add(new TextReplacements.ReplacementMapping(
                        name,
                        replacement,
                        List.of(new Pair<>(new int[]{0, replacement.length()}, color.get().getPacked()))
                    ));
                }
            }
        }
    }

    @Override
    public void onDeactivate() {
        replacements.clear();
    }

    public static NameProtect get() {
        return Modules.get().get(NameProtect.class);
    }
}
