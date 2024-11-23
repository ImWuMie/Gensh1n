package dev.undefinedteam.gensh1n.events.render;

import dev.undefinedteam.gensh1n.events.SingleThreadEvent;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class Render2DAfterHotbar implements SingleThreadEvent {
    private static final Render2DAfterHotbar INSTANCE = new Render2DAfterHotbar();

    public DrawContext drawContext;
    public int width, height, scaledWidth, scaledHeight;
    public float tickDelta;

    public List<Runnable> postTasks = new ArrayList<>();

    public static Render2DAfterHotbar get(DrawContext drawContext, int w, int h, float tickDelta) {
        INSTANCE.drawContext = drawContext;
        INSTANCE.width = w;
        INSTANCE.height = h;
        INSTANCE.tickDelta = tickDelta;
        INSTANCE.postTasks.clear();
        return INSTANCE;
    }

    public void post(Runnable task) {
        this.postTasks.add(task);
    }

    @Override
    public <T extends SingleThreadEvent> T self() {
        return (T) this;
    }
}
