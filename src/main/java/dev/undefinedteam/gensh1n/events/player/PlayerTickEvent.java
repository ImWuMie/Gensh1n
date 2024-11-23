package dev.undefinedteam.gensh1n.events.player;

import dev.undefinedteam.gensh1n.events.Cancellable;

public class PlayerTickEvent extends Cancellable {
    private static final PlayerTickEvent INSTANCE = new PlayerTickEvent();

    public static PlayerTickEvent get() {
        INSTANCE.setCancelled(false);
        return INSTANCE;
    }

    public static class Post {
        public static final Post INSTANCE = new Post();
    }
}
