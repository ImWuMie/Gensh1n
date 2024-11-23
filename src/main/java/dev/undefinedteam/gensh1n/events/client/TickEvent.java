package dev.undefinedteam.gensh1n.events.client;

import dev.undefinedteam.gensh1n.events.SingleThreadEvent;

public abstract class TickEvent implements SingleThreadEvent {
    public static class Pre extends TickEvent {
        private static final Pre INSTANCE = new Pre();

        @Override
        public <T extends SingleThreadEvent> T self() {
            return (T) INSTANCE;
        }

        public static Pre get() {
            return INSTANCE;
        }
    }

    public static class Post extends TickEvent {
        private static final Post INSTANCE = new Post();
        @Override
        public <T extends SingleThreadEvent> T self() {
            return (T) INSTANCE;
        }

        public static Post get() {
            return INSTANCE;
        }
    }

}
