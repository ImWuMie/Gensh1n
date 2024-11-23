package dev.undefinedteam.gensh1n.gui;

import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.events.render.Render2DBeforeChat;
import dev.undefinedteam.gensh1n.gui.overlay.TNotifications;
import dev.undefinedteam.gensh1n.gui.overlay.ROverlayGui;
import dev.undefinedteam.gensh1n.render.Renderer;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import meteordevelopment.orbit.EventHandler;

import static dev.undefinedteam.gensh1n.Client.mc;

public class OverlayRenderer {
    public static OverlayRenderer INSTANCE;
    public final Int2ObjectArrayMap<ROverlayGui> overlays = new Int2ObjectArrayMap<>();

    public void init() {
        Client.EVENT_BUS.subscribe(this);
        addOverlay(new TNotifications());
        INSTANCE = this;
    }

    protected void addOverlay(ROverlayGui overlay) {
        this.overlays.put(overlays.size() + 1, overlay);
    }

    @EventHandler
    private void onRender2D(Render2DBeforeChat e) {
        loopRender(Renderer.MAIN, e.width, e.height, e.tickDelta);
    }

    @EventHandler
    private void onTick(TickEvent.Pre e) {
        tick();
    }

    public void loopRender(Renderer renderer, int width, int height, float tickDelta) {
        overlays.forEach((id, overlay) -> {
            mc.getProfiler().push(Client.LC_NAME + "_r_overlay_" + overlay.name + "_" + id);
            overlay.setup(width, height);
            overlay.render(renderer, tickDelta);
            mc.getProfiler().pop();
        });
    }

    public void tick() {
        overlays.forEach((id, overlay) -> {
            mc.getProfiler().push(Client.LC_NAME + "_t_overlay_" + overlay.name + "_" + id);
            overlay.tick();
            mc.getProfiler().pop();
        });
    }
}
