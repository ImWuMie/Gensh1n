package dev.undefinedteam.gensh1n.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.utils.misc.ISerializable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SettingGroup implements ISerializable<SettingGroup>, Iterable<Setting<?>> {
    public final String name;
    public boolean sectionExpanded;

    public final List<Setting<?>> settings = new ArrayList<>(1);

    public SettingGroup(String name) {
        this(name, false);
    }

    public SettingGroup(String name, boolean sectionExpanded) {
        this.name = name;
        this.sectionExpanded = sectionExpanded;
    }

    public Setting<?> get(String name) {
        for (Setting<?> setting : this) {
            if (setting.name.equals(name)) return setting;
        }

        return null;
    }

    public <T> Setting<T> add(Setting<T> setting) {
        settings.add(setting);

        return setting;
    }

    public Setting<?> getByIndex(int index) {
        return settings.get(index);
    }

    @Override
    public Iterator<Setting<?>> iterator() {
        return settings.iterator();
    }

    @Override
    public JsonObject toTag() {
        JsonObject tag = new JsonObject();

        tag.addProperty("name", name);
        tag.addProperty("sectionExpanded", sectionExpanded);

        JsonArray settingsTag = new JsonArray();
        for (Setting<?> setting : this) if (setting.wasChanged()) settingsTag.add(setting.toTag());
        tag.add("settings", settingsTag);

        return tag;
    }

    @Override
    public SettingGroup fromTag(JsonObject tag) {
        sectionExpanded = tag.get("sectionExpanded").getAsBoolean();

        JsonArray settingsTag = tag.getAsJsonArray("settings");
        for (JsonElement t : settingsTag) {
            JsonObject settingTag = t.getAsJsonObject();

            Setting<?> setting = get(settingTag.get("name").getAsString());
            if (setting != null) setting.fromTag(settingTag);
        }

        return this;
    }
}
