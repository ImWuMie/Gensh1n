package dev.undefinedteam.gensh1n.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.utils.misc.ISerializable;
import dev.undefinedteam.gensh1n.utils.render.color.RainbowColors;
import dev.undefinedteam.gensh1n.utils.render.color.SettingColor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Settings implements ISerializable<Settings>, Iterable<SettingGroup> {
    private SettingGroup defaultGroup;
    public final List<SettingGroup> groups = new ArrayList<>(1);

    public void onActivated() {
        for (SettingGroup group : groups) {
            for (Setting<?> setting : group) {
                setting.onActivated();
            }
        }
    }

    public Setting<?> get(String name) {
        for (SettingGroup sg : this) {
            for (Setting<?> setting : sg) {
                if (name.equalsIgnoreCase(setting.name)) return setting;
            }
        }

        return null;
    }

    public void reset() {
        for (SettingGroup group : groups) {
            for (Setting<?> setting : group) {
                setting.reset();
            }
        }
    }

    public SettingGroup getGroup(String name) {
        for (SettingGroup sg : this) {
            if (sg.name.equals(name)) return sg;
        }

        return null;
    }

    public int sizeGroups() {
        return groups.size();
    }

    public SettingGroup getDefaultGroup() {
        if (defaultGroup == null) defaultGroup = createGroup("General");
        return defaultGroup;
    }

    public SettingGroup createGroup(String name, boolean expanded) {
        SettingGroup group = new SettingGroup(name, expanded);
        groups.add(group);
        return group;
    }

    public SettingGroup createGroup(String name) {
        return createGroup(name, true);
    }

    public void registerColorSettings(Module module) {
        for (SettingGroup group : this) {
            for (Setting<?> setting : group) {
                setting.module = module;

                if (setting instanceof ColorSetting) {
                    RainbowColors.addSetting((Setting<SettingColor>) setting);
                }
            }
        }
    }

    public void unregisterColorSettings() {
        for (SettingGroup group : this) {
            for (Setting<?> setting : group) {
                if (setting instanceof ColorSetting) {
                    RainbowColors.removeSetting((Setting<SettingColor>) setting);
                }
            }
        }
    }

    @Override
    public Iterator<SettingGroup> iterator() {
        return groups.iterator();
    }

    @Override
    public JsonObject toTag() {
        JsonObject tag = new JsonObject();
        JsonArray array = new JsonArray();
        groups.forEach((group) -> {
            array.add(group.toTag());
        });

        tag.add("groups", array);

        return tag;
    }

    @Override
    public Settings fromTag(JsonObject tag) {
        JsonArray groupsTag = tag.getAsJsonArray("groups");

        for (JsonElement t : groupsTag) {
            JsonObject groupTag = t.getAsJsonObject();

            SettingGroup sg = getGroup(groupTag.get("name").getAsString());
            if (sg != null) sg.fromTag(groupTag);
        }

        return this;
    }
}
