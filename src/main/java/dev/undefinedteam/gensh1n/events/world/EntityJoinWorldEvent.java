package dev.undefinedteam.gensh1n.events.world;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityJoinWorldEvent {
    private static final EntityJoinWorldEvent INSTANCE = new EntityJoinWorldEvent();

    public Entity entity;
    public World world;

    public static EntityJoinWorldEvent get(Entity e, World world) {
        INSTANCE.entity = e;
        INSTANCE.world = world;
        return INSTANCE;
    }
}
