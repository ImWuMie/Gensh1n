package dev.undefinedteam.gensh1n.system.modules;

import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import net.minecraft.item.Items;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.StringEncryption;


@StringEncryption
@ControlFlowObfuscation
public class Categories {
    public static final Category Combat;
    public static final Category Movement;
    public static final Category Render;
    public static final Category Player;
    public static final Category World;
    public static final Category Misc;

    public static boolean REGISTERING;

    static {
        Combat = new Category("Combat", !Client.isOnMinecraftEnv() ? null : Items.DIAMOND_SWORD, new java.awt.Color(225, 0, 0, 255).getRGB(), "a");
        Movement = new Category("Movement", !Client.isOnMinecraftEnv() ? null : Items.DIAMOND_BOOTS, new java.awt.Color(0, 125, 255, 255).getRGB(), "b");
        Render = new Category("Render", !Client.isOnMinecraftEnv() ? null : Items.TINTED_GLASS, new java.awt.Color(125, 255, 255, 255).getRGB(), "c");
        Player = new Category("Player", !Client.isOnMinecraftEnv() ? null : Items.ARMOR_STAND, new java.awt.Color(245, 255, 100, 255).getRGB(), "d");
        World = new Category("World", !Client.isOnMinecraftEnv() ? null : Items.GRASS_BLOCK, new java.awt.Color(0, 150, 0, 255).getRGB(), "e");
        Misc = new Category("Misc", !Client.isOnMinecraftEnv() ? null : Items.NETHER_STAR, new java.awt.Color(0, 50, 175, 255).getRGB(), "f");
    }

    public static void init() {
        REGISTERING = true;

        Modules.registerCategory(Combat);
        Modules.registerCategory(Movement);
        Modules.registerCategory(Render);
        Modules.registerCategory(Player);
        Modules.registerCategory(World);
        Modules.registerCategory(Misc);

        REGISTERING = false;
    }

    public static Category byName(String name) {
        if (name == null) return Combat;

        for (Category loopCategory : Modules.loopCategories()) {
            if (loopCategory.name.equals(name)) return loopCategory;
        }
        return Combat;
    }
}
