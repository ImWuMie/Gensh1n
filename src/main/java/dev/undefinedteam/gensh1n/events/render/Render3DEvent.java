package dev.undefinedteam.gensh1n.events.render;

import dev.undefinedteam.gensh1n.events.SingleThreadEvent;
import dev.undefinedteam.gensh1n.render.world.Renderer3D;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Vector;

public class Render3DEvent implements SingleThreadEvent {
    private static final Render3DEvent INSTANCE = new Render3DEvent();

    public MatrixStack matrices;
    public float tickDelta;
    public Renderer3D renderer;

    public Vector<Runnable> postTasks = new Vector<>();

    public void post(Runnable runnable) {
        postTasks.add(runnable);
    }

    public static Render3DEvent get(MatrixStack matrices, float tickDelta) {
        INSTANCE.matrices = matrices;
        INSTANCE.tickDelta = tickDelta;
        INSTANCE.postTasks.clear();
        return INSTANCE;
    }

    @Override
    public <T extends SingleThreadEvent> T self() {
        return (T) this;
    }
}
