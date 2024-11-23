package dev.undefinedteam.gensh1n.settings;

import dev.undefinedteam.gensh1n.render.ShapeMode;
import dev.undefinedteam.gensh1n.system.SettingAdapter;
import dev.undefinedteam.gensh1n.utils.StringUtils;
import dev.undefinedteam.gensh1n.utils.render.color.SettingColor;

public class ColorSettings implements SettingAdapter {
    public final String name;
    public final Setting<ShapeMode> shape;
    public final Setting<SettingColor> side;
    public final Setting<SettingColor> line;

    public ColorSettings(String name, SettingGroup group, IVisible visible) {
        this.name = name;
        shape = choice(group,
            name + "-shape-mode",
            StringUtils.getReplaced("How the shape for the '{}' is rendered.", name),
            ShapeMode.Both, visible);
        side = color(group,
            name + "-side-color",
            StringUtils.getReplaced("The color of the sides for the '{}' shape.", name),
            new SettingColor(255, 255, 255, 20), visible);
        line = color(group,
            name + "-line-color",
            StringUtils.getReplaced("The color of the lines for the '{}' shape.", name),
            new SettingColor(255, 255, 255, 80), visible);
    }

    public ColorSettings side(SettingColor color) {
        side.defaultValue = color;
        return this;
    }

    public ColorSettings line(SettingColor color) {
        line.defaultValue = color;
        return this;
    }
}
