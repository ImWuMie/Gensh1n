package dev.undefinedteam.gensh1n.system;

import dev.undefinedteam.gclient.Formatting;
import dev.undefinedteam.gclient.GCClient;
import dev.undefinedteam.gclient.data.NameColor;
import dev.undefinedteam.gclient.data.UserList;
import dev.undefinedteam.gensh1n.Genshin;
import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.render.Render2DEvent;
import dev.undefinedteam.gensh1n.system.modules.Modules;
import dev.undefinedteam.gensh1n.system.modules.misc.Protocol;
import dev.undefinedteam.gensh1n.system.modules.render.NameProtect;
import dev.undefinedteam.gensh1n.system.modules.render.ServerProtect;
import dev.undefinedteam.gensh1n.utils.heypixel.VIPList;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Pair;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static dev.undefinedteam.gensh1n.Client.mc;
import static dev.undefinedteam.gensh1n.Genshin.*;

@StringEncryption
@ControlFlowObfuscation
@NativeObfuscation
public class TextReplacements {
    public record ReplacementMapping(String originalName, String replacement,
                                     List<Pair<int[], Integer>> colors) {
    }

    private static final List<ReplacementMapping> replacements = new ArrayList<>();

    @EventHandler(priority = -9999)
    private static void onRender(TickEvent.Post e) {
        replacements.clear();

        if (NameProtect.get() != null) {
            ServerProtect serverProtect = Modules.get().get(ServerProtect.class);

            replacements.addAll(NameProtect.get().replacements);
            replacements.add(new TextReplacements.ReplacementMapping("布吉岛", serverProtect.sn.get(), List.of()));

            int gray = Formatting.GRAY.getColor();

            var color = NameProtect.get().color.get();
            if (NameProtect.get().isActive() && NameProtect.get().protectIRC.get()) {
                var session = GCClient.INSTANCE.session();
                if (session != null) {
                    for (UserList.User user : session.users.users) {
                        if (user == null || user.name == null || user.group == null || user.gameName == null || user.gameUUid == null || user.nameColor == null) continue;

                        var prefix = "[" + user.group + "]" + "[" + user.name + "] ";

                        var name = user.gameName;

                        if (!NameProtect.get().showIrcName.get() && mc.player != null && mc.player.getGameProfile() != null && name.equals(mc.player.getGameProfile().getName()))
                            continue;

                        var replacement = prefix + NameProtect.get().replacement.get();

                        var nameStart = prefix.length();
                        replacements.removeIf(m -> m.originalName.equals(name));
                        replacements.add(new TextReplacements.ReplacementMapping(
                            name,
                            replacement,
                            List.of(
                                // [
                                new Pair<>(new int[]{0, 0}, gray),
                                // ***
                                new Pair<>(new int[]{1, user.group.length()}, NameColor.fromString(user.nameColor).mHex),
                                // ][
                                new Pair<>(new int[]{user.group.length() + 1, user.group.length() + 2}, gray),
                                // ***
                                new Pair<>(new int[]{user.group.length() + 2, 2 + user.group.length() + user.name.length()}, Formatting.AQUA.getColor()),
                                // ]
                                new Pair<>(new int[]{prefix.length() - 2, prefix.length() - 1}, gray),
                                new Pair<>(new int[]{nameStart, replacement.length()}, color.getPacked())
                            )
                        ));
                    }
                }
            } else {
                var session = GCClient.INSTANCE.session();
                if (session != null) {
                    for (UserList.User user : session.users.users) {
                        if (user == null || user.name == null || user.group == null || user.gameName == null || user.gameUUid == null || user.nameColor == null) continue;


                        var name = user.gameName;

                        if (!NameProtect.get().showIrcName.get() && mc.player != null && mc.player.getGameProfile() != null && name.equals(mc.player.getGameProfile().getName()))
                            continue;

                        var prefix = "[" + user.group + "]" + "[" + user.name + "] ";
                        var replacement = prefix + name;
                        replacements.removeIf(m -> m.originalName.equals(name));

                        replacements.add(new TextReplacements.ReplacementMapping(
                            name,
                            replacement,
                            List.of(
                                // [
                                new Pair<>(new int[]{0, 0}, gray),
                                // ***
                                new Pair<>(new int[]{1, user.group.length()}, NameColor.fromString(user.nameColor).mHex),
                                // ][
                                new Pair<>(new int[]{user.group.length() + 1, user.group.length() + 2}, gray),
                                // ***
                                new Pair<>(new int[]{user.group.length() + 2, 2 + user.group.length() + user.name.length()}, Formatting.AQUA.getColor()),
                                // ]
                                new Pair<>(new int[]{prefix.length() - 2, prefix.length() - 1}, gray)
                            )
                        ));
                    }
                }
            }

            var protocol = Modules.get().get(Protocol.class);
            if (protocol.isActive() && protocol.isHeypixel()) {
                VIPList.vips.forEach((o, r) -> {
                    replacements.add(new TextReplacements.ReplacementMapping(
                        o,
                        r,
                        List.of(
                            // [
                            new Pair<>(new int[]{0, 0}, gray),
                            // VIP*
                            new Pair<>(new int[]{1, r.length() - 2}, Formatting.GREEN.getColor()),
                            // ]
                            new Pair<>(new int[]{r.length() - 1, r.length()}, gray)
                        )
                    ));
                });
            }

            replacements.sort(Comparator.comparingInt(a -> a.originalName.length()));
        }
    }

