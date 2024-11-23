package dev.undefinedteam.gensh1n.system.modules;

import dev.undefinedteam.gensh1n.utils.Utils;
import net.minecraft.item.Item;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
public class Category {
    public final String name, title, iconName;
    public final Item icon;
    public final int color;
    private final int nameHash;

    public Category(String name, Item icon, int color, String iconName) {
        this.name = name;
        this.title = Utils.nameToTitle(name);
        this.nameHash = name.hashCode();
        this.icon = icon;
        this.color = color;
        this.iconName = iconName;
    }

    public Category(String name, Item icon, int color) {
        this(name, icon, color, "");
    }

    public Category(String name, int color) {
        this(name, null, color);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return nameHash == category.nameHash;
    }

    @Override
    public int hashCode() {
        return nameHash;
    }
}
