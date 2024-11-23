package dev.undefinedteam.gensh1n.system;

import dev.undefinedteam.gensh1n.gui.overlay.TNotifications;
import dev.undefinedteam.gensh1n.utils.chat.ChatUtils;
import net.minecraft.text.Text;

public class ChatAdapter {
    protected final int NLONG = TNotifications.LONG;
    protected final int NSHORT = TNotifications.SHORT;

    public final String title;

    public ChatAdapter(String title) {
        this.title = title;
    }

    public void info(Text message) {
        ChatUtils.forceNextPrefixClass(getClass());
        ChatUtils.sendMsg(title, message);
    }

    public void info(String message, Object... args) {
        ChatUtils.forceNextPrefixClass(getClass());
        ChatUtils.infoPrefix(title, message, args);
    }

    public void warning(String message, Object... args) {
        ChatUtils.forceNextPrefixClass(getClass());
        ChatUtils.warningPrefix(title, message, args);
    }

    public void error(String message, Object... args) {
        ChatUtils.forceNextPrefixClass(getClass());
        ChatUtils.errorPrefix(title, message, args);
    }

    protected void nInfo(String message,int duration,Object... args) {
        TNotifications.INSTANCE.info(message, duration, args);
    }

    protected void nWarn(String message,int duration,Object... args) {
        TNotifications.INSTANCE.warn(message, duration, args);
    }

    protected void nError(String message,int duration,Object... args) {
        TNotifications.INSTANCE.error(message, duration, args);
    }

    protected void nIdk(String message,int duration,Object... args) {
        TNotifications.INSTANCE.idk(message, duration, args);
    }
}