    public static boolean shouldReplace() {
        return true;
    }

    public static String replace(String original) {
        if (!shouldReplace()) return original;

        StringBuilder output = new StringBuilder();
        char[] chars = original.toCharArray();
        boolean wasParagraph = false;
        int index = 0;

        while (index < chars.length) {
            char c = chars[index++];

            if (c == '§') {
                wasParagraph = true;
                output.append(c);
                continue;
            }
            if (wasParagraph) {
                wasParagraph = false;
                output.append(c);
                continue;
            }

            boolean found = false;

            for (ReplacementMapping replacement : replacements) {
                int commonLength = getCommonLength(replacement, chars, index - 1);

                if (commonLength != -1) {
                    index += commonLength - 1;
                    output.append(replacement.replacement);
                    found = true;
                    break;
                }
            }

            if (!found) output.append(c);
        }

        return output.toString();
    }

    private static int getCommonLength(ReplacementMapping replacement, char[] chars, int index) {
        char[] replacementChars = replacement.originalName.toCharArray();
        int charsIndex = index;
        int replacementCharsIndex = 0;
        boolean wasParagraph = false;

        while (replacementCharsIndex < replacementChars.length) {
            if (charsIndex > chars.length - 1) {
                return -1;
            }

            char c = chars[charsIndex++];

            if (c == '§') {
                wasParagraph = true;
                continue;
            }
            if (wasParagraph) {
                wasParagraph = false;
                continue;
            }

            if (c != replacementChars[replacementCharsIndex++]) return -1;
        }

        return charsIndex - index;
    }

    public static class ReplacementOrderedText implements OrderedText {
        private final List<MappedCharacter> mappedCharacters;

        public ReplacementOrderedText(OrderedText original) {
            this.mappedCharacters = new ArrayList<>();

            List<MappedCharacter> originalCharacters = new ArrayList<>();

            original.accept((__, style, codePoint) -> {
                originalCharacters.add(new MappedCharacter(
                    style,
                    false,
                    codePoint
                ));

                return true;
            });

            int index = 0;

            while (index < originalCharacters.size()) {
                MappedCharacter originalChar = originalCharacters.get(index);

                if (!originalChar.bypassesNameProtection) {
                    Integer replacementLen = tryReplaceNames(index, originalCharacters, originalChar);

                    if (replacementLen != null) {
                        index += replacementLen;
                        continue;
                    }
                }

                this.mappedCharacters.add(originalChar);
                index++;
            }
        }

        private Integer tryReplaceNames(int index, List<MappedCharacter> originalCharacters, MappedCharacter originalChar) {
            for (ReplacementMapping replacement : replacements) {
                if (replacement.originalName.isEmpty()) {
                    continue;
                }

                boolean canReplace = true;

                for (int replacementIdx = 0; replacementIdx < replacement.originalName.length(); replacementIdx++) {
                    int origIndex = index + replacementIdx;

                    if (originalCharacters.size() <= origIndex || originalCharacters.get(origIndex).codePoint != replacement.originalName.charAt(replacementIdx)) {
                        canReplace = false;
                        break;
                    }
                }

                if (canReplace) {
                    int charIndex = 0;
                    for (char c : replacement.replacement.toCharArray()) {
                        var color = getTextColor(charIndex, replacement.colors);
                        this.mappedCharacters.add(new MappedCharacter(
                            color != null ? originalChar.style.withColor(color) : originalChar.style
                            , false, c
                        ));

                        charIndex++;
                    }

                    return replacement.originalName.length();
                }
            }

            return null;
        }

        private TextColor getTextColor(int index, List<Pair<int[], Integer>> map) {
            for (Pair<int[], Integer> pair : map) {
                var range = pair.getLeft();
                var color = pair.getRight();

                var min = range[0];
                var max = range[1];
                if (index >= min && index <= max) {
                    return TextColor.fromRgb(color);
                }
            }

            return null;
        }

        public boolean accept(CharacterVisitor visitor) {
            int index = 0;

            for (MappedCharacter character : this.mappedCharacters) {
                if (!visitor.accept(index, character.style, character.codePoint)) {
                    return false;
                }

                index++;
            }

            return true;
        }

        public record MappedCharacter(Style style, boolean bypassesNameProtection, int codePoint) {
        }
    }
}
