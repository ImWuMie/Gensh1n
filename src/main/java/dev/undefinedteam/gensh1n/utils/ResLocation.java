package dev.undefinedteam.gensh1n.utils;

import dev.undefinedteam.gensh1n.Client;
import net.minecraft.util.Identifier;

public class ResLocation {
    public static Identifier of(String path) {
        return Identifier.of(Client.ASSETS_LOCATION, path);
    }
}
