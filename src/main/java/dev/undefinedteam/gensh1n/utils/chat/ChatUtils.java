package dev.undefinedteam.gensh1n.utils.chat;

import com.mojang.brigadier.StringReader;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.mixin_interface.IChatHud;
import dev.undefinedteam.gensh1n.system.ClientConfig;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import static dev.undefinedteam.gensh1n.Client.mc;
@StringEncryption
@ControlFlowObfuscation
public class ChatUtils {
    private static String forcedPrefixClassName;

    public static final String PREFIX = Formatting.GOLD + Client.NAME_F + Formatting.BOLD + Formatting.GRAY + ">>";

    public static void forceNextPrefixClass(Class<?> klass) {
        forcedPrefixClassName = klass.getName();
    }

    // Player

    /**
     * Sends the message as if the user typed it into chat.
     */
    public static void sendPlayerMsg(String message) {
        mc.inGameHud.getChatHud().addToMessageHistory(message);

        if (message.startsWith("/")) mc.player.networkHandler.sendChatCommand(message.substring(1));
        else mc.player.networkHandler.sendChatMessage(message);
    }

    // Default

    public static void info(String message, Object... args) {
        sendMsg(Formatting.GRAY, message, args);
    }

    public static void infoPrefix(String prefix, String message, Object... args) {
        sendMsg(0, prefix, Formatting.AQUA, Formatting.GRAY, message, args);
    }

    // Warning

    public static void warning(String message, Object... args) {
        sendMsg(Formatting.YELLOW, message, args);
    }

    public static void warningPrefix(String prefix, String message, Object... args) {
        sendMsg(0, prefix, Formatting.AQUA, Formatting.YELLOW, message, args);
    }

    // Error

    public static void error(String message, Object... args) {
        sendMsg(Formatting.RED, message, args);
    }

    public static void errorPrefix(String prefix, String message, Object... args) {
        sendMsg(0, prefix, Formatting.AQUA, Formatting.RED, message, args);
    }

    // Misc

    public static void sendMsg(Text message) {
        sendMsg(null, message);
    }

    public static void sendMsg(int id,Text message) {
        sendMsg(id, null, Formatting.AQUA, message);
    }

    public static void sendMsg(String prefix, Text message) {
        sendMsg(0, prefix, Formatting.AQUA, message);
    }

    public static void sendMsg(Formatting color, String message, Object... args) {
        sendMsg(0, null, null, color, message, args);
    }

    public static void sendMsg(int id, Formatting color, String message, Object... args) {
        sendMsg(id, null, null, color, message, args);
    }

    public static void sendMsg(int id, @Nullable String prefixTitle, @Nullable Formatting prefixColor, Formatting messageColor, String messageContent, Object... args) {
        MutableText message = formatMsg(String.format(messageContent, args), messageColor);
        sendMsg(id, prefixTitle, prefixColor, message);
    }

    public static void sendMsg(int id, @Nullable String prefixTitle, @Nullable Formatting prefixColor, String messageContent, Formatting messageColor) {
        MutableText message = formatMsg(messageContent, messageColor);
        sendMsg(id, prefixTitle, prefixColor, message);
    }

    public static void sendMsg(int id, @Nullable String prefixTitle, @Nullable Formatting prefixColor, Text msg) {
        if (mc.world == null) return;

        MutableText message = Text.empty();
        message.append(PREFIX + " ");
        if (prefixTitle != null) message.append(getCustomPrefix(prefixTitle, prefixColor));
        message.append(msg);

        if (!ClientConfig.get().deleteChatFeedback.get()) id = 0;

        ((IChatHud) mc.inGameHud.getChatHud()).gensh1n$add(message, id);
    }

    private static MutableText getCustomPrefix(String prefixTitle, Formatting prefixColor) {
        MutableText prefix = Text.empty();
        /*MutableText moduleTitle = Text.literal(prefixTitle);
        moduleTitle.setStyle(moduleTitle.getStyle().withFormatting(prefixColor));
        prefix.append(moduleTitle);

        prefix.append(Formatting.GRAY + " << " + Formatting.RESET);
        */

        return prefix;
    }

    private static MutableText formatMsg(String message, Formatting defaultColor) {
        StringReader reader = new StringReader(message);
        MutableText text = Text.empty();
        Style style = Style.EMPTY.withFormatting(defaultColor);
        StringBuilder result = new StringBuilder();
        boolean formatting = false;
        while (reader.canRead()) {
            char c = reader.read();
            if (c == '(') {
                text.append(Text.literal(result.toString()).setStyle(style));
                result.setLength(0);
                result.append(c);
                formatting = true;
            } else {
                result.append(c);

                if (formatting && c == ')') {
                    switch (result.toString()) {
                        case "(default)" -> {
                            style = style.withFormatting(defaultColor);
                            result.setLength(0);
                        }
                        case "(highlight)" -> {
                            style = style.withFormatting(Formatting.WHITE);
                            result.setLength(0);
                        }
                        case "(underline)" -> {
                            style = style.withFormatting(Formatting.UNDERLINE);
                            result.setLength(0);
                        }
                    }
                    formatting = false;
                }
            }
        }

        if (!result.isEmpty()) text.append(Text.literal(result.toString()).setStyle(style));

        return text;
    }
}
