package dev.undefinedteam.gensh1n.events.world;

import net.minecraft.client.world.ClientWorld;

public class WorldChangeEvent {
    private static final WorldChangeEvent INSTANCE = new WorldChangeEvent();

    public ClientWorld world;

    public static WorldChangeEvent get(ClientWorld world) {
        INSTANCE.world = world;
        return INSTANCE;
    }
}
