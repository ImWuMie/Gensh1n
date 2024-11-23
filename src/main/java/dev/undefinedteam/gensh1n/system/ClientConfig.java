package dev.undefinedteam.gensh1n.system;

import com.google.gson.JsonObject;
import dev.undefinedteam.gclient.GCClient;
import dev.undefinedteam.gclient.packets.c2s.play.ChatMessageC2S;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.game.GameJoinedEvent;
import dev.undefinedteam.gensh1n.events.game.SendMessageEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.settings.Settings;
import dev.undefinedteam.gensh1n.utils.chat.ChatUtils;
import dev.undefinedteam.gensh1n.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;

import static dev.undefinedteam.gensh1n.Client.mc;

public class ClientConfig extends System<ClientConfig> implements SettingAdapter {
    public final Settings settings = new Settings();

//    private final SettingGroup sgDefault = settings.getDefaultGroup();
    private final SettingGroup sgUI = settings.createGroup("UI");
    private final SettingGroup sgCommand = settings.createGroup("Command");
    private final SettingGroup sgIRC = settings.createGroup("GIRC");
    private final SettingGroup sgPathFinder = settings.createGroup("PathFinder");

    public Setting<Boolean> msaa = bool(sgUI, "msaa-framebuffer", "USE MSAA SAMPLES: )", true);
    public Setting<Double> rainbowSpeed = doubleN(sgUI, "rainbow-speed", 10, 0, 10);
    public Setting<Boolean> renderSpectrum = bool(sgUI, "render-music-spectrum", true);
    public Setting<SettingColor> spectrumColor = color(sgUI, "music-spectrum-color", new SettingColor(156, 197, 255, 135));
    public Setting<Boolean> musicInfo = bool(sgUI, "render-music-info", true);

    public Setting<Boolean> calledByScreen = bool(sgCommand, "called-by-screen", "Should call commands' dispatch on overlay screen", false);
    public Setting<String> commandPrefix = text(sgCommand, "prefix", ".");

    public final Setting<Boolean> chatFeedback = bool(sgCommand, "chat-feedback", "Sends chat feedback when client performs certain actions.", true);
    public final Setting<Boolean> deleteChatFeedback = bool(sgCommand, "delete-chat-feedback", "Delete previous matching chat feedback to keep chat clear.", true, chatFeedback::get);

    public final Setting<String> prefix = text(sgIRC, "chat-irc-prefix", "@");
    public final Setting<Integer> reconnectDelay = intN(sgIRC, "reconnect-irc-delay", 5000, 3000, 30000);

    public Setting<Boolean> fastAStar = bool(sgPathFinder, "fast-astar", true);

//    public final FernFlowerConfig fernFlower = new FernFlowerConfig(this.settings);

    public ClientConfig() {
        super("config");
    }

    public static ClientConfig get() {
        return Systems.get(ClientConfig.class);
    }

    @EventHandler
    private void onChat(SendMessageEvent e) {
        var p = this.prefix.get();
        var msg = e.message;
        if (msg.startsWith(p)) {
            var text = msg.substring(p.length());
            if (GCClient.get().session() != null) {
                GCClient.get().session().send(new ChatMessageC2S(text));
            } else ChatUtils.error("GChat未连接");
            e.cancel();
        }
    }

    @EventHandler
    private void onJoin(GameJoinedEvent e) {
        if (GCClient.get().session() != null) {
            try {
                var profile = mc.player == null ? mc.getGameProfile() : mc.player.getGameProfile();
                GCClient.get().session().onJoinGame(profile);
            } catch (Exception i) {
                i.printStackTrace();
            }
        }
    }

    @Override
    public JsonObject toTag() {
        JsonObject tag = new JsonObject();

        tag.addProperty("version", Client.VERSION);
        tag.add("settings", settings.toTag());

        return tag;
    }

    @Override
    public ClientConfig fromTag(JsonObject tag) {
        if (tag.has("settings")) settings.fromTag(tag.getAsJsonObject("settings"));

        return this;
    }
}
