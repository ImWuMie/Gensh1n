package dev.undefinedteam.gensh1n.utils.render.color;

import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.system.ClientConfig;
import dev.undefinedteam.gensh1n.utils.misc.UnorderedArrayList;
import meteordevelopment.orbit.EventHandler;

import java.util.List;


public class RainbowColors {
    private static final List<Setting<SettingColor>> colorSettings = new UnorderedArrayList<>();
    private static final List<Setting<List<SettingColor>>> colorListSettings = new UnorderedArrayList<>();

    private static final List<SettingColor> colors = new UnorderedArrayList<>();
    private static final List<Runnable> listeners = new UnorderedArrayList<>();

    public static final RainbowColor GLOBAL = new RainbowColor();

    public static void init() {
        Client.EVENT_BUS.subscribe(RainbowColors.class);
    }

    public static void addSetting(Setting<SettingColor> setting) {
        colorSettings.add(setting);
    }

    public static void addSettingList(Setting<List<SettingColor>> setting) {
        colorListSettings.add(setting);
    }

    public static void removeSetting(Setting<SettingColor> setting) {
        colorSettings.remove(setting);
    }

    public static void removeSettingList(Setting<List<SettingColor>> setting) {
        colorListSettings.remove(setting);
    }

    public static void add(SettingColor color) {
        colors.add(color);
    }

    public static void register(Runnable runnable) {
        listeners.add(runnable);
    }

    @EventHandler
    private static void onTick(TickEvent.Post event) {
        GLOBAL.setSpeed(ClientConfig.get().rainbowSpeed.get() / 100);
        GLOBAL.getNext();

        for (Setting<SettingColor> setting : colorSettings) {
            if (setting.module == null || setting.module.isActive()) setting.get().update();
        }

        for (Setting<List<SettingColor>> setting : colorListSettings) {
            if (setting.module == null || setting.module.isActive()) {
                for (SettingColor color : setting.get()) color.update();
            }
        }

        for (SettingColor color : colors) {
            color.update();
        }

        for (Runnable listener : listeners) listener.run();
    }
}
