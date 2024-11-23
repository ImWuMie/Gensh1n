package dev.undefinedteam.gensh1n.system.hud;

import dev.undefinedteam.gensh1n.utils.Utils;

public class ElementInfo {
    public final String name, title, description;
    public Class<? extends HudElement> target;

    public ElementInfo(String name, String description, Class<? extends HudElement> target) {
        this.name = name;
        this.description = description;
        this.title = Utils.nameToTitle(name);
        this.target = target;
    }
}
