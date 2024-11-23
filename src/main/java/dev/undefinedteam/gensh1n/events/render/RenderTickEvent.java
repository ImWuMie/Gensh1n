package dev.undefinedteam.gensh1n.events.render;

import dev.undefinedteam.gensh1n.events.SingleThreadEvent;

public abstract class RenderTickEvent implements SingleThreadEvent {
    public static class Start extends RenderTickEvent {
        private static final Start INSTANCE = new Start();

        @Override
        public <T extends SingleThreadEvent> T self() {
            return (T) INSTANCE;
        }

        public static Start get() {
            return INSTANCE;
        }
    }

    public static class End extends RenderTickEvent {
        private static final End INSTANCE = new End();

        @Override
        public <T extends SingleThreadEvent> T self() {
            return (T) INSTANCE;
        }

        public static End get() {
            return INSTANCE;
        }
    }
}
